package dev.parthenodevs.bugboard.backend.dto.response;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenteResponseDTO
{
    private Long id;
    private String email;
    private String nome;
    private String cognome;
    private Boolean isAdmin;

    @JsonIgnore
    public String getNomeCompleto()
    {
        return nome + " " + cognome;
    }
}