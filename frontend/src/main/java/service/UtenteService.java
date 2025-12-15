package service;

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
        if(!authService.isAuthenticated())
        {
            LOGGER.warning("Tentativo di aggiornamento profilo fallito: utente non autenticato.");
            return Optional.empty();
        }

        try
        {
            LOGGER.info(() -> "Inizio aggiornamento profilo per utente: " + authService.getCurrentUser().getEmail());
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
                LOGGER.info(() -> "Profilo aggiornato con successo. Nuovo nome: " + updatedUser.getNome() + " " + updatedUser.getCognome());
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
            LOGGER.log(Level.SEVERE, "Errore di serializzazione JSON durante l'aggiornamento profilo", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Thread interrotto durante l'aggiornamento profilo", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di I/O (connessione backend) durante l'aggiornamento profilo", e);
        }
        return Optional.empty();
    }



    public Optional<UtenteResponseDTO> register()
    {

    }
}