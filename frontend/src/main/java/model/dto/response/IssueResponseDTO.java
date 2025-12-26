package model.dto.response;

import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
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

    private Long idTeam;
    private String nomeTeam;
}