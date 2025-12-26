package dev.parthenodevs.bugboard.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamRequestDTO
{
    @NotBlank(message = "Il nome del team è obbligatorio.")
    @Size(min = 2, max = 100, message = "Il nome deve avere una lunghezza compresa tra 2 e 100.")
    private String nome;

}