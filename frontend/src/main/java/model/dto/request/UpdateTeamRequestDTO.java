package model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamRequestDTO
{
    @Size(min = 2, max = 100, message = "Il nome deve avere una lunghezza compresa tra 2 e 100.")
    private String nome;
}