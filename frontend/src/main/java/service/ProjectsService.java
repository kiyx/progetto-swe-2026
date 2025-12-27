package service;

import model.dto.request.CreateProgettoRequestDTO;
import model.dto.response.ProgettoResponseDTO;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.logging.*;

public class ProjectsService
{
    private static final Logger logger = Logger.getLogger(ProjectsService.class.getName());
    private static final String API_URL = "http://localhost:8080/progetti";
    private static final String JWT_BEARER = "Bearer ";
    private static final String JWT_AUTH = "Authorization";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public ProjectsService(HttpClient httpClient, ObjectMapper objectMapper, AuthService authService)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    public List<ProgettoResponseDTO> getProgettiGestiti()
    {
        if(authService.isNotAuthenticated())
        {
            logger.warning(() -> "Tentativo di recupero progetti senza autenticazione.");
            return Collections.emptyList();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/admin"))
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            else
                logger.warning(() -> "Errore recupero progetti gestiti. Status: " + response.statusCode());
        }
        catch(JsonProcessingException e)
        {
            logger.log(Level.SEVERE, "Errore di serializzazione JSON durante recupero progetti", e);
        }
        catch(InterruptedException e)
        {
            logger.log(Level.SEVERE, "Thread interrotto durante recupero progetti", e);
            Thread.currentThread().interrupt();
        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, "Errore di I/O (connessione backend) durante recupero progetti", e);
        }

        return Collections.emptyList();
    }

    public boolean createProgetto(CreateProgettoRequestDTO requestDTO)
    {
        if(authService.isNotAuthenticated())
        {
            logger.warning(() -> "Tentativo di creazione senza autenticazione.");
            return false;
        }

        try
        {
            String body = objectMapper.writeValueAsString(requestDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 201)
            {
                logger.info(() -> "Progetto creato con successo: " + requestDTO.getNome());
                return true;
            }
            else
            {
                logger.warning(() -> "Errore creazione progetto. Status: " + response.statusCode() + " | Body: " + response.body());
                return false;
            }
        }
        catch(JsonProcessingException e)
        {
            logger.log(Level.SEVERE, "Errore di serializzazione JSON durante creazione progetto", e);
        }
        catch(InterruptedException e)
        {
            logger.log(Level.SEVERE, "Thread interrotto durante creazione progetto", e);
            Thread.currentThread().interrupt();
        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, "Errore di I/O (connessione backend) durante creazione progetto", e);
        }

        return false;
    }

    public boolean attivaProgetto(Long idProgetto)
    {
        if(authService.isNotAuthenticated())
            return false;

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + idProgetto + "/attiva"))
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) return true;

            logger.warning(() -> "Errore attivazione: " + response.statusCode());
            return false;
        }
        catch(JsonProcessingException e)
        {
            logger.log(Level.SEVERE, "Errore di serializzazione JSON durante attivazione progetto", e);
        }
        catch(InterruptedException e)
        {
            logger.log(Level.SEVERE, "Thread interrotto durante attivazione progetto", e);
            Thread.currentThread().interrupt();
        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, "Errore di I/O (connessione backend) durante attivazione progetto", e);
        }

        return false;
    }

    public boolean concludiProgetto(Long idProgetto)
    {
        if(authService.isNotAuthenticated())
            return false;

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + idProgetto + "/concludi"))
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
            {
                logger.info(() -> "Progetto " + idProgetto + " concluso.");
                return true;
            }
            else
            {
                logger.warning(() -> "Errore chiusura progetto. Status: " + response.statusCode());
                return false;
            }
        }
        catch(JsonProcessingException e)
        {
            logger.log(Level.SEVERE, "Errore di serializzazione JSON durante conclusione progetto", e);
        }
        catch(InterruptedException e)
        {
            logger.log(Level.SEVERE, "Thread interrotto durante conclusione progetto", e);
            Thread.currentThread().interrupt();
        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, "Errore di I/O (connessione backend) durante conclusione progetto", e);
        }

        return false;
    }
}