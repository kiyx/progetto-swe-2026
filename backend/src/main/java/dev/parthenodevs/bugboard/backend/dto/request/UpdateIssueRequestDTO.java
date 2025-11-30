package dev.parthenodevs.bugboard.backend.dto.request;

import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateIssueRequestDTO
{
    @Size(min = 2, max = 200, message = "Il titolo deve avere una lunghezza compresa tra 2 e 200.")
    private String titolo;

    @Size(min = 2, message = "La descrizione deve avere una lunghezza maggiore di 2.")
    private String descrizione;

    private TipoIssue tipo;
    private StatoIssue stato;
    private boolean isArchiviato;

    private TipoPriorita priorita;

    @Size(max = 255, message = "L'immagine deve essere una stringa di max 255")
    private String immagine;
}