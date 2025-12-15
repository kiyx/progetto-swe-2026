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
import jakarta.transaction.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import java.util.logging.*;

@Service
public class UtenteService
{
    private static final Logger logger = Logger.getLogger(UtenteService.class.getName());

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
        logger.info(() -> "Richiesta aggiornamento password avviata per l'utente: " + email);
        final String FAILED_UPDATE = "Aggiornamento fallito per ";

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.log(Level.SEVERE, "Tentativo di aggiornamento fallito: Utente {0} non trovato nel database.", email);
                    return new UserNotFoundException("Utente non trovato");
                });

        if(request.getOldPassword() == null || request.getOldPassword().isBlank())
        {
            logger.warning(() -> FAILED_UPDATE + email + ": Vecchia password mancante.");
            throw new InvalidFieldException("È necessario inserire la vecchia password per procedere.");
        }

        if(!passwordEncoder.matches(request.getOldPassword(), utente.getPassword()))
        {
            logger.warning(() -> FAILED_UPDATE + email + ": La vecchia password non corrisponde.");
            throw new PasswordNotMatchingException("La vecchia password non è corretta.");
        }

        if(request.getPassword() != null && !request.getPassword().isBlank())
        {
            utente.setPassword(passwordEncoder.encode(request.getPassword()));
            logger.info(() -> "Password aggiornata con successo per l'utente: " + email);
        }
        else
        {
            logger.warning(() -> FAILED_UPDATE + email + ": La nuova password è vuota.");
            throw new InvalidFieldException("La nuova password non può essere vuota.");
        }

        return utenteMapper.toDto(utenteRepository.save(utente));
    }

    @Transactional
    public UtenteResponseDTO registerUtente(RegisterRequestDTO request)
    {
        logger.info(() -> "Inizio procedura di registrazione nuovo utente: " + request.getEmail());

        if(utenteRepository.existsByEmail(request.getEmail()))
        {
            logger.warning(() -> "Registrazione fallita: L'email " + request.getEmail() + " è già presente nel sistema.");
            throw new EmailAlreadyUsedException("L'indirizzo email " + request.getEmail() + " è già associato a un account.");
        }

        Utente nuovoUtente = utenteMapper.toEntity(request);

        String encodedPwd = passwordEncoder.encode(request.getPassword());
        nuovoUtente.setPassword(encodedPwd);

        Utente utenteSalvato = utenteRepository.save(nuovoUtente);

        logger.info(() -> String.format("Utente registrato con successo. ID: %d, Email: %s, Admin: %b",
                utenteSalvato.getId(), utenteSalvato.getEmail(), utenteSalvato.getIsAdmin()));

        return utenteMapper.toDto(utenteSalvato);
    }
}