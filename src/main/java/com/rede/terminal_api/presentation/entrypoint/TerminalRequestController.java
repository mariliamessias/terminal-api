package com.rede.terminal_api.presentation.entrypoint;


import com.rede.terminal_api.application.usecase.CreateTerminalRequestUseCase;
import com.rede.terminal_api.application.usecase.GetTerminalRequestUseCase;
import com.rede.terminal_api.presentation.entrypoint.request.CreateTerminalRequest;
import com.rede.terminal_api.presentation.entrypoint.response.CreateTerminalRequestResponse;
import com.rede.terminal_api.presentation.entrypoint.response.GetTerminalRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terminal-requests")
public class TerminalRequestController {

    private final CreateTerminalRequestUseCase createTerminalRequestUseCase;
    private final GetTerminalRequestUseCase getTerminalRequestUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTerminalRequestResponse create(@Valid @RequestBody CreateTerminalRequest request) {
        var result = createTerminalRequestUseCase.execute(request.toDomain());
        return CreateTerminalRequestResponse.from(result);
    }

    @GetMapping("/{id}")
    public GetTerminalRequestResponse findById(@PathVariable UUID id) {
        var result = getTerminalRequestUseCase.execute(id);
        return GetTerminalRequestResponse.from(result);
    }
}