package dev.parthenodevs.bugboard.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Team", schema = "bugboard26")
public class Team
{
    @Column(nullable = false, unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTeam;

    @Column(length = 100, nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (nullable = false)
    private Utente admin;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REFRESH)
    private Set<Progetto> storiaProgetti;

    @Builder
    public Team(String nome)
    {
        this.nome = nome;
    }
}