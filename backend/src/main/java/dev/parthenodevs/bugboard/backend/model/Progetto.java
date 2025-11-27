package dev.parthenodevs.bugboard.backend.model;

import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import dev.parthenodevs.bugboard.backend.model.enums.StatoProgetto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Progetto", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Progetto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprogetto", nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoProgetto stato;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idteam", nullable = false)
    private Team team;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idadmin", nullable = false)
    private Utente admin;

    @Builder
    public Progetto(String nome, StatoProgetto stato, Team team, Utente admin)
    {
        if(nome == null || nome.isBlank())
            throw new InvalidFieldException("Nome obbligatorio");
        if(stato == null)
            throw new InvalidFieldException("Stato obbligatorio");
        if(team == null)
            throw new InvalidFieldException("Team obbligatorio");
        if(admin == null)
            throw new InvalidFieldException("Admin obbligatorio");

        this.nome = nome;
        this.stato = stato;
        this.team = team;
        this.admin = admin;
    }
}