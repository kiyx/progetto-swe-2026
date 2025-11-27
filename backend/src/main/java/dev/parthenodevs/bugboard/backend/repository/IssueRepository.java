package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface IssueRepository extends JpaRepository<Issue, Long>
{

}