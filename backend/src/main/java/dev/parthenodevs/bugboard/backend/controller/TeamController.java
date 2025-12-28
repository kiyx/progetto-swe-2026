package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/teams")
@SuppressWarnings("NullableProblems")
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

    @GetMapping("/member/{id}")
    public ResponseEntity<List<UtenteResponseDTO>> getMembri(@PathVariable Long id)
    {
        return ResponseEntity.ok(teamService.getTeamsMembers(id));
    }

    @GetMapping("/notmember/{id}")
    public ResponseEntity<List<UtenteResponseDTO>> getNotMembri(@PathVariable Long id)
    {
        return ResponseEntity.ok(teamService.getTeamsNotMembers(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TeamResponseDTO> createTeam (@Valid @RequestBody CreateTeamRequestDTO request)
    {
        TeamResponseDTO newTeam = teamService.createNewTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTeam);
    }

    @PatchMapping("/{id}/add")
    public ResponseEntity<Void> addMembri(@PathVariable Long id, @RequestParam List<Long> userIds)
    {
        teamService.addMembri(id, userIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/remove")
    public ResponseEntity<Void> removeMembri(@PathVariable Long id, @RequestParam List<Long> userIds)
    {
        teamService.removeMembri(id, userIds);
        return ResponseEntity.ok().build();
    }
}