package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.CreateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateIssueRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.IssueResponseDTO;
import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Progetto;
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
                .tipo(issue.getTipo())
                .isArchiviato(issue.isArchiviato())
                .immagine(issue.getImmagine())
                .titolo(issue.getTitolo())
                .priorita(issue.getPriorita())
                .idAutore(issue.getAutore().getId())
                .nomeAutore(issue.getAutore().getNome() + " " + issue.getAutore().getCognome())
                .idProgetto(issue.getProgetto().getId())
                .nomeProgetto(issue.getProgetto().getNome())
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

    public void update(Issue entity, UpdateIssueRequestDTO  request)
    {
        if(entity == null || request == null)
            return;

        if(request.getTitolo()!= null &&  !request.getTitolo().isBlank())
            entity.setTitolo(request.getTitolo());

        if(request.getDescrizione() != null && !request.getDescrizione().isBlank())
            entity.setDescrizione(request.getDescrizione());

        if(request.getTipo() != null)
            entity.setTipo(request.getTipo());

        if(request.getStato() != null)
            entity.setStato(request.getStato());

        entity.setArchiviato(request.isArchiviato());

        if(request.getPriorita() != null)
            entity.setPriorita(request.getPriorita());

        if(request.getImmagine()!= null &&  !request.getImmagine().isBlank())
            entity.setImmagine(request.getImmagine());
    }
}