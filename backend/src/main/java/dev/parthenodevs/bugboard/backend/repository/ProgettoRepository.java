package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Progetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface ProgettoRepository extends JpaRepository<Progetto, Long>
{
    List<Progetto> findByTeamId(Long id);
    List<Progetto> findByAdminId(Long id);
}