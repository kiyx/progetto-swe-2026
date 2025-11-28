package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Issue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoIssue;
import dev.parthenodevs.bugboard.backend.model.enums.TipoPriorita;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface IssueRepository extends JpaRepository<Issue, Long>
{
    Optional<Issue> findById(Long id);
    List<Issue> findByProgettoId(Long progettoId);
    @Query("SELECT i FROM Issue i WHERE i.idprogetto = :idprogetto AND i.tipo = 'Bug'")
    List<Issue> findByProgettoAndTipoIsBugAndArchiviatoIsFalse(@Param("progettoId") Long progettoId);
}