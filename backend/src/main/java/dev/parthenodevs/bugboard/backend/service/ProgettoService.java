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
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

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

    public List<ProgettoResponseDTO> getProgettiAccessibili()
    {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        List<Progetto> progetti = progettoRepository.findProgettiAccessibiliOrderByIdDesc(utente.getId());

        return progetti.stream()
                .map(progettoMapper::toDto)
                .toList();
    }

    @Transactional
    public ProgettoResponseDTO createProgetto(CreateProgettoRequestDTO request)
    {
        logger.info(() -> "Creazione progetto: " + request.getNome());

        Utente adminLoggato = getAdminLoggato();

        Team team = teamRepository.findById(request.getIdTeam())
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", request.getIdTeam()));

        if(!team.getAdmin().getId().equals(adminLoggato.getId()))
            throw new UnauthorizedException("Non puoi creare progetti per un Team che non amministri.");

        if(request.getStato() == StatoProgetto.ATTIVO)
        {
            boolean esisteAttivo = progettoRepository.existsByTeamIdAndStato(team.getId(), StatoProgetto.ATTIVO);
            if(esisteAttivo)
                throw new BusinessLogicException("Il Team '" + team.getNome() + "' ha già un progetto ATTIVO.");
        }

        Progetto nuovoProgetto = progettoMapper.toEntity(request, team, adminLoggato);
        return progettoMapper.toDto(progettoRepository.save(nuovoProgetto));
    }

    @Transactional
    public ProgettoResponseDTO attivaProgetto(Long idProgetto)
    {
        Progetto progetto = getProgettoIfAdmin(idProgetto);

        if(progetto.getStato() == StatoProgetto.ATTIVO)
            return progettoMapper.toDto(progetto);

        boolean esisteAttivo = progettoRepository.existsByTeamIdAndStato(progetto.getTeam().getId(), StatoProgetto.ATTIVO);
        if(esisteAttivo)
            throw new BusinessLogicException("Esiste già un progetto ATTIVO nel team. Concludilo prima.");

        progetto.setStato(StatoProgetto.ATTIVO);
        logger.info(() -> "Progetto ID " + idProgetto + " attivato.");
        return progettoMapper.toDto(progettoRepository.save(progetto));
    }

    @Transactional
    public ProgettoResponseDTO concludiProgetto(Long idProgetto)
    {
        Progetto progetto = getProgettoIfAdmin(idProgetto);

        if(progetto.getStato() == StatoProgetto.CONCLUSO)
            return progettoMapper.toDto(progetto);

        progetto.setStato(StatoProgetto.CONCLUSO);
        logger.info(() -> "Progetto ID " + idProgetto + " concluso.");
        return progettoMapper.toDto(progettoRepository.save(progetto));
    }

    private Utente getAdminLoggato()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated())
            throw new UnauthorizedException("Utente non autenticato o sessione mancante.");

        String email = authentication.getName();
        if(email == null)
            throw new UnauthorizedException("Identità utente non valida.");

        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "email", email));
    }

    private Progetto getProgettoIfAdmin(Long idProgetto)
    {
        Progetto progetto = progettoRepository.findById(idProgetto)
                .orElseThrow(() -> new ResourceNotFoundException("Progetto", "id", idProgetto));

        Utente adminLoggato = getAdminLoggato();

        if(!progetto.getTeam().getAdmin().getId().equals(adminLoggato.getId()))
            throw new UnauthorizedException("Non hai i permessi per modificare questo progetto.");
        return progetto;
    }
}