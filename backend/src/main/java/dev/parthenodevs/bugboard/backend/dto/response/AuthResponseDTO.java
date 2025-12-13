package dev.parthenodevs.bugboard.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO
{
    private String jwtToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private UtenteResponseDTO utente;
}