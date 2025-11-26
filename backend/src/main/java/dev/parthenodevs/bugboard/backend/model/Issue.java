package dev.parthenodevs.bugboard.backend.model;
import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Issue", schema = "bugboard26")
public class Issue
{
    @Column(name = "idissue", nullable = false, unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIssue;

    @Column(length = 200, nullable = false)
    private String titolo;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false )
    @Enumerated(EnumType.ORDINAL)
    private TipoIssue tipo;

    @Column(nullable = false )
    @Enumerated(EnumType.ORDINAL)
    private StatoIssue stato = StatoIssue.TODO;

    @Column(nullable = false)
    private boolean isArchiviato = false;

    @Column(name = "Priorita" )
    @Enumerated(EnumType.ORDINAL)
    private TipoPriorita priorita;

    @Column(name = "Immagine" )
    private String immagine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "idutente" )
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idprogetto" )
    private Progetto progetto;

    @Builder
    public Issue(String titolo, String descrizione, TipoIssue tipo, StatoIssue stato, boolean isArchiviato, TipoPriorita priorita, String immagine)
    {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.stato = stato;
        this.isArchiviato = isArchiviato;
        this.priorita = priorita;
        this.immagine = immagine;


    }
}