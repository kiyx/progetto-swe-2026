package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Team;
import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface TeamRepository extends JpaRepository<Team, Long>
{
    List<Team> findByAdmin(Utente admin);
}