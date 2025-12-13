package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.*;
import model.dto.request.LoginRequestDTO;
import model.dto.response.AuthResponseDTO;
import model.dto.response.UtenteResponseDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.logging.*;

public class AuthService
{
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static final String API_URL = "http://localhost:8080/auth/login";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Getter
    private String jwtToken;
    @Getter
    private UtenteResponseDTO currentUser;

    public AuthService(HttpClient httpClient, ObjectMapper objectMapper)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public boolean login(String email, String password)
    {
        try
        {
            LOGGER.info(() -> "Tentativo di login per: " + email);

            LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
            String body = objectMapper.writeValueAsString(loginRequest);

            HttpRequest request = HttpRequest.newBuilder()
                                 .uri(URI.create(API_URL))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
            {
                AuthResponseDTO authResponse = objectMapper.readValue(response.body(), AuthResponseDTO.class);

                this.jwtToken = authResponse.getJwtToken();
                this.currentUser = authResponse.getUtente();

                LOGGER.info(() -> "Login riuscito per utente: " + currentUser.getNome() + " " + currentUser.getCognome());
                return true;
            }
            else
            {
                LOGGER.warning(() -> "Login fallito. Status code: " + response.statusCode());
                LOGGER.fine(() -> "Risposta server: " + response.body());
                return false;
            }
        }
        catch (JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di parsing JSON durante il login", e);
            return false;
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di connessione con il backend", e);
            if(e instanceof InterruptedException)
                Thread.currentThread().interrupt();

            return false;
        }
    }

    public void logout()
    {
        String userEmail = (currentUser != null) ? currentUser.getEmail() : "Nessuno";
        this.jwtToken = null;
        this.currentUser = null;
        LOGGER.info(() -> "Logout effettuato per: " + userEmail);
    }

    public boolean isAuthenticated()
    {
        return this.jwtToken != null && this.currentUser != null;
    }
}