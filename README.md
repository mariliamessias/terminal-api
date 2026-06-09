# Terminal Request API

API para o case técnico de **Reserva de Terminal POS**.

A aplicação recebe uma solicitação de terminal POS, cria o registro com status `SOLICITADO` e processa o fluxo de forma assíncrona por evento interno.

```text
SOLICITADO -> VALIDADO -> RESERVADO -> AGENDADO
```

Estados de falha de negócio:

```text
REJEITADO
ERRO_RESERVA
ERRO_AGENDAMENTO
```

Também foram simuladas falhas técnicas de integração. Nesses casos, o status permanece no último estado persistido.

---

## Tecnologias

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Postman Collection

---

## Arquitetura

A solução está organizada em camadas:

```text
presentation
application
domain
infrastructure
```

O processamento da solicitação é realizado por um workflow com steps encadeados:

```text
ValidateCustomerStep
        ↓
ReserveTerminalStep
        ↓
ScheduleDeliveryStep
```

Cada etapa executa sua regra, altera o status da solicitação quando necessário e o workflow persiste o estado antes de avançar.

---

## Processamento Assíncrono

Ao chamar o endpoint de criação:

```http
POST /terminal-requests
```

a aplicação:

1. Cria a solicitação com status `SOLICITADO`.
2. Persiste a solicitação.
3. Publica um evento interno.
4. Um listener processa o fluxo de forma assíncrona.
5. O status final deve ser consultado via `GET`.

Por isso, o `POST` retorna sempre o estado inicial:

```text
SOLICITADO
```

---

## Como executar a aplicação

Na raiz do projeto:

```bash
./gradlew clean bootRun
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

---

## H2 Database

Console do H2:

```text
http://localhost:8080/h2-console
```

Configuração:

```text
JDBC URL: jdbc:h2:mem:terminaldb
User: sa
Password: deixe vazio
```

Consulta útil:

```sql
SELECT * FROM terminal_requests;
```

---

## Como executar os testes automatizados

```bash
./gradlew test
```

---

## Collection Postman

Importe o arquivo:

```text
Terminal Request Case.postman_collection.json
```

A collection possui os cenários de teste do case técnico.

Variáveis utilizadas:

```text
baseUrl = http://localhost:8080
terminalRequestId = vazio inicialmente
```

Fluxo recomendado para validar cada cenário:

1. Execute um dos cenários `POST`.
2. Copie o campo `id` retornado.
3. Preencha a variável `terminalRequestId`.
4. Execute `09 - Consultar Solicitação`.
5. Valide o status final esperado.

---

## Endpoints

### Criar solicitação

```http
POST /terminal-requests
```

#### Request

```json
{
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "address": {
    "street": "Rua Exemplo",
    "number": "100",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01000-000"
  }
}
```

#### Response

```http
201 Created
```

```json
{
  "id": "f479d1d5-a88c-4190-8de7-80a197b4df90",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "SOLICITADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### Consultar solicitação

```http
GET /terminal-requests/{id}
```

#### Response para ID existente

```http
200 OK
```

```json
{
  "id": "c690ba6a-f10a-43d0-89a0-e34d21e99d39",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "AGENDADO",
  "createdAt": "2026-06-09T17:22:23.164666"
}
```

#### Response para ID inexistente

```http
404 Not Found
```

```json
{
  "code": "TERMINAL_REQUEST_NOT_FOUND",
  "message": "Terminal request not found: 9552d10a-e05a-4fa6-996d-8de7baf84bfa",
  "timestamp": "2026-06-09T17:25:55.304014"
}
```

---

## Regras simuladas nas integrações HTTP

### Customer Service

| customerId | Resultado |
|---|---|
| `CUST-VALID` | Cliente encontrado e ativo |
| `CUST-INACTIVE` | Cliente encontrado, porém inativo |
| `CUST-NOT-FOUND` | Cliente inexistente |
| `CUST-INTEGRATION-FAIL` | Falha técnica na integração |

### Terminal Reservation Service

| terminalType | Resultado |
|---|---|
| `POS_WIFI` | Terminal disponível |
| `POS_SMART` | Terminal indisponível |
| `POS_CHIP` | Falha técnica na integração |

### Logistics Service

| state | Resultado |
|---|---|
| `AM` | Falha de negócio no agendamento |
| `RR` | Falha técnica na integração |
| Qualquer outro estado | Agendamento realizado com sucesso |

---

## Cenários da collection

| Request | Resultado final esperado | Descrição |
|---|---:|---|
| `01 - AGENDADO` | `AGENDADO` | Cliente válido, terminal disponível e logística disponível. |
| `02 - SOLICITADO - (Cliente Falha na Integração com Serviço)` | `SOLICITADO` | Falha técnica ao consultar o serviço de clientes. |
| `03 - REJEITADO (Cliente Inativo)` | `REJEITADO` | Cliente encontrado, porém inativo. |
| `04 - REJEITADO (Cliente Não Encontrado)` | `REJEITADO` | Cliente inexistente. |
| `05 - VALIDADO - (Reserva Falha na Integração com Serviço)` | `VALIDADO` | Cliente validado, mas ocorre falha técnica na reserva. |
| `06 - ERRO_RESERVA (Reserva Não Encontrada)` | `ERRO_RESERVA` | Cliente válido, mas não há terminal disponível. |
| `07 - RESERVADO - (Agendamento Falha na Integração com Serviço)` | `RESERVADO` | Cliente validado e terminal reservado, mas ocorre falha técnica na logística. |
| `08- ERRO_AGENDAMENTO` | `ERRO_AGENDAMENTO` | Cliente validado e terminal reservado, mas a logística retorna falha de negócio. |
| `09 - Consultar Solicitação` | `200 OK` | Consulta uma solicitação existente usando `terminalRequestId`. |
| `10 - Solicitação Não Encontrada` | `404 Not Found` | Consulta uma solicitação inexistente. |

---

## Exemplos com cURL

### 01 - AGENDADO

Cliente válido, terminal disponível e logística disponível.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-VALID",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta inicial esperada:

```http
201 Created
```

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "SOLICITADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

Após consultar por ID:

```bash
curl -X GET http://localhost:8080/terminal-requests/UUID_GERADO
```

Resposta final esperada:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "AGENDADO",
  "createdAt": "2026-06-09T17:22:23.164666"
}
```

---

### 02 - SOLICITADO - Cliente Falha na Integração com Serviço

Falha técnica ao consultar o serviço de clientes. A solicitação permanece no último estado persistido: `SOLICITADO`.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-INTEGRATION-FAIL",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-INTEGRATION-FAIL",
  "terminalType": "POS_WIFI",
  "status": "SOLICITADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 03 - REJEITADO - Cliente Inativo

Cliente encontrado, porém inativo.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-INACTIVE",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-INACTIVE",
  "terminalType": "POS_WIFI",
  "status": "REJEITADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 04 - REJEITADO - Cliente Não Encontrado

Cliente inexistente no serviço externo simulado.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-NOT-FOUND",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-NOT-FOUND",
  "terminalType": "POS_WIFI",
  "status": "REJEITADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 05 - VALIDADO - Reserva Falha na Integração com Serviço

Cliente validado, mas ocorre falha técnica na integração de reserva. A solicitação permanece no último estado persistido: `VALIDADO`.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-VALID",
    "terminalType": "POS_CHIP",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_CHIP",
  "status": "VALIDADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 06 - ERRO_RESERVA - Reserva Não Encontrada

Cliente válido, mas não há terminal disponível para o tipo solicitado.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-VALID",
    "terminalType": "POS_SMART",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_SMART",
  "status": "ERRO_RESERVA",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 07 - RESERVADO - Agendamento Falha na Integração com Serviço

Cliente validado e terminal reservado, mas ocorre falha técnica na logística. A solicitação permanece no último estado persistido: `RESERVADO`.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-VALID",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "Boa Vista",
      "state": "RR",
      "zipCode": "69300-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "RESERVADO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 08 - ERRO_AGENDAMENTO

Cliente validado e terminal reservado, mas a logística retorna falha de negócio para o agendamento.

```bash
curl -X POST http://localhost:8080/terminal-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-VALID",
    "terminalType": "POS_WIFI",
    "address": {
      "street": "Rua Exemplo",
      "number": "100",
      "city": "Manaus",
      "state": "AM",
      "zipCode": "69000-000"
    }
  }'
