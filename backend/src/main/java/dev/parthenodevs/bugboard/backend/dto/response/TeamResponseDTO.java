package dev.parthenodevs.bugboard.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamResponseDTO
{
    private Long id;
    private String nome;
    private Long idAdmin;
}
