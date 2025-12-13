package dev.parthenodevs.bugboard.backend.controller;

import dev.parthenodevs.bugboard.backend.dto.request.LoginRequestDTO;
import dev.parthenodevs.bugboard.backend.dto.response.AuthResponseDTO;
import dev.parthenodevs.bugboard.backend.service.AuthService;
import jakarta.validation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@SuppressWarnings("NullableProblems")
public class AuthController
{
    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

   @PostMapping("/login")
   public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO)
   {
           AuthResponseDTO response = authService.login(requestDTO);
           return new ResponseEntity<>(response, HttpStatus.OK);
   }


}