package dev.parthenodevs.bugboard.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    // Handler per errori di validazione (protezione ulteriore oltre alla validazione Frontend)
    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ErrorDTO> handleInvalidFieldException(InvalidFieldException ex)
    {
        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Errore di Validazione",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}