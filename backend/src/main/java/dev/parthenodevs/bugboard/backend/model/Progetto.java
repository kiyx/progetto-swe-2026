package dev.parthenodevs.bugboard.backend.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idteam", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idadmin", nullable = false)
    private Utente admin;
}