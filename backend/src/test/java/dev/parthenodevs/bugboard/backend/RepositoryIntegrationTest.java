package dev.parthenodevs.bugboard.backend;

import dev.parthenodevs.bugboard.backend.model.*;
import dev.parthenodevs.bugboard.backend.dto.enums.*;
import dev.parthenodevs.bugboard.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RepositoryIntegrationTest {

    @Autowired private UtenteRepository utenteRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProgettoRepository progettoRepository;
    @Autowired private IssueRepository issueRepository;

    @Test
    @DisplayName("1. Test Utente: Creazione, Modifica ed Eliminazione")
    void testUtenteLifecycle() {
        // --- CREATE ---
        Utente user = Utente.builder()
                .nome("Test")
                .cognome("User")
                .email("test.lifecycle@bugboard.it")
                .password("password123")
                .isAdmin(false)
                .build();

        utenteRepository.save(user);
        assertThat(user.getId()).isNotNull();

        // --- UPDATE ---
        user.setIsAdmin(true); // Promuoviamo admin
        utenteRepository.save(user);

        Utente updatedUser = utenteRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getIsAdmin()).isTrue();

        // --- DELETE ---
        utenteRepository.delete(user);
        assertThat(utenteRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("2. Test Team e Membri (Relazione Many-to-Many)")
    void testTeamMembership() {
        // Setup: Creo admin e due utenti
        Utente admin = creaUtente("Admin", "Boss", "admin@bb.it");
        Utente dev1 = creaUtente("Mario", "Rossi", "mario@bb.it");
        Utente dev2 = creaUtente("Luigi", "Verdi", "luigi@bb.it");

        // --- CREATE TEAM ---
        Team team = Team.builder()
                .nome("Sviluppo Backend")
                .admin(admin)
                .build();
        teamRepository.save(team);

        // --- AGGIUNTA MEMBRI (Many-to-Many) ---
        // Nota: Utente è l'owner della relazione 'assignedTeams'
        admin.addTeam(team);
        dev1.addTeam(team);
        dev2.addTeam(team);

        utenteRepository.saveAll(List.of(admin, dev1, dev2));

        // Verifica: Rileggo il team dal DB
        Team teamFetch = teamRepository.findById(team.getId()).orElseThrow();
        assertThat(teamFetch.getMembri()).hasSize(3); // Admin + 2 Devs
        System.out.println("✅ Membri aggiunti correttamente: " + teamFetch.getMembri().size());

        // --- RIMOZIONE MEMBRO ---
        dev2.removeTeam(team);
        utenteRepository.save(dev2);

        Team teamAfterRemove = teamRepository.findById(team.getId()).orElseThrow();
        assertThat(teamAfterRemove.getMembri()).hasSize(2); // Luigi rimosso
        System.out.println("✅ Membro rimosso correttamente.");
    }

    @Test
    @DisplayName("3. Test Progetto: Ciclo di vita e Stati")
    void testProgettoLifecycle() {
        Utente admin = creaUtente("Project", "Manager", "pm@bb.it");
        Team team = creaTeam("DevOps Team", admin);

        // --- CREATE (Stato FUTURO) ---
        Progetto progetto = Progetto.builder()
                .nome("Migrazione Cloud")
                .stato(StatoProgetto.FUTURO) // Enum corretto
                .team(team)
                .admin(admin)
                .build();

        progettoRepository.save(progetto);
        assertThat(progetto.getId()).isNotNull();

        // --- UPDATE (Stato ATTIVO) ---
        progetto.setStato(StatoProgetto.ATTIVO); // Enum corretto
        progettoRepository.save(progetto);

        Progetto pUpdated = progettoRepository.findById(progetto.getId()).orElseThrow();
        assertThat(pUpdated.getStato()).isEqualTo(StatoProgetto.ATTIVO);

        // --- DELETE ---
        progettoRepository.delete(progetto);
        assertThat(progettoRepository.findById(progetto.getId())).isEmpty();
    }

    @Test
    @DisplayName("4. Test Issue: Assegnazione Utenti e Cambio Stato")
    void testIssueWorkflow() {
        Utente dev = creaUtente("Dev", "Eloper", "dev@bb.it");
        Utente tester = creaUtente("QA", "Tester", "qa@bb.it");
        Team team = creaTeam("Product Team", dev);
        Progetto progetto = creaProgetto("App Mobile", StatoProgetto.ATTIVO, team, dev);

        Issue bug = Issue.builder()
                .titolo("Crash all'avvio")
                .descrizione("L'app crasha su Android 14")
                .tipo(TipoIssue.BUG)
                .priorita(TipoPriorita.ALTA)
                .stato(StatoIssue.TODO) // Enum corretto
                .autore(tester) // QA apre il bug
                .progetto(progetto)
                .build();

        issueRepository.save(bug);

        // --- ASSEGNAZIONE (Many-to-Many Utente <-> Issue) ---
        dev.addIssue(bug); // Assegno al developer
        utenteRepository.save(dev);

        // Verifica lato Utente
        Utente devFetch = utenteRepository.findById(dev.getId()).orElseThrow();
        assertThat(devFetch.getAssignedIssues()).contains(bug);

        // Verifica lato Issue (Stato ASSEGNATA)
        bug.setStato(StatoIssue.ASSEGNATA); // Enum corretto
        issueRepository.save(bug);

        Issue bugFetch = issueRepository.findById(bug.getId()).orElseThrow();
        assertThat(bugFetch.getStato()).isEqualTo(StatoIssue.ASSEGNATA);

        // --- RISOLUZIONE ---
        bug.setStato(StatoIssue.RISOLTA);
        issueRepository.save(bug);
        assertThat(issueRepository.findById(bug.getId()).get().getStato())
                .isEqualTo(StatoIssue.RISOLTA);

        System.out.println("✅ Workflow Issue completato: TODO -> ASSEGNATA -> RISOLTA");
    }

    // --- Metodi Helper per snellire i test ---

    private Utente creaUtente(String nome, String cognome, String email) {
        return utenteRepository.save(Utente.builder()
                .nome(nome).cognome(cognome).email(email)
                .password("pass").isAdmin(true).build());
    }

    private Team creaTeam(String nome, Utente admin) {
        return teamRepository.save(Team.builder().nome(nome).admin(admin).build());
    }

    private Progetto creaProgetto(String nome, StatoProgetto stato, Team team, Utente admin) {
        return progettoRepository.save(Progetto.builder()
                .nome(nome).stato(stato).team(team).admin(admin).build());
    }
}