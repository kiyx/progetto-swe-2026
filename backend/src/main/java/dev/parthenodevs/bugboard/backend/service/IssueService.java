package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.dto.request.CreateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.IssueResponseDTO;
import dev.parthenodevs.bugboard.backend.mapper.IssueMapper;
import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.IssueRepository;
import dev.parthenodevs.bugboard.backend.repository.ProgettoRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.persistence.*;
import jakarta.transaction.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class IssueService
{
    private static final Logger logger = Logger.getLogger(IssueService.class.getName());
    private static final String ISSUE_NOT_FOUND_MSG = "Issue non trovata con ID: ";

    private final IssueRepository issueRepository;
    private final UtenteRepository utenteRepository;
    private final ProgettoRepository progettoRepository;
    private final IssueMapper issueMapper;

    public IssueService(IssueRepository issueRepository,
                        UtenteRepository utenteRepository,
                        ProgettoRepository progettoRepository,
                        IssueMapper issueMapper)
    {
        this.issueRepository = issueRepository;
        this.utenteRepository = utenteRepository;
        this.progettoRepository = progettoRepository;
        this.issueMapper = issueMapper;
    }

    public List<IssueResponseDTO> getAllIssues()
    {
        return issueRepository.findAll().stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Transactional
    public List<IssueResponseDTO> getIssuesByAssignee(Long userId)
    {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con ID: " + userId));

        return utente.getAssignedIssues().stream()
                .map(issueMapper::toDto)
                .toList();
    }

    public List<IssueResponseDTO> getIssuesByAuthor(Long userId)
    {
        return issueRepository.findByAutore_Id(userId).stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Transactional
    public void createIssue(CreateIssueRequestDTO request)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated())
        {
            logger.warning("Tentativo di creazione issue senza autenticazione valida.");
            throw new SecurityException("Utente non autenticato.");
        }

        String email = authentication.getName();
        logger.info(() -> "Inizio creazione issue da parte di: " + email);

        Utente autore = utenteRepository.findByEmail(email)
                .orElseThrow(() ->
                {
                    logger.warning(() -> "Autore non trovato nel DB per email: " + email);
                    return new EntityNotFoundException("Utente autenticato non trovato nel DB");
                });

        Progetto progetto = progettoRepository.findById(request.getIdProgetto())
                .orElseThrow(() ->
                {
                    logger.warning(() -> "Progetto non trovato con ID: " + request.getIdProgetto());
                    return new EntityNotFoundException("Progetto non trovato");
                });

        Issue issue = issueMapper.toEntity(request, autore, progetto);
        Issue savedIssue = issueRepository.save(issue);

        logger.info(() -> "Nuova issue creata con successo: " + savedIssue.getTitolo() + " (ID: " + savedIssue.getId() + ")");
    }

    @Transactional
    public void updateIssue(Long id, UpdateIssueRequestDTO request)
    {
        Issue issue = getIssueOrThrow(id);

        issueMapper.update(issue, request);
        issueRepository.save(issue);

        logger.info(() -> "Issue aggiornata con successo: " + id);
    }

    @Transactional
    public void resolveIssue(Long id)
    {
        Issue issue = getIssueOrThrow(id);

        if(issue.getAssegnatari().isEmpty())
        {
            String currentUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

            if(currentUserEmail == null)
                throw new SecurityException("Nessun utente autenticato trovato.");

            Utente currentUser = utenteRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Utente corrente non trovato"));

            currentUser.addIssue(issue);
            utenteRepository.saveAndFlush(currentUser);

            logger.info(() -> "Issue auto-assegnata all'admin " + currentUserEmail + " prima della risoluzione.");
        }

        issue.setStato(StatoIssue.RISOLTA);
        issueRepository.save(issue);

        logger.info(() -> "Issue marcata come risolta: " + id);
    }

    @Transactional
    public void archiveIssue(Long id)
    {
        Issue issue = getIssueOrThrow(id);

        issue.setArchiviato(true);
        issueRepository.save(issue);

        logger.info(() -> "Issue archiviata: " + id);
    }

    @Transactional
    public void assignIssue(Long issueId, List<Long> userIds)
    {
        Issue issue = getIssueOrThrow(issueId);

        List<Utente> oldAssignees = utenteRepository.findByAssignedIssuesContaining(issue);
        for(Utente oldUser : oldAssignees)
            oldUser.removeIssue(issue);
        utenteRepository.saveAll(oldAssignees);

        List<Utente> newAssignees = utenteRepository.findAllById(userIds);
        for(Utente newUser : newAssignees)
            newUser.addIssue(issue);
        utenteRepository.saveAll(newAssignees);

        if(issue.getStato() == StatoIssue.TODO && !newAssignees.isEmpty())
        {
            issue.setStato(StatoIssue.ASSEGNATA);
            issueRepository.save(issue);
        }

        logger.info(() -> "Issue " + issueId + " riassegnata a " + newAssignees.size() + " utenti.");
    }

    private Issue getIssueOrThrow(Long id)
    {
        return issueRepository.findById(id)
                .orElseThrow(() ->
                {
                    logger.warning(() -> ISSUE_NOT_FOUND_MSG + id);
                    return new EntityNotFoundException(ISSUE_NOT_FOUND_MSG + id);
                });
    }
}