```

Resposta final esperada após consultar por ID:

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "ERRO_AGENDAMENTO",
  "createdAt": "2026-06-09T16:59:47.947104"
}
```

---

### 09 - Consultar Solicitação

Use o ID retornado por qualquer cenário `POST`.

```bash
curl -X GET http://localhost:8080/terminal-requests/UUID_GERADO
```

Resposta esperada:

```http
200 OK
```

```json
{
  "id": "UUID_GERADO",
  "customerId": "CUST-VALID",
  "terminalType": "POS_WIFI",
  "status": "AGENDADO",
  "createdAt": "2026-06-09T17:22:23.164666"
}
```

---

### 10 - Solicitação Não Encontrada

```bash
curl -X GET http://localhost:8080/terminal-requests/9552d10a-e05a-4fa6-996d-8de7baf84bfa
```

Resposta esperada:

```http
404 Not Found
```

```json
{
  "code": "TERMINAL_REQUEST_NOT_FOUND",
  "message": "Terminal request not found: 9552d10a-e05a-4fa6-996d-8de7baf84bfa",
  "timestamp": "2026-06-09T17:25:55.304014"
}
```

---

## Observação sobre falhas técnicas

As falhas técnicas foram modeladas de forma diferente das falhas de negócio.

Quando uma integração retorna falha de negócio, o status da solicitação é atualizado:

```text
Cliente inativo/inexistente -> REJEITADO
Terminal indisponível -> ERRO_RESERVA
Logística indisponível -> ERRO_AGENDAMENTO
```

Quando ocorre uma exception técnica, o status permanece no último estado persistido:

```text
Falha técnica no cliente -> SOLICITADO
Falha técnica na reserva -> VALIDADO
Falha técnica na logística -> RESERVADO
```

Essa distinção demonstra que a aplicação diferencia erro esperado de negócio e falha técnica de integração.

---

## Melhorias futuras

- Retry para integrações externas
- Mensageria com RabbitMQ ou Kafka
- Banco relacional persistente
- Observabilidade com métricas e tracing
- Circuit breaker para integrações externas
- Testes de integração automatizados para os fluxos assíncronos
