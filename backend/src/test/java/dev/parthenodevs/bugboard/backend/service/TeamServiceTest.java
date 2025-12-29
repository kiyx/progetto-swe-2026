package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.mapper.TeamMapper;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.TeamRepository;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private TeamMapper teamMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private TeamService teamService;

    private Utente admin;
    private Team team;

    @BeforeEach
    void setUp() {
        admin = new Utente(); admin.setId(1L); admin.setEmail("admin@test.com");
        team = new Team(); team.setId(10L); team.setNome("Team A"); team.setAdmin(admin);
    }

    private void mockSecurity()
    {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(admin.getEmail());
        when(utenteRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
    }

    @Test
    void createNewTeam_Success()
    {
        mockSecurity();
        CreateTeamRequestDTO req = new CreateTeamRequestDTO("Team A");

        when(teamMapper.toEntity(req, admin)).thenReturn(team);
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(TeamResponseDTO.builder().nome("Team A").build());

        TeamResponseDTO res = teamService.createNewTeam(req);

        assertEquals("Team A", res.getNome());
        verify(utenteRepository).save(admin);
    }

    @Test
    void getTeamsGestitiDaAdmin_Success() {
        mockSecurity();
        when(teamRepository.findByAdmin(admin)).thenReturn(List.of(team));
        when(teamMapper.toDto(team)).thenReturn(TeamResponseDTO.builder().nome("Team A").build());

        List<TeamResponseDTO> res = teamService.getTeamsGestitiDaAdmin();
        assertEquals(1, res.size());
    }

    @Test
    void addMembri_Success() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        Utente u = new Utente(); u.setId(2L);
        when(utenteRepository.findAllById(List.of(2L))).thenReturn(List.of(u));

        teamService.addMembri(10L, List.of(2L));

        verify(utenteRepository).saveAll(anyList());
    }

    @Test
    void addMembri_NoUsersFound_DoesNothing() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(utenteRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        teamService.addMembri(10L, List.of(99L));

        verify(utenteRepository, never()).saveAll(anyList());
    }

    @Test
    void removeMembri_Success() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        Utente u = new Utente(); u.setId(2L);
        when(utenteRepository.findAllById(List.of(2L))).thenReturn(List.of(u));

        teamService.removeMembri(10L, List.of(2L));

        verify(utenteRepository).saveAll(anyList());
    }

    @Test
    void teamNotFound_ThrowsException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());
        List<Long> ids = List.of(1L);
        assertThrows(EntityNotFoundException.class, () -> teamService.addMembri(99L, ids));
    }
}