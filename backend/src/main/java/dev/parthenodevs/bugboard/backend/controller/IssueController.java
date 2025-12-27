package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.CreateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.IssueResponseDTO;
import dev.parthenodevs.bugboard.backend.service.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/issues")
@SuppressWarnings("NullableProblems")
public class IssueController
{
    private static final Logger LOGGER = Logger.getLogger(IssueController.class.getName());
    private final IssueService issueService;

    public IssueController(IssueService issueService)
    {
        this.issueService = issueService;
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getAllIssues()
    {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesAssignedTo(@PathVariable Long userId)
    {
        return ResponseEntity.ok(issueService.getIssuesByAssignee(userId));
    }

    @GetMapping("/author/{userId}")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesCreatedBy(@PathVariable Long userId)
    {
        return ResponseEntity.ok(issueService.getIssuesByAuthor(userId));
    }

    @PostMapping
    public ResponseEntity<Void> createIssue(@RequestBody CreateIssueRequestDTO request)
    {
        issueService.createIssue(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateIssue(@PathVariable Long id, @RequestBody UpdateIssueRequestDTO request)
    {
        issueService.updateIssue(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveIssue(@PathVariable Long id)
    {
        issueService.resolveIssue(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Void> archiveIssue(@PathVariable Long id)
    {
        issueService.archiveIssue(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<Void> assignIssue(@PathVariable Long id, @RequestParam List<Long> userIds)
    {
        LOGGER.info(() -> "Richiesta assegnazione issue " + id + " agli utenti: " + userIds);
        issueService.assignIssue(id, userIds);
        return ResponseEntity.ok().build();
    }
}