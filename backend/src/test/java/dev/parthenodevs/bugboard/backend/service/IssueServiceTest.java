package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.CreateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.exception.UnauthorizedException;
import dev.parthenodevs.bugboard.backend.mapper.IssueMapper;
import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.IssueRepository;
import dev.parthenodevs.bugboard.backend.repository.ProgettoRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock private IssueRepository issueRepository;
    @Mock private ProgettoRepository progettoRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private IssueMapper issueMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private IssueService issueService;

    private Utente user;
    private Progetto progetto;
    private Team team;
    private Issue issue;

    @BeforeEach
    void setUp()
    {
        user = new Utente(); user.setId(1L); user.setEmail("user@test.com");
        team = new Team(); team.setId(10L);
        team.setMembri(Set.of(user));

        progetto = new Progetto(); progetto.setId(100L); progetto.setTeam(team);

        issue = new Issue(); issue.setId(1000L); issue.setProgetto(progetto);
    }

    private void mockSecurity() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(utenteRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void createIssue_Unauthorized_IfNotMember()
    {
        mockSecurity();
        team.setMembri(Set.of());
        CreateIssueRequestDTO req = new CreateIssueRequestDTO();
        req.setIdProgetto(100L);

        when(progettoRepository.findById(100L)).thenReturn(Optional.of(progetto));

        assertThrows(UnauthorizedException.class, () -> issueService.createIssue(req));
    }

}