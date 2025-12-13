package dev.parthenodevs.bugboard.backend.exception;

import io.jsonwebtoken.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.logging.*;

@RestControllerAdvice
@SuppressWarnings("NullableProblems")
public class GlobalExceptionHandler
{
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

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

    @ExceptionHandler({UserNotFoundException.class, PasswordNotMatchingException.class})
    public ResponseEntity<ErrorDTO> handleAuthFailure(RuntimeException ex)
    {
        LOGGER.log(Level.WARNING, ex, () -> "Tentativo di login fallito: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Autenticazione Fallita",
                "Credenziali non valide."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDTO> handleJwtAuthenticationException(RuntimeException ex)
    {
        LOGGER.log(Level.WARNING, ex, () -> "Tentativo di accesso con Token non valido o scaduto: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Autenticazione Fallita",
                "Accesso negato: Il token JWT non è valido o è scaduto."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}