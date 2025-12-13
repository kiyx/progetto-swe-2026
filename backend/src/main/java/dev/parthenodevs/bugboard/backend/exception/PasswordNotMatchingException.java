package dev.parthenodevs.bugboard.backend.exception;

public class PasswordNotMatchingException extends RuntimeException
{
    public PasswordNotMatchingException(String message) {
        super(message);
    }
}
