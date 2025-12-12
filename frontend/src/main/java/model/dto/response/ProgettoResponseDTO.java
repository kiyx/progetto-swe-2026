package model.dto.response;

import model.dto.enums.StatoProgetto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgettoResponseDTO
{
    private Long id;
    private String nome;
    private StatoProgetto stato;

    private Long idTeam;
    private String nomeTeam;

    private Long idAdmin;
    private String nomeAdmin;

    private int issuesTotali;
}