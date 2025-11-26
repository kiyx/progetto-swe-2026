package dev.parthenodevs.bugboard.backend.model;
import dev.parthenodevs.bugboard.backend.model.enums.StatoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
public class Issue
{
    @Column(name = "IdIssue", nullable = false, unique = true)
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIssue;

    @Column(name = "Titolo", length = 200, nullable = false)
    @Getter
    @Setter
    private String titolo;

    @Column(name = "Descrizione", nullable = false)
    @Getter
    @Setter
    private String descrizione;

    @Column(name = "Tipo", nullable = false )
    @Getter
    @Setter
    @Enumerated(EnumType.ORDINAL)
    private TipoIssue tipo;

    @Column(name = "Stato", nullable = false )
    @Getter
    @Setter
    @Enumerated(EnumType.ORDINAL)
    private StatoIssue stato = StatoIssue.TODO;

    @Column(name = "IsArchiviato" )
    @Getter
    @Setter
    private boolean isArchiviato = false;

    @Column(name = "Priorita" )
    @Getter
    @Setter
    @Enumerated(EnumType.ORDINAL)
    private TipoPriorita priorita;

    @Column(name = "Immagine" )
    @Getter
    @Setter
    private String immagine;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "IdUtente" )
    @Getter
    @Setter
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "IdProgetto" )
    @Getter
    @Setter
    private Progetto progetto;
}
