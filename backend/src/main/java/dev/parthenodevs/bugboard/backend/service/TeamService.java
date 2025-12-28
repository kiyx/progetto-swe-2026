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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class TeamService
{
    private static final Logger LOGGER = Logger.getLogger(TeamService.class.getName());

    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;
    private final TeamMapper teamMapper;
    private final UtenteMapper utenteMapper;
    private static final String TEAM_NOT_FOUND_MSG = "Team non trovato con ID: ";

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
        LOGGER.info(() -> "Inizio procedura di creazione di un nuovo team.");

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente adminLoggato = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trovato"));

        Team nuovoTeam = teamMapper.toEntity(request, adminLoggato);

        Team teamSalvato = teamRepository.save(nuovoTeam);

        adminLoggato.addTeam(teamSalvato);
        utenteRepository.save(adminLoggato);

        LOGGER.info(() -> String.format("Team salvato con successo. ID: %d, Nome: %s, Admin: %s",
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

    @Transactional
    public void addMembri(Long teamId, List<Long> userIds)
    {
        Team team = getTeamOrThrow(teamId);

        List<Utente> newMembers = utenteRepository.findByAssignedTeams_Id(teamId);

        for(Utente newMem : newMembers)
            newMem.addTeam(team);
        utenteRepository.saveAll(newMembers);

        if(!newMembers.isEmpty())
        {
            teamRepository.save(team);
        }

        LOGGER.info(() -> "Al Team " + teamId + " aggiunti " + newMembers.size() + " utenti.");
    }

    @Transactional
    public void removeMembri(Long teamId, List<Long> userIds)
    {
        Team team = getTeamOrThrow(teamId);

        List<Utente> oldMembers = utenteRepository.findByAssignedTeams_Id(teamId);
        List<Utente> toDeletemembers = utenteRepository.findAllById(userIds);
        for(Utente oldMem : oldMembers)
            for(Utente toDelete : toDeletemembers)
                if(oldMem.equals(toDelete))
                    oldMem.removeTeam(team);
        utenteRepository.saveAll(oldMembers);

        if(!toDeletemembers.isEmpty())
        {
            teamRepository.save(team);
        }

        LOGGER.info(() -> "Dal Team " + teamId + " eliminati " + toDeletemembers.size() + " utenti.");
    }

    private Team getTeamOrThrow(Long id)
    {
        return teamRepository.findById(id)
                .orElseThrow(() ->
                {
                    LOGGER.warning(() -> TEAM_NOT_FOUND_MSG + id);
                    return new EntityNotFoundException(TEAM_NOT_FOUND_MSG + id);
                });
    }
}