package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import dev.parthenodevs.bugboard.backend.dto.request.CreateProgettoRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.ProgettoResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.BusinessLogicException;
import dev.parthenodevs.bugboard.backend.exception.ResourceNotFoundException;
import dev.parthenodevs.bugboard.backend.exception.UnauthorizedException;
import dev.parthenodevs.bugboard.backend.mapper.ProgettoMapper;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.ProgettoRepository;
import dev.parthenodevs.bugboard.backend.repository.TeamRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.transaction.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.*;

@Service
public class ProgettoService
{
    private static final Logger logger = Logger.getLogger(ProgettoService.class.getName());

    private final ProgettoRepository progettoRepository;
    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;
    private final ProgettoMapper progettoMapper;

    public ProgettoService(ProgettoRepository progettoRepository,
                           TeamRepository teamRepository,
                           UtenteRepository utenteRepository,
                           ProgettoMapper progettoMapper)
    {
        this.progettoRepository = progettoRepository;
        this.teamRepository = teamRepository;
        this.utenteRepository = utenteRepository;
        this.progettoMapper = progettoMapper;
    }

    public List<ProgettoResponseDTO> getProgettiGestitiDaAdmin()
    {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        Utente admin = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        return progettoRepository.findAllByAdminIdOrderByIdDesc(admin.getId())
                .stream()
                .map(progettoMapper::toDto)
                .toList();
    }

    @Transactional
    public ProgettoResponseDTO createProgetto(CreateProgettoRequestDTO request)
    {
        logger.info(() -> "Tentativo creazione progetto: " + request.getNome());

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente adminLoggato = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trovato"));

        Team team = teamRepository.findById(request.getIdTeam())
                .orElseThrow(() -> new ResourceNotFoundException("Team non trovato"));

        if(!team.getAdmin().getId().equals(adminLoggato.getId()))
        {
            logger.warning(() -> "Tentativo non autorizzato sul team: " + team.getNome());
            throw new UnauthorizedException("Non puoi creare progetti per un Team che non amministri.");
        }

        if(request.getStato() == StatoProgetto.ATTIVO)
        {
            boolean esisteAttivo = progettoRepository.existsByTeamIdAndStato(team.getId(), StatoProgetto.ATTIVO);

            if(esisteAttivo)
            {
                logger.warning(() -> "Creazione bloccata: Il team " + team.getNome() + " ha già un progetto attivo.");
                throw new BusinessLogicException("Il Team '" + team.getNome() + "' ha già un progetto ATTIVO. Concludilo manualmente prima di crearne uno nuovo.");
            }
        }

        Progetto nuovoProgetto = progettoMapper.toEntity(request, team, adminLoggato);
        Progetto salvato = progettoRepository.save(nuovoProgetto);

        logger.info(() -> "Progetto creato con successo: ID " + salvato.getId());
        return progettoMapper.toDto(salvato);
    }

    @Transactional
    public ProgettoResponseDTO concludiProgetto(Long idProgetto)
    {
        Progetto progetto = progettoRepository.findById(idProgetto)
                .orElseThrow(() -> new ResourceNotFoundException("Progetto", "id", idProgetto));

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente adminLoggato = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trovato"));

        if(!progetto.getTeam().getAdmin().getId().equals(adminLoggato.getId()))
            throw new UnauthorizedException("Non puoi modificare un progetto di un team che non amministri.");

        if(progetto.getStato() == StatoProgetto.CONCLUSO)
            return progettoMapper.toDto(progetto);

        progetto.setStato(StatoProgetto.CONCLUSO);
        Progetto salvato = progettoRepository.save(progetto);

        logger.info(() -> "Progetto ID " + idProgetto + " concluso manualmente.");
        return progettoMapper.toDto(salvato);
    }
}