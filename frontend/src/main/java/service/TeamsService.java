package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.request.CreateTeamRequestDTO;
import model.dto.response.TeamResponseDTO;
import model.dto.response.UtenteResponseDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TeamsService
{
    private static final Logger LOGGER = Logger.getLogger(TeamsService.class.getName());
    private static final String API_URL = "http://localhost:8080/teams";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

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
        if(authService.isNotAuthenticated())
            return new ArrayList<>();

        HttpRequest request = buildGetRequest(API_URL + "/managed");
        return executeListRequest(request, new TypeReference<>() {});
    }

    public List<UtenteResponseDTO> getTeamsNotMembers(Long teamId)
    {
        if(authService.isNotAuthenticated())
            return new ArrayList<>();

        HttpRequest request = buildGetRequest(API_URL + "/" + teamId + "/non-members");
        return executeListRequest(request, new TypeReference<>() {});
    }

    public List<UtenteResponseDTO> getTeamMembers(Long teamId)
    {
        if(authService.isNotAuthenticated())
            return new ArrayList<>();

        HttpRequest request = buildGetRequest(API_URL + "/" + teamId + "/members");
        return executeListRequest(request, new TypeReference<>() {});
    }

    public boolean create(CreateTeamRequestDTO requestDTO)
    {
        if(authService.isNotAuthenticated())
            return false;

        try
        {
            String body = objectMapper.writeValueAsString(requestDTO);
            HttpRequest request = buildPostRequest(body);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(isSuccess(response.statusCode()))
            {
                LOGGER.info(() -> "Team creato con successo: " + requestDTO.getNome());
                return true;
            }
            logError("Create Team", response);
        }
        catch(Exception e)
        {
            logException("Create Team", e);
        }
        return false;
    }

    public boolean aggiungiMembri(Long teamId, List<Long> userIds)
    {
        return executeMemberOperation(teamId, userIds, "add");
    }

    public boolean rimuoviMembri(Long teamId, List<Long> userIds)
    {
        return executeMemberOperation(teamId, userIds, "remove");
    }


    private boolean executeMemberOperation(Long teamId, List<Long> userIds, String operation)
    {
        if(authService.isNotAuthenticated() || userIds == null || userIds.isEmpty())
            return false;

        try
        {
            String idsParam = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            String url = String.format("%s/%d/%s?userIds=%s", API_URL, teamId, operation, idsParam);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(AUTHORIZATION, BEARER + authService.getJwtToken())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(isSuccess(response.statusCode()))
            {
                LOGGER.info(() -> "Operazione " + operation + " su team " + teamId + " riuscita.");
                return true;
            }
            logError("Member Operation " + operation, response);
        }
        catch(Exception e)
        {
            logException("Member Operation " + operation, e);
        }
        return false;
    }

    private <T> List<T> executeListRequest(HttpRequest request, TypeReference<List<T>> typeRef)
    {
        try
        {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(isSuccess(response.statusCode()))
                return objectMapper.readValue(response.body(), typeRef);
            logError("Fetch List", response);
        }
        catch(Exception e)
        {
            logException("Fetch List", e);
        }
        return new ArrayList<>();
    }

    private HttpRequest buildGetRequest(String url)
    {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + authService.getJwtToken())
                .GET()
                .build();
    }

    private HttpRequest buildPostRequest(String body)
    {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/teams/create"))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + authService.getJwtToken())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private boolean isSuccess(int code)
    {
        return code >= 200 && code < 300;
    }

    private void logError(String context, HttpResponse<String> response)
    {
        LOGGER.warning(() -> String.format("[%s] Fallito. Code: %d Body: %s", context, response.statusCode(), response.body()));
    }

    private void logException(String context, Exception e)
    {
        if(e instanceof InterruptedException)
            Thread.currentThread().interrupt();
        LOGGER.log(Level.SEVERE, () -> "Eccezione in " + context + ": " + e.getMessage());
    }
}