package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.response.TeamResponseDTO;

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

    public List<TeamResponseDTO> getTeamsManagedByAdmin()
    {
       LOGGER.info(() -> "[MOCK] Recupero lista Team finti...");

        List<TeamResponseDTO> mockTeams = new ArrayList<>();

        // Team 1
        mockTeams.add(new TeamResponseDTO(
                1L,                 // ID
                "Team Backend",     // Nome
                100L,               // ID Admin
                "Admin User",       // Nome Admin
                5,                  // N. Membri
                2                   // N. Progetti
        ));

        // Team 2
        mockTeams.add(new TeamResponseDTO(
                2L,
                "Team Frontend",
                100L,
                "Admin User",
                3,
                1
        ));

        // Team 3
        mockTeams.add(new TeamResponseDTO(
                3L,
                "Team DevOps",
                100L,
                "Admin User",
                2,
                0
        ));

        return mockTeams;
    }
    public List<TeamResponseDTO> getTeams ()
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
}
