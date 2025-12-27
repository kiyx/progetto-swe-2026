package model.dto.request;

import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIssueRequestDTO
{
    @NotBlank(message = "Il titolo è obbligatorio.")
    @Size(min = 2, max = 200, message = "Il titolo deve avere una lunghezza compresa tra 2 e 200.")
    private String titolo;

    @NotBlank(message = "La descrizione è obbligatoria.")
    @Size(min = 2, message = "La descrizione deve avere una lunghezza maggiore di 2.")
    private String descrizione;

    @NotNull(message = "Il tipo issue è obbligatorio.")
    private TipoIssue tipo;

    private Boolean isArchiviato;

    private TipoPriorita priorita;
    private String immagine;

    @NotNull(message = "Il progetto è obbligatorio")
    private Long idProgetto;
}