package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.CreateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.CreateTeamRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.IssueResponseDTO;
import dev.parthenodevs.bugboard.backend.dto.response.TeamResponseDTO;
import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper
{
    public IssueResponseDTO toDto(Issue issue)
    {
        if(issue == null)
            return null;

        return IssueResponseDTO.builder()
                .id(issue.getId())
                .stato(issue.getStato())
                .descrizione(issue.getDescrizione())
                .idAutore(issue.getAutore().getId())
                .idProgetto(issue.getProgetto().getId())
                .tipo(issue.getTipo())
                .isArchiviato(issue.isArchiviato())
                .immagine(issue.getImmagine())
                .titolo(issue.getTitolo())
                .priorita(issue.getPriorita())
                .build();

    }

    public Issue toEntity(CreateIssueRequestDTO request, Utente autore, Progetto progetto)
    {
        if(request == null)
            return null;

        return Issue.builder()
                .autore(autore)
                .descrizione(request.getDescrizione())
                .immagine(request.getImmagine())
                .priorita(request.getPriorita())
                .progetto(progetto)
                .tipo(request.getTipo())
                .titolo(request.getTitolo())
                .build();
    }

}