package dev.parthenodevs.bugboard.backend.dto.request;

import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIssueRequestDTO
{
    @NotBlank(message = "Il titolo è obbligatorio.")
    @Size(min = 2, max = 200, message = "Il titolo deve avere una lunghezza compresa tra 2 e 200.")
    private String titolo;

    @NotBlank(message = "La descrizione è obbligatoria.")
    @Size(min = 2, message = "La descrizione deve avere una lunghezza maggiore di 2.")
    private String descrizione;

    @NotBlank(message = "Il tipo issue è obbligatorio.")
    private TipoIssue tipo;

    @NotBlank(message = "Inserire se la issue è stata archiviata è obbligatorio.")
    private boolean isArchiviato;

    private TipoPriorita priorita;

    @Size(max = 255, message = "L'immagine deve essere una stringa di max 255")
    private String immagine;

    @NotBlank(message = "Il progetto è obbligatorio\"")
    private Long idProgetto;

    @NotBlank(message = "L'autore è obbligatorio")
    private Long idAutore;
}