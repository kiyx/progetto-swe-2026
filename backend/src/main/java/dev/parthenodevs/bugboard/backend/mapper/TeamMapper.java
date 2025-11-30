package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.RegisterRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
                .nomeAdmin(team.getAdmin().getNome())
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