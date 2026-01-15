package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.enums.*;
import dev.parthenodevs.bugboard.backend.dto.request.*;
import dev.parthenodevs.bugboard.backend.mapper.*;
import dev.parthenodevs.bugboard.backend.model.*;
import dev.parthenodevs.bugboard.backend.repository.*;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import java.util.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
class IssueServiceTest
{
    IssueRepository issueRepository = mock(IssueRepository.class);
    UtenteRepository utenteRepository = mock(UtenteRepository.class);
    ProgettoRepository progettoRepository = mock(ProgettoRepository.class);
    IssueMapper issueMapper = mock(IssueMapper.class);
    private final IssueService service = new IssueService(issueRepository, utenteRepository, progettoRepository, issueMapper);


    // ---------------------------------------------
    // Test per updateIssue
    // ---------------------------------------------

    // T1: CE-ID2 (Non Esiste) + CE-DTO1 (Valido) -> EntityNotFoundException
    @Test
    void updateIssue_T1_NotFound()
    {
        Long issueId = 999L;
        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateIssue(issueId, request));

        verify(issueRepository, never()).save(any());
    }

    // T2: CE-ID3 (Null) + CE-DTO2 (Null) -> IllegalArgumentException
    @Test
    void updateIssue_T2_IdNull_DtoNull()
    {
        when(issueRepository.findById(isNull())).thenThrow(new IllegalArgumentException("ID null"));

        assertThrows(IllegalArgumentException.class, () -> service.updateIssue(null, null));

        verify(issueRepository).findById(isNull());
    }

    // T3: CE-ID1 (Esiste) + CE-DTO1 (Valido) -> Successo
    @Test
    void updateIssue_T3_Success()
    {
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setId(issueId);

        UpdateIssueRequestDTO request = new UpdateIssueRequestDTO();
        request.setTitolo("Nuovo Titolo");

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        // Simuliamo che il mapper aggiorni davvero l'oggetto
        doAnswer(invocation ->
        {
            Issue t = invocation.getArgument(0);
            UpdateIssueRequestDTO s = invocation.getArgument(1);
            t.setTitolo(s.getTitolo());
            return null;
        }).when(issueMapper).update(any(), any());

        service.updateIssue(issueId, request);

        assertEquals("Nuovo Titolo", issue.getTitolo());
        verify(issueRepository).save(issue);
    }

    // T4: CE-ID1 (Esiste) + CE-DTO2 (Null) -> Successo
    @Test
    void updateIssue_T4_DtoNull()
    {
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setId(issueId);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        service.updateIssue(issueId, null);

        verify(issueMapper).update(eq(issue), isNull());
        verify(issueRepository).save(issue);
    }

    // ---------------------------------------------
    // Test per assignIssue
    // ---------------------------------------------

    // T1: CE-ID1 (TODO) + CE-U1 (Lista Piena) -> Successo + Cambio Stato
    @Test
    void assignIssue_T1_Todo_ListNotEmpty()
    {
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setStato(StatoIssue.TODO);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(utenteRepository.findAllById(anyList())).thenReturn(List.of(new Utente()));

        service.assignIssue(issueId, List.of(10L));

        assertEquals(StatoIssue.ASSEGNATA, issue.getStato());
        verify(issueRepository).save(issue);
    }

    // T2: CE-ID2 (Altro Stato) + CE-U2 (Lista Vuota) -> Successo + No Cambio Stato
    @Test
    void assignIssue_T2_OtherState_EmptyList()
    {
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setStato(StatoIssue.RISOLTA);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(utenteRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

        service.assignIssue(issueId, Collections.emptyList());

        assertEquals(StatoIssue.RISOLTA, issue.getStato());
    }

    // T3: CE-ID3 (Non Esiste) + CE-U1 (Valid) -> EntityNotFoundException
    @Test
    void assignIssue_T3_IssueNotFound()
    {
        when(issueRepository.findById(999L)).thenReturn(Optional.empty());

        List<Long> ids = List.of(1L);
        assertThrows(EntityNotFoundException.class, () -> service.assignIssue(999L, ids));
    }

    // T4: CE-ID4 (Null) + CE-U2 (Valid) -> IllegalArgumentException
    @Test
    void assignIssue_T4_IssueIdNull()
    {
        when(issueRepository.findById(null)).thenThrow(new IllegalArgumentException("ID null"));

        List<Long> emptyList = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> service.assignIssue(null, emptyList));
    }

    // T5: CE-U3 (Null List) + CE-ID2 (Valid) -> IllegalArgumentException
    @Test
    void assignIssue_T5_ListNull()
    {
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setStato(StatoIssue.RISOLTA);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(utenteRepository.findAllById(null)).thenThrow(new IllegalArgumentException("List null"));

        assertThrows(IllegalArgumentException.class, () -> service.assignIssue(issueId, null));
    }
}