package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.RegisterRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateUtenteRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class UtenteMapper
{
    public UtenteResponseDTO toDto(Utente utente)
    {
        if(utente == null)
            return null;

        return UtenteResponseDTO.builder()
                                .id(utente.getId())
                                .nome(utente.getNome())
                                .cognome(utente.getCognome())
                                .email(utente.getEmail())
                                .isAdmin(utente.getIsAdmin())
                                .build();
    }

    public Utente toEntity(RegisterRequestDTO request)
    {
        if(request == null)
            return null;

        return Utente.builder()
                     .nome(request.getNome())
                     .cognome(request.getCognome())
                     .email(request.getEmail())
                     .password(request.getPassword())
                     .isAdmin(request.getIsAdmin() != null && request.getIsAdmin())
                     .build();
    }

    public void update(Utente entity, UpdateUtenteRequestDTO request)
    {
        if(request == null || entity == null)
            return;

        if(request.getNome() != null && !request.getNome().isBlank())
            entity.setNome(request.getNome());

        if(request.getCognome() != null && !request.getCognome().isBlank())
            entity.setCognome(request.getCognome());

        if(request.getIsAdmin() != null)
            entity.setIsAdmin(request.getIsAdmin());

        // L'eventuale cambio di password verrà gestito nel service corrispondente
    }
}