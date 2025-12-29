package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper
{
    public TeamResponseDTO toDto(Team team)
    {
        if(team == null)
            return null;

        return TeamResponseDTO.builder()
                .id(team.getId())
                .nome(team.getNome())
                .idAdmin(team.getAdmin().getId())
                .nomeAdmin(team.getAdmin().getNome() + " " + team.getAdmin().getCognome())
                .numeroMembri(team.getMembri() != null ? team.getMembri().size() : 0)
                .numeroProgetti(team.getProgetti() != null ? team.getProgetti().size() : 0)
                .build();
    }

    public Team toEntity(CreateTeamRequestDTO request, Utente admin)
    {
        if(request == null)
            return null;

        return Team.builder()
                .nome(request.getNome())
                .admin(admin)
                .build();
    }
}