package dev.parthenodevs.bugboard.backend.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
    {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
        );

        // 3. Disabilita la gestione delle sessioni basata su cookie (necessario per JWT stateless)
        // http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Nota: sessionCreationPolicy la aggiungerai in una fase successiva, per ora la lasciamo implicita.

        return http.build();
    }

    // NOTA: Se hai un metodo PasswordEncoderConfig.passwordEncoder(),
    // assicurati che sia ancora iniettato correttamente.
}