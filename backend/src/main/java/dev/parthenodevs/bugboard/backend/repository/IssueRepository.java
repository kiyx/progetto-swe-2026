package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>
{
    List<Issue> findByProgettoId(Long progettoId);
    @Query("SELECT i FROM Issue i WHERE i.progetto.id = :progettoId AND i.tipo = 'BUG' AND i.isArchiviato = false")
    List<Issue> findByProgettoAndTipoIsBugAndArchiviatoIsFalse(@Param("progettoId") Long progettoId);
    List<Issue> findByAutore_Id(Long idAutore);
}