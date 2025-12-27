package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface ProgettoRepository extends JpaRepository<Progetto, Long>
{
    @Query("SELECT DISTINCT p FROM Progetto p " +
            "JOIN p.team t " +
            "LEFT JOIN t.membri m " +
            "WHERE t.admin.id = :userId " +
            "OR m.id = :userId")
    List<Progetto> findProgettiAccessibiliOrderByIdDesc(@Param("userId") Long userId);
    boolean existsByTeamIdAndStato(Long teamId, StatoProgetto stato);
}