package dev.parthenodevs.bugboard.backend.exception;

public class InvalidFieldException extends RuntimeException
{
    public InvalidFieldException(String message)
    {
        super(message);
    }
}
