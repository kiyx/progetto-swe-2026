package dev.parthenodevs.bugboard.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO
{
    @NotBlank(message = "L'email è obbligatoria !")
    @Email(message = "Il formato dell'email non è valido")
    private String email;

    @NotBlank(message = "Il nome non può essere vuoto !")
    @Size(min = 2, max = 100, message = "Il nome deve avere tra i 2 e i 100 caratteri")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto !")
    @Size(min = 2, max = 100, message = "Il cognome deve avere tra i 2 e i 100 caratteri")
    private String cognome;

    @NotBlank(message = "La password è obbligatoria!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "La password deve avere almeno 8 caratteri, inclusi lettere e numeri")
    private String password;

    private Boolean isAdmin;
}