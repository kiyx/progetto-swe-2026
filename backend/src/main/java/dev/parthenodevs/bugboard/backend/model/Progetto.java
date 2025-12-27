package dev.parthenodevs.bugboard.backend.model;

import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Progetto", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Progetto implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprogetto", nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private StatoProgetto stato;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idteam", nullable = false)
    @SuppressWarnings("java:S1948")
    private Team team;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idadmin", nullable = false)
    private Utente admin;

    @OneToMany(mappedBy = "progetto", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Issue> issues = new ArrayList<>();

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