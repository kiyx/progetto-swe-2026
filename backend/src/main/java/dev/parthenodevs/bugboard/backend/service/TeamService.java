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
import jakarta.persistence.*;
import jakarta.transaction.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import java.util.List;
import java.util.*;
import java.util.logging.*;

@Service
public class TeamService
{
    private static final Logger LOGGER = Logger.getLogger(TeamService.class.getName());
    private static final String TEAM_NOT_FOUND_MSG = "Team non trovato con ID: ";

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
        LOGGER.info("Inizio creazione nuovo team.");

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente adminLoggato = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trovato"));

        Team nuovoTeam = teamMapper.toEntity(request, adminLoggato);
        Team teamSalvato = teamRepository.save(nuovoTeam);

        adminLoggato.addTeam(teamSalvato);
        utenteRepository.save(adminLoggato);

        LOGGER.log(Level.INFO, "Team creato con successo: {0}", teamSalvato.getNome());
        return teamMapper.toDto(teamSalvato);
    }

    public List<TeamResponseDTO> getTeamsGestitiDaAdmin()
    {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente admin = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente corrente non trovato"));

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

    @Transactional
    public void addMembri(Long teamId, List<Long> userIds)
    {
        Team team = getTeamOrThrow(teamId);
        List<Utente> utentiDaAggiungere = utenteRepository.findAllById(userIds);

        if(utentiDaAggiungere.isEmpty())
        {
            LOGGER.warning("Nessun utente trovato per l'aggiunta.");
            return;
        }

        for(Utente u : utentiDaAggiungere)
            u.addTeam(team);

        utenteRepository.saveAll(utentiDaAggiungere);
        LOGGER.log(Level.INFO, "Aggiunti {0} utenti al team {1}", new Object[]{utentiDaAggiungere.size(), teamId});
    }

    @Transactional
    public void removeMembri(Long teamId, List<Long> userIds)
    {
        Team team = getTeamOrThrow(teamId);
        List<Utente> utentiDaRimuovere = utenteRepository.findAllById(userIds);

        if(utentiDaRimuovere.isEmpty())
        {
            LOGGER.warning("Nessun utente trovato per la rimozione.");
            return;
        }

        for(Utente u : utentiDaRimuovere)
            u.removeTeam(team);

        utenteRepository.saveAll(utentiDaRimuovere);
        LOGGER.log(Level.INFO, "Rimossi {0} utenti dal team {1}", new Object[]{utentiDaRimuovere.size(), teamId});
    }

    private Team getTeamOrThrow(Long id)
    {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_NOT_FOUND_MSG + id));
    }
}