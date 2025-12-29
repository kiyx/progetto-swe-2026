package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import dev.parthenodevs.bugboard.backend.dto.request.CreateProgettoRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.ProgettoResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.BusinessLogicException;
import dev.parthenodevs.bugboard.backend.exception.UnauthorizedException;
import dev.parthenodevs.bugboard.backend.mapper.ProgettoMapper;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.ProgettoRepository;
import dev.parthenodevs.bugboard.backend.repository.TeamRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgettoServiceTest {

    @Mock private ProgettoRepository progettoRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private ProgettoMapper progettoMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ProgettoService progettoService;

    private Utente admin;
    private Team team;
    private Progetto progetto;

    @BeforeEach
    void setUp() {
        admin = new Utente(); admin.setId(1L); admin.setEmail("admin@test.com");
        team = new Team(); team.setId(10L); team.setAdmin(admin);
        progetto = new Progetto(); progetto.setId(100L); progetto.setTeam(team); progetto.setStato(StatoProgetto.FUTURO);
    }

    private void mockSecurity() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(admin.getEmail());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(utenteRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
    }

    @Test
    void createProgetto_Success() {
        mockSecurity();
        CreateProgettoRequestDTO req = new CreateProgettoRequestDTO("New Prj", StatoProgetto.FUTURO, 1L, 10L);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(progettoMapper.toEntity(any(), any(), any())).thenReturn(progetto);
        when(progettoRepository.save(any())).thenReturn(progetto);
        when(progettoMapper.toDto(any())).thenReturn(ProgettoResponseDTO.builder().id(100L).build());

        assertNotNull(progettoService.createProgetto(req));
        verify(progettoRepository).save(any());
    }

    @Test
    void createProgetto_Unauthorized_IfNotTeamAdmin() {
        mockSecurity();
        Utente otherAdmin = new Utente(); otherAdmin.setId(2L);
        team.setAdmin(otherAdmin); // Il team è di un altro

        CreateProgettoRequestDTO req = new CreateProgettoRequestDTO("New Prj", StatoProgetto.FUTURO, 1L, 10L);
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        assertThrows(UnauthorizedException.class, () -> progettoService.createProgetto(req));
    }

    @Test
    void createProgetto_BusinessLogic_ActiveExists() {
        mockSecurity();
        CreateProgettoRequestDTO req = new CreateProgettoRequestDTO("New Prj", StatoProgetto.ATTIVO, 1L, 10L);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(progettoRepository.existsByTeamIdAndStato(10L, StatoProgetto.ATTIVO)).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> progettoService.createProgetto(req));
    }

    @Test
    void attivaProgetto_Success() {
        mockSecurity();
        when(progettoRepository.findById(100L)).thenReturn(Optional.of(progetto));
        when(progettoRepository.existsByTeamIdAndStato(10L, StatoProgetto.ATTIVO)).thenReturn(false);
        when(progettoRepository.save(progetto)).thenReturn(progetto);
        when(progettoMapper.toDto(progetto)).thenReturn(ProgettoResponseDTO.builder().stato(StatoProgetto.ATTIVO).build());

        progettoService.attivaProgetto(100L);
        assertEquals(StatoProgetto.ATTIVO, progetto.getStato());
    }

    @Test
    void attivaProgetto_AlreadyActive_DoesNothing() {
        mockSecurity();
        progetto.setStato(StatoProgetto.ATTIVO);
        when(progettoRepository.findById(100L)).thenReturn(Optional.of(progetto));

        progettoService.attivaProgetto(100L);
        verify(progettoRepository, never()).save(any());
    }

    @Test
    void concludiProgetto_Success() {
        mockSecurity();
        progetto.setStato(StatoProgetto.ATTIVO);
        when(progettoRepository.findById(100L)).thenReturn(Optional.of(progetto));
        when(progettoRepository.save(progetto)).thenReturn(progetto);

        progettoService.concludiProgetto(100L);
        assertEquals(StatoProgetto.CONCLUSO, progetto.getStato());
    }
}