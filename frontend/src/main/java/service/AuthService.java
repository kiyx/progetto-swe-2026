package service;

import model.dto.request.LoginRequestDTO;
import model.dto.response.AuthResponseDTO;
import model.dto.response.UtenteResponseDTO;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import lombok.*;
import java.io.*;
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
                LOGGER.warning(() -> String.format("Login fallito per %s. Status: %d. Risposta Server: %s",
                        email, response.statusCode(), response.body()));
                return false;
            }
        }
        catch (JsonProcessingException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di serializzazione JSON durante il login", e);
            return false;
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Thread interrotto durante il login", e);
            Thread.currentThread().interrupt();
            return false;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore di I/O (connessione backend) durante il login", e);
            return false;
        }
    }

    public void refreshLocalUser(UtenteResponseDTO newUser)
    {
        this.currentUser = newUser;
        LOGGER.info("Cache utente locale aggiornata.");
    }

    public boolean isAuthenticated()
    {
        return this.jwtToken != null && this.currentUser != null;
    }

    public void logout()
    {
        String userEmail = (currentUser != null) ? currentUser.getEmail() : "Nessuno";
        this.jwtToken = null;
        this.currentUser = null;
        LOGGER.info(() -> "Logout effettuato per: " + userEmail);
    }
}