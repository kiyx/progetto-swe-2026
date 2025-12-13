package dev.parthenodevs.bugboard.backend.config;

import dev.parthenodevs.bugboard.backend.model.Utente;
import dev.parthenodevs.bugboard.backend.repository.UtenteRepository;
import dev.parthenodevs.bugboard.backend.service.JWTService;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;
import java.io.*;
import java.util.logging.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JWTService jwtService;
    private final UtenteRepository utenteRepository;

    public JwtAuthenticationFilter(JWTService jwtService, UtenteRepository utenteRepository)
    {
        this.jwtService = jwtService;
        this.utenteRepository = utenteRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try
        {
            userEmail = jwtService.extractEmail(jwt);
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                UserDetails userDetails = (UserDetails) utenteRepository.findByEmail(userEmail).orElse(null);
                if(userDetails != null && jwtService.isTokenValid(jwt, (Utente) userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    LOGGER.info(()-> "Autenticazione JWT riuscita per: " + userEmail);
                }
            }
        }
        catch (ExpiredJwtException e)
        {
            LOGGER.log(Level.WARNING, "Tentativo di accesso fallito: Token JWT scaduto.", e);
        }
        catch (MalformedJwtException e)
        {
            LOGGER.log(Level.WARNING, "Tentativo di accesso fallito: Token JWT malformato o non valido.", e);
        }
        catch (JwtException e)
        {
            LOGGER.log(Level.WARNING, "Tentativo di accesso fallito: Errore generico JWT.", e);
        }
        filterChain.doFilter(request, response);

    }
}
