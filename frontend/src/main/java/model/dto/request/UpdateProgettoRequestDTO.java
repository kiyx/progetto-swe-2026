package model.dto.request;

import model.dto.enums.StatoProgetto;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgettoRequestDTO
{
    @Size(min = 2, max = 100, message = "Il nome deve avere tra 2 e 100 caratteri")
    private String nome;

    private StatoProgetto stato;
}