package service;

import model.dto.request.RegisterRequestDTO;
import model.dto.request.UpdateUtenteRequestDTO;
import model.dto.response.UtenteResponseDTO;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.logging.*;

public class UtenteService
{
    private static final Logger LOGGER = Logger.getLogger(UtenteService.class.getName());
    private static final String API_BASE_URL = "http://localhost:8080/utente";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public UtenteService(HttpClient httpClient, ObjectMapper objectMapper, AuthService authService)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    public Optional<UtenteResponseDTO> updateMe(UpdateUtenteRequestDTO requestDTO)
    {
        if(authService.isNotAuthenticated())
        {
            LOGGER.warning("Tentativo di aggiornamento password fallito: utente non autenticato.");
            return Optional.empty();
        }

        try
        {
            LOGGER.info(() -> "Inizio aggiornamento password per utente: " + authService.getCurrentUser().getEmail());
            String body = objectMapper.writeValueAsString(requestDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/me"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authService.getJwtToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
            {
                UtenteResponseDTO updatedUser = objectMapper.readValue(response.body(), UtenteResponseDTO.class);
                LOGGER.info(() -> "Password aggiornata con successo");
                return Optional.of(updatedUser);
            }
            else
            {
                LOGGER.warning(() -> String.format("Errore updateMe. Status: %d. Risposta Server: %s",
                        response.statusCode(), response.body()));
            }
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di serializzazione JSON durante l'aggiornamento password", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Thread interrotto durante l'aggiornamento password", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di I/O (connessione backend) durante l'aggiornamento password", e);
        }
        return Optional.empty();
    }

    public boolean register(RegisterRequestDTO requestDTO)
    {
        if(authService.isNotAuthenticated())
        {
            LOGGER.warning("Tentativo di creazione utenza fallito: utente non autenticato.");
            return false;
        }

        try
        {
            LOGGER.info(() -> "Inizio creazione utenza per " + requestDTO.getEmail());
            String body = objectMapper.writeValueAsString(requestDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/register"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authService.getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 || response.statusCode() == 201)
            {
                UtenteResponseDTO newUser = objectMapper.readValue(response.body(), UtenteResponseDTO.class);
                LOGGER.info(() -> "Nuovo utente creato con successo: " + newUser.getEmail());
                return true;
            }
            else
            {
                LOGGER.warning(() -> String.format("Errore register. Status: %d. Risposta Server: %s",
                        response.statusCode(), response.body()));
            }
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di serializzazione JSON durante la registrazione utente", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Thread interrotto durante la registrazione utente", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di I/O (connessione backend) durante la registrazione utente", e);
        }

        return false;
    }
}