package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.request.CreateTeamRequestDTO;
import model.dto.request.RegisterRequestDTO;
import model.dto.response.TeamResponseDTO;
import model.dto.response.UtenteResponseDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamsService
{
    private static final Logger LOGGER = Logger.getLogger(TeamsService.class.getName());
    private static final String API_URL = "http://localhost:8080/teams";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    private final AuthService authService;

    public TeamsService(HttpClient httpClient, ObjectMapper objectMapper, AuthService authService)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    public List<TeamResponseDTO> getTeamsManagedByAdmin ()
    {
        String token = authService.getJwtToken();
        if(token == null)
        {
            LOGGER.warning("Utente non loggato. Richiesta non effettuata.");
            return new ArrayList<>();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL+"/managed"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
            {
                LOGGER.info("Request dei team avvenuta correttamente.");
                return objectMapper.readValue(response.body(), new TypeReference<>(){});
            }
            else
            {
                LOGGER.warning(() -> "Request fallito. Status code: " + response.statusCode());
                LOGGER.fine(() -> "Risposta server: " + response.body());
                return new ArrayList<>();
            }
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di parsing JSON durante il getter dei team", e);
            return new ArrayList<>();
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Errore IOException durante il getter dei team", e);
            if(e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            return new ArrayList<>();
        }

    }

    public boolean create(CreateTeamRequestDTO requestDTO)
    {
        if(!authService.isAuthenticated())
        {
            LOGGER.warning("Tentativo di creazione utenza fallito: utente non autenticato.");
            return false;
        }

        try
        {
            LOGGER.info(() -> "Inizio creazione team:" + requestDTO.getNome());
            String body = objectMapper.writeValueAsString(requestDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/create"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authService.getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 || response.statusCode() == 201)
            {
                TeamResponseDTO newTeam = objectMapper.readValue(response.body(), TeamResponseDTO.class);
                LOGGER.info(() -> "Nuovo team creato con successo: " + newTeam.getNome());
                return true;
            }
            else
            {
                LOGGER.warning(() -> String.format("Errore create team. Status: %d. Risposta Server: %s",
                        response.statusCode(), response.body()));
            }
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di serializzazione JSON durante la creazione team", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Thread interrotto durante la creazione team", e);
            Thread.currentThread().interrupt();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di I/O (connessione backend) durante la creazione team", e);
        }

        return false;
    }
}
