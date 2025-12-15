package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.RegisterRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateUtenteRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.service.UtenteService;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/utente")
@SuppressWarnings("NullableProblems")
public class UtenteController
{
    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService)
    {
        this.utenteService = utenteService;
    }

    @PutMapping("/me")
    public ResponseEntity<UtenteResponseDTO> updateMe(@Valid @RequestBody UpdateUtenteRequestDTO request)
    {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        UtenteResponseDTO updatedUser = utenteService.updateUtente(email, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtenteResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        UtenteResponseDTO newUser = utenteService.registerUtente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}