package dev.parthenodevs.bugboard.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class TeamRequestDTO
{
    @NotBlank(message = "Il nome è obbligatorio.")
    @Size(min = 2, max = 100, message = "Il nome deve avere una lunghezza compresa tra 2 e 100.")
    private String nome;

    @NotBlank(message = "l'id dell'admin è obbligatorio.")
    private Long idAdmin;
}
