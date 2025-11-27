package dev.parthenodevs.bugboard.backend.repository;

import dev.parthenodevs.bugboard.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UtenteRepository extends JpaRepository<Utente, Long>
{
    List<Utente> findByEmail(String email);

}