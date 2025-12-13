package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.LoginRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.AuthResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.PasswordNotMatchingException;
import dev.parthenodevs.bugboard.backend.exception.UserNotFoundException;
import dev.parthenodevs.bugboard.backend.mapper.UtenteMapper;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.validation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.logging.*;

@Service
public class AuthService
{
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthService(UtenteRepository utenteRepository, UtenteMapper utenteMapper, PasswordEncoder passwordEncoder, JWTService jwtService)
    {
        this.utenteRepository = utenteRepository;
        this.utenteMapper = utenteMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO requestDTO)
    {
        LOGGER.info("Tentativo di autenticazione per email: " + requestDTO.getEmail());

        Utente utente = utenteRepository.findByEmail(requestDTO.getEmail())
                        .orElseThrow(() -> new UserNotFoundException("L'utente non è stato trovato nel database"));

        if(!passwordEncoder.matches(requestDTO.getPassword(), utente.getPassword()))
        {
            LOGGER.warning("Password non corrispondente per l'utente: " + requestDTO.getEmail());
            throw new PasswordNotMatchingException("Password non corrispondente per l'utente");
        }

        String token = jwtService.generateToken(utente);
        UtenteResponseDTO utenteResponse = utenteMapper.toDto(utente);

        return AuthResponseDTO.builder()
                              .jwtToken(token)
                              .utente(utenteResponse)
                              .build();
    }
}