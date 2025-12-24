package dev.parthenodevs.bugboard.backend.exception;

import java.time.LocalDateTime;

public record ErrorDTO
                    (
                        LocalDateTime timestamp,
                        int status,
                        String error,
                        String message
                    ) {}