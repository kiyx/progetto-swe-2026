package dev.parthenodevs.bugboard.backend.model;

import java.util.*;
import com.fasterxml.jackson.annotation.*;
import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Utente", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Utente
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idutente")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(nullable = false, length = 60)
    @JsonIgnore
    private String password;

    @Column(nullable = false, name = "isadmin")
    private Boolean isAdmin = false;

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assegnazioni", schema = "bugboard26",
            joinColumns = @JoinColumn(name = "idutente"),
            inverseJoinColumns = @JoinColumn(name = "idissue")
    )
    private Set<Issue> assignedIssues = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "partecipanti", schema = "bugboard26",
            joinColumns = @JoinColumn(name = "idutente"),
            inverseJoinColumns = @JoinColumn(name = "idteam")
    )
    private Set<Team> assignedTeams = new HashSet<>();

    @Builder
    public Utente(String email, String nome, String cognome, String password, Boolean isAdmin)
    {
        if(email == null || email.isBlank())
            throw new InvalidFieldException("L'email è un campo obbligatorio");
        if(nome == null || nome.isBlank())
            throw new InvalidFieldException("Il nome è un campo obbligatorio");
        if(cognome == null || cognome.isBlank())
            throw new InvalidFieldException("Il cognome è un campo obbligatorio");
        if(password == null || password.isBlank())
            throw new InvalidFieldException("La password è obbligatoria");

        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.password = password;
        this.isAdmin = isAdmin != null && isAdmin;
    }

    public void addIssue(Issue issue)
    {
        this.assignedIssues.add(issue);
    }

    public void removeIssue(Issue issue)
    {
        this.assignedIssues.remove(issue);
    }

    public void addTeam(Team team)
    {
        this.assignedTeams.add(team);
        team.getMembri().add(this);
    }

    public void removeTeam(Team team)
    {
        this.assignedTeams.remove(team);
        team.getMembri().remove(this);
    }
}