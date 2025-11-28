package dev.parthenodevs.bugboard.backend.dto.response;

import lombok.*;

@Data
@Builder
public class UtenteResponseDTO
{
    private Long id;
    private String email;
    private String nome;
    private String cognome;
    private String password;
    private Boolean isAdmin;

    public String getNomeCompleto()
    {
        return nome + " " + cognome;
    }
}