package dev.parthenodevs.bugboard.backend.dto.response;

import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private Long idAutore;
    private String nomeAutore;

    private Long idProgetto;
    private String nomeProgetto;
}