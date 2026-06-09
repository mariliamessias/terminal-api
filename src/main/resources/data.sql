INSERT INTO terminal_requests (
    id,
    customer_id,
    terminal_type,
    street,
    number,
    city,
    state,
    zip_code,
    status,
    created_at,
    updated_at
) VALUES (
             '11111111-1111-1111-1111-111111111111',
             'CUST-123',
             'POS_WIFI',
             'Rua Exemplo',
             '100',
             'São Paulo',
             'SP',
             '01000-000',
             'AGENDADO',
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP
         );