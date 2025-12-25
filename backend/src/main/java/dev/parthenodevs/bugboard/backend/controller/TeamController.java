package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.CreateProgettoRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.ProgettoResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController
{
    private final TeamService teamService;

    public TeamController(TeamService teamService)
    {
        this.teamService = teamService;
    }

    @GetMapping("/managed")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsGestiti()
    {
        return ResponseEntity.ok(teamService.getTeamsGestitiDaAdmin());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TeamResponseDTO> createTeam (@Valid @RequestBody CreateTeamRequestDTO request)
    {
        TeamResponseDTO newTeam = teamService.createNewTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTeam);
    }
}