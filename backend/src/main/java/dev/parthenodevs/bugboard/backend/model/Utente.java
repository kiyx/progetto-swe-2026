package dev.parthenodevs.bugboard.backend.model;

import jakarta.persistence.*;

@Entity
public class Utente
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgetto;
}
