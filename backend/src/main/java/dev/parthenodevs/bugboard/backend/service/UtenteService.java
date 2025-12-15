package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.dto.request.RegisterRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.request.UpdateUtenteRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.UtenteResponseDTO;
import dev.parthenodevs.bugboard.backend.exception.EmailAlreadyUsedException;
import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import dev.parthenodevs.bugboard.backend.exception.PasswordNotMatchingException;
import dev.parthenodevs.bugboard.backend.exception.UserNotFoundException;
import dev.parthenodevs.bugboard.backend.mapper.UtenteMapper;
import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

@Service
public class UtenteService
{
    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository utenteRepository, UtenteMapper utenteMapper, PasswordEncoder passwordEncoder)
    {
        this.utenteRepository = utenteRepository;
        this.utenteMapper = utenteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtenteResponseDTO updateUtente(String email, UpdateUtenteRequestDTO request)
    {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato"));

        if(request.getOldPassword() == null || request.getOldPassword().isBlank())
            throw new InvalidFieldException("È necessario inserire la vecchia password per procedere.");

        if(!passwordEncoder.matches(request.getOldPassword(), utente.getPassword()))
            throw new PasswordNotMatchingException("La vecchia password non è corretta.");

        if(request.getPassword() != null && !request.getPassword().isBlank())
            utente.setPassword(passwordEncoder.encode(request.getPassword()));
        else
            throw new InvalidFieldException("La nuova password non può essere vuota.");

        return utenteMapper.toDto(utenteRepository.save(utente));
    }

    @Transactional
    public UtenteResponseDTO registerUtente(RegisterRequestDTO request)
    {
        if(utenteRepository.existsByEmail(request.getEmail()))
        {
            throw new EmailAlreadyUsedException("L'indirizzo email " + request.getEmail() + " è già associato a un account.");
        }

        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(request.getNome());
        nuovoUtente.setCognome(request.getCognome());
        nuovoUtente.setEmail(request.getEmail());

        nuovoUtente.setPassword(passwordEncoder.encode(request.getPassword()));
        nuovoUtente.setIsAdmin(request.getIsAdmin() != null && request.getIsAdmin());
        
        Utente utenteSalvato = utenteRepository.save(nuovoUtente);
        return utenteMapper.toDto(utenteSalvato);
    }
}