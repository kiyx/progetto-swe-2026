package model.dto.request;

import model.dto.enums.StatoProgetto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProgettoRequestDTO
{
    @NotBlank(message = "Il nome del progetto è obbligatorio")
    private String nome;

    @NotNull(message = "Lo stato del progetto è obbligatorio")
    private StatoProgetto stato;

    @NotNull(message = "Il progetto deve appartenere a un team")
    private Long idTeam;

    @NotNull(message = "Il progetto deve essere creato da un admin")
    private Long idAdmin;
}