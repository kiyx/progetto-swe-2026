package dev.parthenodevs.bugboard.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO
{
    @NotBlank(message = "L'email è obbligatoria !")
    @Email(message = "Il formato dell'email non è valido")
    private String email;

    @NotBlank(message = "La password è obbligatoria!")
    private String password;
}