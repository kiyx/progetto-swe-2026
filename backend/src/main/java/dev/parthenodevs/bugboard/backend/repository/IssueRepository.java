package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.dto.enums.*;
import dev.parthenodevs.bugboard.backend.model.Issue;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>
{
    List<Issue> findByAutore_Id(Long idAutore);

    @Query("SELECT i FROM Issue i " +
            "JOIN FETCH i.progetto p " +
            "JOIN FETCH p.team t " +
            "WHERE t.admin.id = :adminId " +
            "ORDER BY i.id DESC")
    List<Issue> findIssuesByAdminTeams(@Param("adminId") Long adminId);

    @Query("SELECT i FROM Issue i WHERE " +
            "(:titolo IS NULL OR LOWER(i.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "(i.tipo = COALESCE(:tipo, i.tipo)) AND " +
            "(i.stato = COALESCE(:stato, i.stato)) AND " +
            "(i.priorita = COALESCE(:priorita, i.priorita)) AND " +
            "(i.isArchiviato = COALESCE(:isArchiviato, i.isArchiviato))")
    List<Issue> filtraIssues(
            @Param("titolo") String titolo,
            @Param("tipo") TipoIssue tipo,
            @Param("stato") StatoIssue stato,
            @Param("priorita") TipoPriorita priorita,
            @Param("isArchiviato") Boolean isArchiviato
    );
}