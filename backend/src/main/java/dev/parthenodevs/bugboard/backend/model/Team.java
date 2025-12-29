package dev.parthenodevs.bugboard.backend.model;

import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import jakarta.persistence.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Entity
@Table(name = "Team", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Team implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idteam", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String nome;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "idadmin", nullable = false)
    private Utente admin;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "assignedTeams", fetch = FetchType.LAZY)
    private Set<Utente> membri = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "team", cascade = CascadeType.REFRESH)
    @SuppressWarnings("java:S1948")
    private Set<Progetto> progetti = new HashSet<>();

    @Builder
    public Team(String nome, Utente admin)
    {
        if(nome == null || nome.isBlank())
            throw new InvalidFieldException("Nome team obbligatorio");
        if(admin == null)
            throw new InvalidFieldException("Admin team obbligatorio");

        this.nome = nome;
        this.admin = admin;
    }

    public void addProgetto(Progetto progetto)
    {
        this.progetti.add(progetto);
        progetto.setTeam(this);
    }
}