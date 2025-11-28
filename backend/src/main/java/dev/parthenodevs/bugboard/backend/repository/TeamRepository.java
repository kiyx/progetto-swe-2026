package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Progetto;
import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamRepository extends JpaRepository<Team, Long>
{
    Optional<Team> findById(Long id);
    List<Team> findByAdmin(Utente admin);
    List<Team> findByMembriWithin(Set<Utente> membri);
    List<Team> findByMembriContains(Utente utente);
    List<Team> findByProgettiContains(Progetto progetto);
}