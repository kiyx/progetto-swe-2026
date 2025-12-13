package dev.parthenodevs.bugboard.backend.service;

import dev.parthenodevs.bugboard.backend.model.Utente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

@Service
public class JWTService
{
    private static final Logger LOGGER = Logger.getLogger(JWTService.class.getName());

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // ====================================================================
    // 1. GENERAZIONE TOKEN
    // ====================================================================

    public String generateToken(Utente utente)
    {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("idutente", utente.getId());
        extraClaims.put("isAdmin", utente.getIsAdmin());

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expirationTime);

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(utente.getEmail())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();

        LOGGER.info(() -> String.format("Generato JWT per utente: %s", utente.getEmail()));
        return token;
    }

    // ====================================================================
    // 2. ESTRAZIONE E VALIDAZIONE TOKEN
    // ====================================================================

    public String extractEmail(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, Utente utente)
    {
        final String username = extractEmail(token);
        return (username.equals(utente.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException
    {
        try
        {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (IllegalArgumentException e)
        {
            throw  new JwtException("Token malformato o assente", e);
        }
    }

    // ====================================================================
    // 3. UTILITY
    // ====================================================================

    private SecretKey getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}