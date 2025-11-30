package dev.parthenodevs.bugboard.backend.mapper;

import dev.parthenodevs.bugboard.backend.dto.request.RegisterRequestDTO;
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
}