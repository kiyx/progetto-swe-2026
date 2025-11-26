package dev.parthenodevs.bugboard.backend.model;
import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Issue
{
    @Column(name = "IdIssue", nullable = false, unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIssue;

    @Column(name = "Titolo", length = 200, nullable = false)
    private String titolo;

    @Column(name = "Descrizione", nullable = false)
    private String descrizione;

    @Column(name = "Tipo", nullable = false )
    @Enumerated(EnumType.ORDINAL)
    private TipoIssue tipo;

    @Column(name = "Stato", nullable = false )
    @Enumerated(EnumType.ORDINAL)
    private StatoIssue stato = StatoIssue.TODO;

    @Column(name = "IsArchiviato" )
    private boolean isArchiviato = false;

    @Column(name = "Priorita" )
    @Enumerated(EnumType.ORDINAL)
    private TipoPriorita priorita;

    @Column(name = "Immagine" )
    private String immagine;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "IdUtente" )
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "IdProgetto" )
    private Progetto progetto;
}
