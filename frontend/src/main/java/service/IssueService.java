package service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import model.dto.request.CreateIssueRequestDTO;
import model.dto.request.UpdateIssueRequestDTO;
import model.dto.response.IssueResponseDTO;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class IssueService
{
    private static final Logger logger = Logger.getLogger(IssueService.class.getName());
    private static final String API_URL = "http://localhost:8080/api/issues";
    private static final String JWT_BEARER = "Bearer ";
    private static final String JWT_AUTH = "Authorization";
    public static final String PATCH = "PATCH";
    public static final String PUT = "PUT";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public IssueService(HttpClient httpClient, ObjectMapper objectMapper, AuthService authService)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    public List<IssueResponseDTO> getAllIssues()
    {
        return executeListGetRequest(API_URL, "recupero globale issues");
    }

    public List<IssueResponseDTO> getIssuesAssignedTo(Long userId)
    {
        return executeListGetRequest(API_URL + "/assignee/" + userId, "recupero issues assegnate");
    }

    public List<IssueResponseDTO> getIssuesCreatedBy(Long userId)
    {
        return executeListGetRequest(API_URL + "/author/" + userId, "recupero issues create");
    }

    public boolean createIssue(CreateIssueRequestDTO requestDTO)
    {
        return executeWriteRequest(API_URL, "POST", requestDTO, "creazione issue");
    }

    public boolean updateIssue(Long issueId, UpdateIssueRequestDTO requestDTO)
    {
        return executeWriteRequest(API_URL + "/" + issueId, PUT, requestDTO, "aggiornamento issue");
    }

    public boolean resolveIssue(Long issueId)
    {
        return executeWriteRequest(API_URL + "/" + issueId + "/resolve", PATCH, null, "risoluzione issue");
    }

    public boolean archiveIssue(Long issueId)
    {
        return executeWriteRequest(API_URL + "/" + issueId + "/archive", PATCH, null, "archiviazione issue");
    }

    public boolean assignIssue(Long issueId, List<Long> userIds)
    {
        if(authService.isNotAuthenticated() || userIds == null || userIds.isEmpty())
            return false;

        String idsParam = String.join(",", userIds.stream().map(String::valueOf).toArray(String[]::new));
        String url = API_URL + "/" + issueId + "/assign?userIds=" + idsParam;

        return executeWriteRequest(url, PATCH, null, "assegnazione multipla issue");
    }

    private List<IssueResponseDTO> executeListGetRequest(String url, String operationDescription)
    {
        if(authService.isNotAuthenticated())
        {
            logger.warning(() -> "Tentativo di " + operationDescription + " senza autenticazione.");
            return Collections.emptyList();
        }

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(JWT_AUTH, JWT_BEARER + authService.getJwtToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            else
                logger.warning(() -> "Errore durante " + operationDescription + ". Status: " + response.statusCode());
        }
        catch(Exception e)
        {
            handleException(e, operationDescription);
        }

        return Collections.emptyList();
    }

    private boolean executeWriteRequest(String url, String method, Object bodyPayload, String operationDescription)
    {
        if(authService.isNotAuthenticated())
        {
            logger.warning(() -> "Tentativo di " + operationDescription + " senza autenticazione.");
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
                logger.info(() -> "Operazione riuscita: " + operationDescription);
                return true;
            }
            else
            {
                logger.warning(() -> "Fallimento operazione " + operationDescription + ". Status: " + response.statusCode());
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
                logger.log(Level.SEVERE, e, () -> "Thread interrotto durante: " + context);
                Thread.currentThread().interrupt();
            }
            case JsonProcessingException ignored -> logger.log(Level.SEVERE, e, () -> "Errore JSON durante: " + context);

            case IOException ignored -> logger.log(Level.SEVERE, e, () -> "Errore I/O Backend durante: " + context);

            case null, default -> logger.log(Level.SEVERE, e, () -> "Errore generico durante: " + context);
        }
    }
}