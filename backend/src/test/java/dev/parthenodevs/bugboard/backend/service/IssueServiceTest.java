package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import dev.parthenodevs.bugboard.backend.dto.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.dto.enums.TipoPriorita;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.mapper.IssueMapper;
import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.IssueRepository;
import dev.parthenodevs.bugboard.backend.repository.ProgettoRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest
{

    IssueRepository issueRepository = mock(IssueRepository.class);
    UtenteRepository utenteRepository = mock(UtenteRepository.class);
    ProgettoRepository progettoRepository = mock(ProgettoRepository.class);
    IssueMapper issueMapper = mock(IssueMapper.class);

    private final IssueService service = new IssueService(issueRepository, utenteRepository, progettoRepository, issueMapper);

    @Test
    void updateIssue_ID2_UIRD1()
    {
        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();
        request.setTitolo("titolo cambiato");
        request.setDescrizione("descrizione cambiata");
        request.setTipo(TipoIssue.DOCUMENTATION);
        request.setStato(StatoIssue.ASSEGNATA);
        request.setPriorita(TipoPriorita.BASSA);

        IssueRepository issueRepository = mock(IssueRepository.class);

        service.updateIssue(null, request);

        verify(issueRepository).findById(null);
        verify(issueRepository, times(0)).save(null);
    }

    void updateIssue_ID3_UIRD1()
    {
        Utente mockUtente = new Utente();
        mockUtente.setId(111L);
        mockUtente.setNome("Utente");
        mockUtente.setCognome("Cognome");
        mockUtente.setEmail("email@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Utente mockAdmin = new Utente();
        mockUtente.setId(222L);
        mockUtente.setNome("Admin");
        mockUtente.setCognome("CognomeAdmin");
        mockUtente.setEmail("emailadmin@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Team mockTeam = new Team();
        mockTeam.setId(333L);
        mockTeam.setAdmin(mockAdmin);
        mockTeam.setNome("nomeTeam");
        mockTeam.setMembri(null);

        Progetto mockProgetto = new Progetto();
        mockProgetto.setId(444L);
        mockProgetto.setNome("nomeProgetto");
        mockProgetto.setTeam(mockTeam);
        mockProgetto.setStato(StatoProgetto.ATTIVO);
        mockProgetto.setIssues(null);
        mockProgetto.setAdmin(mockAdmin);

        Issue mockIssue = new Issue();
        mockIssue.setId(123L);
        mockIssue.setTitolo("titolo iniziale");
        mockIssue.setDescrizione("descrizione iniziale");
        mockIssue.setTipo(TipoIssue.BUG);
        mockIssue.setStato(StatoIssue.TODO);
        mockIssue.setArchiviato(false);
        mockIssue.setPriorita(TipoPriorita.ALTA);
        mockIssue.setAutore(mockUtente);
        mockIssue.setProgetto(mockProgetto);

        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();
        request.setTitolo("titolo cambiato");
        request.setDescrizione("descrizione cambiata");
        request.setTipo(TipoIssue.DOCUMENTATION);
        request.setStato(StatoIssue.ASSEGNATA);
        request.setPriorita(TipoPriorita.BASSA);

        IssueRepository issueRepository = mock(IssueRepository.class);
        when(issueRepository.findById(123L)).thenReturn(Optional.empty());

        assertNotEquals(mockIssue.getTitolo(), request.getTitolo());
        assertNotEquals(mockIssue.getDescrizione(), request.getDescrizione());
        assertNotEquals(mockIssue.getTipo(), request.getTipo());
        assertNotEquals(mockIssue.getStato(), request.getStato());
        assertNotEquals(mockIssue.getPriorita(), request.getPriorita());

        verify(issueRepository).findById(123L);
        verify(issueRepository, times(0)).save(null);
    }

    void updateIssue_UIRD2_ID1()
    {
        Utente mockUtente = new Utente();
        mockUtente.setId(111L);
        mockUtente.setNome("Utente");
        mockUtente.setCognome("Cognome");
        mockUtente.setEmail("email@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Utente mockAdmin = new Utente();
        mockUtente.setId(222L);
        mockUtente.setNome("Admin");
        mockUtente.setCognome("CognomeAdmin");
        mockUtente.setEmail("emailadmin@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Team mockTeam = new Team();
        mockTeam.setId(333L);
        mockTeam.setAdmin(mockAdmin);
        mockTeam.setNome("nomeTeam");
        mockTeam.setMembri(null);

        Progetto mockProgetto = new Progetto();
        mockProgetto.setId(444L);
        mockProgetto.setNome("nomeProgetto");
        mockProgetto.setTeam(mockTeam);
        mockProgetto.setStato(StatoProgetto.ATTIVO);
        mockProgetto.setIssues(null);
        mockProgetto.setAdmin(mockAdmin);

        Issue mockIssue = new Issue();
        mockIssue.setId(123L);
        mockIssue.setTitolo("titolo iniziale");
        mockIssue.setDescrizione("descrizione iniziale");
        mockIssue.setTipo(TipoIssue.BUG);
        mockIssue.setStato(StatoIssue.TODO);
        mockIssue.setArchiviato(false);
        mockIssue.setPriorita(TipoPriorita.ALTA);
        mockIssue.setAutore(mockUtente);
        mockIssue.setProgetto(mockProgetto);
        mockIssue.setAssegnatari(null);

        IssueRepository issueRepository = mock(IssueRepository.class);
        when(issueRepository.findById(123L)).thenReturn(Optional.of(mockIssue));
        when(issueRepository.save(mockIssue)).thenReturn(mockIssue);

        service.updateIssue(123L, null);

        assertEquals("titolo iniziale", mockIssue.getTitolo());
        assertEquals("descrizione iniziale", mockIssue.getDescrizione());
        assertEquals(TipoIssue.BUG, mockIssue.getTipo());
        assertEquals(StatoIssue.TODO, mockIssue.getStato());
        assertEquals(TipoPriorita.ALTA, mockIssue.getPriorita());

        verify(issueRepository).findById(123L);
        verify(issueRepository).save(mockIssue);

    }

    void updateIssue_ID1_UIRD1()
    {

        Utente mockUtente = new Utente();
        mockUtente.setId(111L);
        mockUtente.setNome("Utente");
        mockUtente.setCognome("Cognome");
        mockUtente.setEmail("email@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Utente mockAdmin = new Utente();
        mockUtente.setId(222L);
        mockUtente.setNome("Admin");
        mockUtente.setCognome("CognomeAdmin");
        mockUtente.setEmail("emailadmin@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Team mockTeam = new Team();
        mockTeam.setId(333L);
        mockTeam.setAdmin(mockAdmin);
        mockTeam.setNome("nomeTeam");
        mockTeam.setMembri(null);

        Progetto mockProgetto = new Progetto();
        mockProgetto.setId(444L);
        mockProgetto.setNome("nomeProgetto");
        mockProgetto.setTeam(mockTeam);
        mockProgetto.setStato(StatoProgetto.ATTIVO);
        mockProgetto.setIssues(null);
        mockProgetto.setAdmin(mockAdmin);

        Issue mockIssue = new Issue();
        mockIssue.setId(123L);
        mockIssue.setTitolo("titolo iniziale");
        mockIssue.setDescrizione("descrizione iniziale");
        mockIssue.setTipo(TipoIssue.BUG);
        mockIssue.setStato(StatoIssue.TODO);
        mockIssue.setArchiviato(false);
        mockIssue.setPriorita(TipoPriorita.ALTA);
        mockIssue.setAutore(mockUtente);
        mockIssue.setProgetto(mockProgetto);
        mockIssue.setAssegnatari(null);

        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();
        request.setTitolo("titolo cambiato");
        request.setDescrizione("descrizione cambiata");
        request.setTipo(TipoIssue.DOCUMENTATION);
        request.setStato(StatoIssue.ASSEGNATA);
        request.setPriorita(TipoPriorita.BASSA);

        IssueRepository issueRepository = mock(IssueRepository.class);
        when(issueRepository.findById(123L)).thenReturn(Optional.of(mockIssue));
        when(issueRepository.save(mockIssue)).thenReturn(mockIssue);

        service.updateIssue(123L, request);

        assertEquals(mockIssue.getTitolo(), request.getTitolo());
        assertEquals(mockIssue.getDescrizione(), request.getDescrizione());
        assertEquals(mockIssue.getTipo(), request.getTipo());
        assertEquals(mockIssue.getStato(), request.getStato());
        assertEquals(mockIssue.getPriorita(), request.getPriorita());

        verify(issueRepository).findById(123L);
        verify(issueRepository).save(mockIssue);
    }

    void updateIssue_ID1_UIRD3()
    {

        Utente mockUtente = new Utente();
        mockUtente.setId(111L);
        mockUtente.setNome("Utente");
        mockUtente.setCognome("Cognome");
        mockUtente.setEmail("email@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Utente mockAdmin = new Utente();
        mockUtente.setId(222L);
        mockUtente.setNome("Admin");
        mockUtente.setCognome("CognomeAdmin");
        mockUtente.setEmail("emailadmin@bugboard26.com");
        mockUtente.setPassword("password");
        mockUtente.setIsAdmin(false);

        Team mockTeam = new Team();
        mockTeam.setId(333L);
        mockTeam.setAdmin(mockAdmin);
        mockTeam.setNome("nomeTeam");
        mockTeam.setMembri(null);

        Progetto mockProgetto = new Progetto();
        mockProgetto.setId(444L);
        mockProgetto.setNome("nomeProgetto");
        mockProgetto.setTeam(mockTeam);
        mockProgetto.setStato(StatoProgetto.ATTIVO);
        mockProgetto.setIssues(null);
        mockProgetto.setAdmin(mockAdmin);

        Issue mockIssue = new Issue();
        mockIssue.setId(123L);
        mockIssue.setTitolo("titolo iniziale");
        mockIssue.setDescrizione("descrizione iniziale");
        mockIssue.setTipo(TipoIssue.BUG);
        mockIssue.setStato(StatoIssue.TODO);
        mockIssue.setArchiviato(false);
        mockIssue.setPriorita(TipoPriorita.ALTA);
        mockIssue.setAutore(mockUtente);
        mockIssue.setProgetto(mockProgetto);
        mockIssue.setAssegnatari(null);

        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();
        request.setDescrizione("descrizione cambiata");
        request.setTipo(TipoIssue.DOCUMENTATION);
        request.setStato(StatoIssue.ASSEGNATA);
        request.setPriorita(TipoPriorita.BASSA);

        IssueRepository issueRepository = mock(IssueRepository.class);
        when(issueRepository.findById(123L)).thenReturn(Optional.of(mockIssue));
        when(issueRepository.save(mockIssue)).thenReturn(mockIssue);

        service.updateIssue(123L, request);

        assertEquals("titolo iniziale", mockIssue.getTitolo());
        assertEquals(mockIssue.getDescrizione(), request.getDescrizione());
        assertEquals(mockIssue.getTipo(), request.getTipo());
        assertEquals(mockIssue.getStato(), request.getStato());
        assertEquals(mockIssue.getPriorita(), request.getPriorita());

        verify(issueRepository).findById(123L);
        verify(issueRepository).save(mockIssue);
    }
}
