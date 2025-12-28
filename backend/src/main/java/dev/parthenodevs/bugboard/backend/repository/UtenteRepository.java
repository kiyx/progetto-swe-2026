package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long>
{
    Optional<Utente> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM Utente u WHERE u NOT IN (SELECT m FROM Team t JOIN t.membri m WHERE t.id = :teamId)")
    List<Utente> findUsersNotInTeam(@Param("teamId") Long teamId);
    List<Utente> findByAssignedTeams_Id(Long teamId);
    List<Utente> findByAssignedIssuesContaining(Issue issue);
}