package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.CreateProgettoRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.ProgettoResponseDTO;
import dev.parthenodevs.bugboard.backend.service.ProgettoService;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/progetti")
@SuppressWarnings("NullableProblems")
public class ProgettoController
{
    private final ProgettoService progettoService;

    public ProgettoController(ProgettoService progettoService)
    {
        this.progettoService = progettoService;
    }

    @GetMapping("/miei")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProgettoResponseDTO>> getMieiProgetti()
    {
        return ResponseEntity.ok(progettoService.getProgettiAccessibili());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProgettoResponseDTO> createProgetto(@Valid @RequestBody CreateProgettoRequestDTO request)
    {
        ProgettoResponseDTO newProject = progettoService.createProgetto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
    }

    @PatchMapping("/{id}/attiva")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProgettoResponseDTO> attivaProgetto(@PathVariable Long id)
    {
        return ResponseEntity.ok(progettoService.attivaProgetto(id));
    }

    @PatchMapping("/{id}/concludi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProgettoResponseDTO> concludiProgetto(@PathVariable Long id)
    {
        ProgettoResponseDTO progettoConcluso = progettoService.concludiProgetto(id);
        return ResponseEntity.ok(progettoConcluso);
    }
}