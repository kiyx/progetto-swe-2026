package model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUtenteRequestDTO
{
    @Size(min = 2, max = 100)
    private String nome;

    @Size(min = 2, max = 100)
    private String cognome;

    // Se è null, il service ignora l'update della password.
    // Ma SE viene inviata, deve rispettare la sicurezza.
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "La nuova password deve avere almeno 8 caratteri, lettere e numeri")
    private String password;

    private Boolean isAdmin;
}