package dev.parthenodevs.bugboard.backend.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessLogicException extends RuntimeException
{
    public BusinessLogicException(String message)
    {
        super(message);
    }
}