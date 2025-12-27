package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.ResourceNotFoundException;
import dev.parthenodevs.bugboard.backend.mapper.TeamMapper;
import dev.parthenodevs.bugboard.backend.mapper.UtenteMapper;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.TeamRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class TeamService
{
    private static final Logger logger = Logger.getLogger(TeamService.class.getName());

    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;
    private final TeamMapper teamMapper;
    private final UtenteMapper utenteMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper, UtenteRepository utenteRepository, UtenteMapper utenteMapper)
    {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.utenteRepository = utenteRepository;
        this.utenteMapper = utenteMapper;
    }

    @Transactional
    public TeamResponseDTO createNewTeam(CreateTeamRequestDTO request)
    {
        logger.info(() -> "Inizio procedura di creazione di un nuovo team.");

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente adminLoggato = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trovato"));

        Team nuovoTeam = teamMapper.toEntity(request, adminLoggato);

        Team teamSalvato = teamRepository.save(nuovoTeam);

        adminLoggato.addTeam(teamSalvato);
        utenteRepository.save(adminLoggato);

        logger.info(() -> String.format("Team salvato con successo. ID: %d, Nome: %s, Admin: %s",
                teamSalvato.getId(), teamSalvato.getNome(), teamSalvato.getAdmin().getNome()));

        return teamMapper.toDto(teamSalvato);
    }

    public List<TeamResponseDTO> getTeamsGestitiDaAdmin()
    {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        Utente admin = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        return teamRepository.findByAdmin(admin)
                .stream()
                .map(teamMapper::toDto)
                .toList();
    }

    public List<UtenteResponseDTO> getTeamsMembers(long id)
    {
        return utenteRepository.findByAssignedTeams_Id(id)
                .stream()
                .map(utenteMapper::toDto)
                .toList();
    }

    public List<UtenteResponseDTO> getTeamsNotMembers(long id)
    {

        return utenteRepository.findUsersNotInTeam(id)
                .stream()
                .map(utenteMapper::toDto)
                .toList();
    }
}