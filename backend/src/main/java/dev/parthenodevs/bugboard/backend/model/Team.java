package dev.parthenodevs.bugboard.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Utente", schema = "bugboard26")
public class Team
{
    @Column(name = "idteam", nullable = false, unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTeam;

    @Column(name = "Nome", length = 100, nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "idadmin" )
    private Utente admin;
}
