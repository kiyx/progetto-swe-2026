---------------------- Vincoli derivanti dalla ristrutturazione ---------------------- 

-- Se isAdmin == false allora Utente non può formare un team
CREATE OR REPLACE FUNCTION check_team_creation_permission()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Utente WHERE IdUtente = NEW.IdAdmin AND isAdmin = TRUE) THEN
        RAISE EXCEPTION 'Operazione non consentita: solo gli utenti con privilegi di amministratore possono creare un team.';
    END IF;
    RETURN NEW;
    
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_team_creation_permission
BEFORE INSERT OR UPDATE ON Team
FOR EACH ROW
EXECUTE FUNCTION check_team_creation_permission();

-- Se isAdmin == false allora Utente non può assegnare un progetto a un team
CREATE OR REPLACE FUNCTION check_project_assignment_permission()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Utente WHERE IdUtente = NEW.IdAdmin AND isAdmin = TRUE) THEN
        RAISE EXCEPTION 'Operazione non consentita: solo gli utenti con privilegi di amministratore possono assegnare un progetto a un team.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_project_assignment_permission
BEFORE INSERT OR UPDATE ON Progetto
FOR EACH ROW
EXECUTE FUNCTION check_project_assignment_permission();

---------------------- Fine vincoli derivanti dalla ristrutturazione ---------------------- 

---------------------- Vincoli di coerenza aggiuntivi ---------------------- 

-- Una issue può essere assegnata solo a utenti che fanno parte del team associato al progetto della issue
CREATE OR REPLACE FUNCTION check_assignee_is_in_team()
RETURNS TRIGGER AS $$
DECLARE
    team_id INT;
BEGIN
    SELECT P.IdTeam INTO team_id
    FROM Issue I
    JOIN Progetto P ON P.IdProgetto = I.IdProgetto
    WHERE I.IdIssue = NEW.IdIssue;

    IF NOT EXISTS (
        SELECT 1
        FROM Partecipanti P
        WHERE P.IdUtente = NEW.IdUtente AND P.IdTeam = team_id
    ) THEN
        RAISE EXCEPTION 'Operazione non consentita: l''utente assegnato non fa parte del team associato al progetto della issue.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_assignee_is_in_team
BEFORE INSERT ON Assegnazioni
FOR EACH ROW
EXECUTE FUNCTION check_assignee_is_in_team();

-- Non è possibile aprire issues su progetti conclusi
CREATE OR REPLACE FUNCTION check_project_status_before_issue()
RETURNS TRIGGER AS $$
DECLARE
    project_status Stato_Progetto;
BEGIN
    SELECT Stato INTO project_status
    FROM Progetto
    WHERE IdProgetto = NEW.IdProgetto;

    IF project_status = 'Concluso' THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile aprire issues su progetti conclusi.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_project_status_before_issue
BEFORE INSERT ON Issue
FOR EACH ROW
EXECUTE FUNCTION check_project_status_before_issue();

-- Non è possibile segnare la issue come Risolta se non è stata assegnata a nessun utente
CREATE OR REPLACE FUNCTION check_resolved_issue_has_assignee()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.Stato = 'Risolta' THEN
        IF NOT EXISTS (
            SELECT 1
            FROM Assegnazioni
            WHERE IdIssue = NEW.IdIssue
        ) THEN
            RAISE EXCEPTION 'Operazione non consentita: una issue non può essere marcata come Risolta se non è stata assegnata a nessun utente.';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_resolved_issue_has_assignee
BEFORE INSERT OR UPDATE ON Issue
FOR EACH ROW
EXECUTE FUNCTION check_resolved_issue_has_assignee();

-- Non è possibile modificare un bug archiviato
CREATE OR REPLACE FUNCTION prevent_modification_of_archived_issues()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.IsArchiviato = TRUE THEN
        IF NEW.IsArchiviato = FALSE THEN
            RETURN NEW;
        END IF;
        RAISE EXCEPTION 'Operazione non consentita: non è possibile modificare una issue archiviata.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_modification_of_archived_issues
BEFORE UPDATE ON Issue
FOR EACH ROW
WHERE OLD.Tipo = 'Bug'
EXECUTE FUNCTION prevent_modification_of_archived_issues();

-- Non è possibile archiviare una issue che non è di tipo bug
CREATE OR REPLACE FUNCTION prevent_archiving_non_bug_issues()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.IsArchiviato = TRUE AND OLD.Tipo <> 'Bug' THEN
        RAISE EXCEPTION 'Operazione non consentita: solo le issue di tipo Bug possono essere archiviate.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_archiving_non_bug_issues
BEFORE UPDATE ON Issue
FOR EACH ROW
EXECUTE FUNCTION prevent_archiving_non_bug_issues();

-- Non è possibile modificare i campi Tipo, Priorità, IdProgetto, IdUtente di una issue
CREATE OR REPLACE FUNCTION prevent_issue_field_modification()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.Tipo IS DISTINCT FROM NEW.Tipo THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile modificare il campo Tipo di una issue.';
    ELSIF OLD.Priorita IS DISTINCT FROM NEW.Priorita THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile modificare il campo Priorità di una issue.';
    ELSIF OLD.IdProgetto IS DISTINCT FROM NEW.IdProgetto THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile modificare il campo IdProgetto di una issue.';
    ELSIF OLD.IdUtente IS DISTINCT FROM NEW.IdUtente THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile modificare il campo IdUtente di una issue.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_issue_field_modification
BEFORE UPDATE ON Issue
FOR EACH ROW
EXECUTE FUNCTION prevent_issue_field_modification();

-- Non è possibile spostare un progetto da un team a un altro
CREATE OR REPLACE FUNCTION prevent_project_team_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.IdTeam <> NEW.IdTeam THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile spostare un progetto da un team a un altro.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_project_team_change
BEFORE UPDATE ON Progetto
FOR EACH ROW
EXECUTE FUNCTION prevent_project_team_change();

-- Non è possibile demotare un admin 
CREATE OR REPLACE FUNCTION prevent_admin_demotion()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.isAdmin = TRUE AND NEW.isAdmin = FALSE THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile demotare un amministratore.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_admin_demotion
BEFORE UPDATE ON Utente
FOR EACH ROW
EXECUTE FUNCTION prevent_admin_demotion();

-- Non è possibile rimuovere un utente con issue attive da un team
CREATE OR REPLACE FUNCTION prevent_removal_of_user_with_active_issues()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
                SELECT 1 FROM Assegnazioni A
                JOIN Issue I ON A.IdIssue = I.IdIssue
                JOIN Progetto P ON I.IdProgetto = P.IdProgetto
                WHERE A.IdUtente = OLD.IdUtente
                  AND P.IdTeam = OLD.IdTeam
                  AND I.Stato <> 'Risolta'
    )
    THEN
        RAISE EXCEPTION 'Operazione non consentita: non è possibile rimuovere un utente con issue attive dal team.';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_removal_of_user_with_active_issues
BEFORE DELETE ON Partecipanti
FOR EACH ROW
EXECUTE FUNCTION prevent_removal_of_user_with_active_issues();

---------------------- Fine vincoli di coerenza aggiuntivi ---------------------- 