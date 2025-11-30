package dev.parthenodevs.bugboard.backend.dto.request;

import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class IssueRequestDTO
{
    @NotBlank(message = "Il titolo è obbligatorio.")
    @Size(min = 2, max = 200, message = "Il titolo deve avere una lunghezza compresa tra 2 e 200.")
    private String titolo;

    @NotBlank(message = "La descrizione è obbligatoria.")
    @Size(min = 2, message = "La descrizione deve avere una lunghezza maggiore di 2.")
    private String descrizione;

    @NotBlank(message = "Il tipo issue è obbligatorio.")
    private TipoIssue tipo;

    @NotBlank(message = "Lo stato issue è obbligatorio.")
    private StatoIssue stato;

    @NotBlank(message = "Inserire se la issue è stata archiviata è obbligatorio.")
    private boolean isArchiviato;

    @Size(max = 255, message = "L'immagine deve essere una stringa di max 255")
    private String immagine;

    private TipoPriorita priorita;

    @NotBlank(message = "l'id dell'utente che ha segnalato la issue è obbligatorio.")
    private Long idUtente;

    @NotBlank(message = "l'id del progetto è obbligatorio.")
    private Long idProgetto;

}
