package dev.parthenodevs.bugboard.backend.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue)
    {
        super(String.format("%s non trovato con %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}