package dev.parthenodevs.bugboard.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
public class Team
{
    @Column(name = "IdTeam", nullable = false, unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long idTeam;

    @Column(name = "Nome", length = 100, nullable = false)
    @Getter
    @Setter
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "IdAdmin" )
    @Getter
    @Setter
    private Utente admin;
}
