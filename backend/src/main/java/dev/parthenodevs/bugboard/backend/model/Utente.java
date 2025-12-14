package dev.parthenodevs.bugboard.backend.model;

import java.io.*;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import jakarta.persistence.*;
import lombok.*;
import lombok.NonNull;
import org.jspecify.annotations.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;

@Entity
@Table(name = "Utente", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Utente implements UserDetails
{
    @Serial
    private static final long serialVersionUID = 1L;

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
    @SuppressWarnings("java:S1948")
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
    @SuppressWarnings("java:S1948")
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

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if(Boolean.TRUE.equals(this.isAdmin))
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @NonNull
    public String getUsername()
    {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}