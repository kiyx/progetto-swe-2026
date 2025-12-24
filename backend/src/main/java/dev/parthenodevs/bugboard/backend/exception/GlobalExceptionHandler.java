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
        LOGGER.log(Level.WARNING, () -> "Tentativo di login fallito: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Autenticazione Fallita",
                "Credenziali non valide."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDTO> handleExpiredJwtException(ExpiredJwtException ex)
    {
        LOGGER.log(Level.INFO, "Token JWT scaduto.");

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Token Scaduto",
                "La sessione è scaduta. Effettua nuovamente il login."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorDTO> handleInvalidJwtException(JwtException ex)
    {
        LOGGER.log(Level.WARNING, "Token JWT malformato.");

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Token Non Valido",
                "Il token fornito non è valido."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDTO> handleGenericJwtException(JwtException ex)
    {
        LOGGER.log(Level.WARNING, () -> "Errore JWT generico: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Errore Autenticazione",
                "Impossibile verificare l'identità."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFound(ResourceNotFoundException ex)
    {
        LOGGER.log(Level.INFO, "Risorsa non trovata: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Risorsa Non Trovata",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmailAlreadyUsedException.class, BusinessLogicException.class})
    public ResponseEntity<ErrorDTO> handleConflicts(RuntimeException ex)
    {
        LOGGER.log(Level.WARNING, ()-> "Conflitto dati: " + ex.getMessage());

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflitto",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex)
    {
        LOGGER.log(Level.SEVERE, "Errore interno imprevisto", ex);

        ErrorDTO errorResponse = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore Interno",
                "Si è verificato un errore imprevisto nel server."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}