package dev.parthenodevs.bugboard.backend.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyUsedException extends RuntimeException
{
    public EmailAlreadyUsedException(String message)
    {
        super(message);
    }
}