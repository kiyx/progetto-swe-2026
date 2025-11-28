package dev.parthenodevs.bugboard.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO
{
    // Stringa token per JWT
    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private UtenteResponseDTO utente;
}