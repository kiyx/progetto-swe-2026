package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Issue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@SuppressWarnings("NullableProblems")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>
{
    List<Issue> findByAutore_Id(Long idAutore);
}