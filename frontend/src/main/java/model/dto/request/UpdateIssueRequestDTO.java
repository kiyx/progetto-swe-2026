package model.dto.request;

import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIssueRequestDTO
{
    @Size(min = 2, max = 200, message = "Il titolo deve avere una lunghezza compresa tra 2 e 200.")
    private String titolo;

    @Size(min = 2, message = "La descrizione deve avere una lunghezza maggiore di 2.")
    private String descrizione;

    private TipoIssue tipo;
    private StatoIssue stato;
    private Boolean isArchiviato;

    private TipoPriorita priorita;
    private String immagine;
}