package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.dto.enums.StatoProgetto;
import dev.parthenodevs.bugboard.backend.model.Progetto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface ProgettoRepository extends JpaRepository<Progetto, Long>
{
    List<Progetto> findAllByAdminIdOrderByIdDesc(Long adminId);
    boolean existsByTeamIdAndStato(Long teamId, StatoProgetto stato);
}