package com.rede.terminal_api.presentation.handler;

import com.rede.terminal_api.domain.exception.TerminalRequestNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(TerminalRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTerminalRequestNotFound(TerminalRequestNotFoundException exception) {

        var response = new ErrorResponse(
                "TERMINAL_REQUEST_NOT_FOUND",
                exception.getMessage(),
                LocalDateTime.now()

        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        var response = new ErrorResponse(
                "INVALID_REQUEST",
                "Invalid request parameter: " + exception.getName(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException exception
    ) {

        var response = new ErrorResponse(
                "INVALID_REQUEST",
                Objects.requireNonNull(exception.getBindingResult()
                                .getFieldError())
                        .getDefaultMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {

        var response = new ErrorResponse(
                "INVALID_REQUEST",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(
            Exception exception) {
        log.error("Unexpected error", exception);

        var response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);

    }

}