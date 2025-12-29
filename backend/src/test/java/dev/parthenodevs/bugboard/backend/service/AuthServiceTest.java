package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.LoginRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.AuthResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.UserNotFoundException;
import dev.parthenodevs.bugboard.backend.mapper.UtenteMapper;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest
{

    @Mock private UtenteRepository utenteRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTService jwtService;
    @Mock private UtenteMapper utenteMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success()
    {
        LoginRequestDTO req = new LoginRequestDTO("mario@test.com", "pass");
        Utente utente = new Utente();
        utente.setEmail("mario@test.com");

        when(utenteRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(utente));
        when(jwtService.generateToken(utente)).thenReturn("token");

        AuthResponseDTO res = authService.login(req);

        assertNotNull(res);
        assertEquals("token", res.getJwtToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_UserNotFound_ThrowsException()
    {
        LoginRequestDTO req = new LoginRequestDTO("notfound@test.com", "pass");
        when(utenteRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(req));
    }
}