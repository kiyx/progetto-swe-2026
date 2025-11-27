package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Progetto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProgettoRepository extends JpaRepository<Progetto, Long>
{
}