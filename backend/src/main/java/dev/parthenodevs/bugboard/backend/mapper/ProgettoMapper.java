package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.CreateProgettoRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.ProgettoResponseDTO;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class ProgettoMapper
{
    public ProgettoResponseDTO toDto(Progetto progetto)
    {
        if(progetto == null)
            return null;

        return ProgettoResponseDTO.builder()
                                  .id(progetto.getId())
                                  .nome(progetto.getNome())
                                  .stato(progetto.getStato())

                                  .idTeam(progetto.getTeam().getId())
                                  .nomeTeam(progetto.getTeam().getNome())

                                  .idAdmin(progetto.getAdmin().getId())
                                  .nomeAdmin(progetto.getAdmin().getNome() + " " + progetto.getAdmin().getCognome())

                                  .issuesTotali(progetto.getIssues().size())
                                  .build();
    }

    public Progetto toEntity(CreateProgettoRequestDTO request, Team team, Utente admin)
    {
        if(request == null)
            return null;

        return Progetto.builder()
                       .nome(request.getNome())
                       .stato(request.getStato())
                       .team(team)
                       .admin(admin)
                       .build();
    }
}