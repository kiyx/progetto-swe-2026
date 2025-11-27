package dev.parthenodevs.bugboard.backend.model;

import dev.parthenodevs.bugboard.backend.exception.InvalidFieldException;
import dev.parthenodevs.bugboard.backend.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Issue", schema = "bugboard26")
@Data
@NoArgsConstructor
public class Issue
{
    @Id
    @Column(name = "idissue")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String titolo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoIssue tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoIssue stato = StatoIssue.TODO;

    @Column(name="isarchiviato", nullable = false)
    private boolean isArchiviato;

    private String immagine;

    @Enumerated(EnumType.STRING)
    private TipoPriorita priorita;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "idutente", nullable = false)
    private Utente autore;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idprogetto", nullable = false)
    private Progetto progetto;

    @Builder
    public Issue(String titolo, String descrizione, TipoIssue tipo, StatoIssue stato, Utente autore, Progetto progetto, TipoPriorita priorita, String immagine)
    {
        if(titolo == null || titolo.isBlank())
            throw new InvalidFieldException("Titolo obbligatorio");
        if(descrizione == null || descrizione.isBlank())
            throw new InvalidFieldException("Descrizione obbligatoria");
        if(tipo == null)
            throw new InvalidFieldException("Tipo obbligatorio");
        if(autore == null)
            throw new InvalidFieldException("Autore obbligatorio");
        if(progetto == null)
            throw new InvalidFieldException("Progetto obbligatorio");

        this.titolo = titolo;
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.stato = (stato != null) ? stato : StatoIssue.TODO;
        this.autore = autore;
        this.progetto = progetto;
        this.isArchiviato = false;
        this.priorita = priorita;
        this.immagine = immagine;
    }
}