package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.request.CreateTeamRequestDTO;
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
    public static final String RICHIESTA_NON_EFFETTUATA = "Utente non loggato. Richiesta non effettuata.";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String REQUEST_FALLITO_STATUS_CODE = "Request fallito. Status code: ";
    public static final String RISPOSTA_SERVER = "Risposta server: ";
    public static final String PATCH = "PATCH";
    private static final String JWT_BEARER = "Bearer ";
    private static final String JWT_AUTH = "Authorization";

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
            LOGGER.warning(RICHIESTA_NON_EFFETTUATA);
            return new ArrayList<>();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/managed"))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + token)
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
                LOGGER.warning(() -> REQUEST_FALLITO_STATUS_CODE + response.statusCode());
                LOGGER.fine(() -> RISPOSTA_SERVER + response.body());
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

    public List<UtenteResponseDTO> getTeamsNotMembers (Long id)
    {
        String token = authService.getJwtToken();
        if(token == null)
        {
            LOGGER.warning(RICHIESTA_NON_EFFETTUATA);
            return new ArrayList<>();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/notmember" + "/" + id))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + token)
                    .GET()
                    .build();

            return getUtenteResponseDTOS(request);
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di parsing JSON durante il getter dei membri", e);
            return new ArrayList<>();
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Errore IOException durante il getter dei membri", e);
            if(e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            return new ArrayList<>();
        }

    }

    private List<UtenteResponseDTO> getUtenteResponseDTOS(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200)
        {
            LOGGER.info("Request dei membri avvenuta correttamente.");
            return objectMapper.readValue(response.body(), new TypeReference<>(){});
        }
        else
        {
            LOGGER.warning(() -> REQUEST_FALLITO_STATUS_CODE + response.statusCode());
            LOGGER.fine(() -> RISPOSTA_SERVER + response.body());
            return new ArrayList<>();
        }
    }

    public List<UtenteResponseDTO> getTeamMembers(Long id)
    {
        String token = authService.getJwtToken();
        if(token == null)
        {
            LOGGER.warning(RICHIESTA_NON_EFFETTUATA);
            return new ArrayList<>();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/member" + "/" + id))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + token)
                    .GET()
                    .build();

            return getUtenteResponseDTOS(request);
        }
        catch(JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di parsing JSON durante il getter dei membri", e);
            return new ArrayList<>();
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Errore IOException durante il getter dei membri", e);
            if(e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            return new ArrayList<>();
        }

    }

    public boolean aggiungiMembri(Long teamId, List<Long> userIds)
    {
        if(authService.isNotAuthenticated() || userIds == null || userIds.isEmpty())
            return false;

        String idsParam = String.join(",", userIds.stream().map(String::valueOf).toArray(String[]::new));
        String url = API_URL + "/" + teamId + "/add?userIds=" + idsParam;

        return executeWriteRequest(url, PATCH, null, "assegnazione multipla issue");
    }

    public boolean rimuoviMembri(Long teamId, List<Long> userIds)
    {
        if(authService.isNotAuthenticated() || userIds == null || userIds.isEmpty())
            return false;

        String idsParam = String.join(",", userIds.stream().map(String::valueOf).toArray(String[]::new));
        String url = API_URL + "/" + teamId + "/remove?userIds=" + idsParam;

        return executeWriteRequest(url, PATCH, null, "assegnazione multipla issue");
    }


    public boolean create(CreateTeamRequestDTO requestDTO)
    {
        if(authService.isNotAuthenticated())
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
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + authService.getJwtToken())
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

    private boolean executeWriteRequest(String url, String method, Object bodyPayload, String operationDescription)
    {
        if(authService.isNotAuthenticated())
        {
            LOGGER.warning(() -> "Tentativo di " + operationDescription + " senza autenticazione.");
            return false;
        }

        try
        {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken());

            HttpRequest.BodyPublisher publisher;

            if(bodyPayload != null)
            {
                String json = objectMapper.writeValueAsString(bodyPayload);
                publisher = HttpRequest.BodyPublishers.ofString(json);
                builder.header("Content-Type", "application/json");
            }
            else
                publisher = HttpRequest.BodyPublishers.noBody();

            builder.method(method, publisher);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() >= 200 && response.statusCode() < 300)
            {
                LOGGER.info(() -> "Operazione riuscita: " + operationDescription);
                return true;
            }
            else
            {
                LOGGER.warning(() -> "Fallimento operazione " + operationDescription + ". Status: " + response.statusCode());
                return false;
            }
        }
        catch(Exception e)
        {
            handleException(e, operationDescription);
        }

        return false;
    }

    private void handleException(Exception e, String context)
    {
        switch (e)
        {
            case InterruptedException ignored ->
            {
                LOGGER.log(Level.SEVERE, e, () -> "Thread interrotto durante: " + context);
                Thread.currentThread().interrupt();
            }
            case JsonProcessingException ignored -> LOGGER.log(Level.SEVERE, e, () -> "Errore JSON durante: " + context);

            case IOException ignored -> LOGGER.log(Level.SEVERE, e, () -> "Errore I/O Backend durante: " + context);

            case null, default -> LOGGER.log(Level.SEVERE, e, () -> "Errore generico durante: " + context);
        }
    }
}
