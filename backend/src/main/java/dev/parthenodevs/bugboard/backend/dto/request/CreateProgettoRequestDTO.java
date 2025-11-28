package dev.parthenodevs.bugboard.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProgettoRequestDTO
{
    @NotBlank(message = "Il nome del progetto è obbligatorio")
    private String nome;

    @NotNull(message = "Il progetto deve appartenere a un team")
    private Long idTeam;

    @NotNull(message = "Il progetto deve essere creato da un admin")
    private Long idAdmin;
}