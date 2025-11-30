package dev.parthenodevs.bugboard.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO
{
    private Long id;
    private String nome;

    private Long idAdmin;
    private String nomeAdmin;

    private int numeroMembri;
    private int numeroProgetti;
}