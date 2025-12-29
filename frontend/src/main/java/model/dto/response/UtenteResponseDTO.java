package model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenteResponseDTO
{
    private Long id;
    private String email;
    private String nome;
    private String cognome;
    private Boolean isAdmin;
}