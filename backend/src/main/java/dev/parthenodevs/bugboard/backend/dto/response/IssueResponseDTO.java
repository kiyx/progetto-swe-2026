package dev.parthenodevs.bugboard.backend.dto.response;

import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueResponseDTO
{
    private Long id;
    private String titolo;
    private String descrizione;
    private TipoIssue tipo;
    private StatoIssue stato;
    private boolean isArchiviato;
    private String immagine;
    private TipoPriorita priorita;
    private Long idUtente;
    private Long idProgetto;
}
