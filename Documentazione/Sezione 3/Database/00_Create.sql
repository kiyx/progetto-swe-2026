-- Setto lo schema come primo nella ricerca
SET search_path TO BugBoard26, public;

-- Creo lo schema per BugBoard26
CREATE SCHEMA BugBoard26;

-- 00_Create: Definizione Enum e Tabelle

CREATE TYPE Tipo_Issue AS ENUM ('QUESTION', 'BUG', 'DOCUMENTATION', 'FEATURE');
CREATE TYPE Tipo_Priorita AS ENUM ('ALTA', 'MEDIA', 'BASSA');
CREATE TYPE Stato_Issue AS ENUM('TODO', 'ASSEGNATA', 'DA_ACCETTARE', 'RISOLTA');
CREATE TYPE Stato_Progetto AS ENUM('ATTIVO', 'FUTURO', 'CONCLUSO');

-- Utente
CREATE TABLE Utente
(
    IdUtente BIGSERIAL PRIMARY KEY,
    Email TEXT UNIQUE NOT NULL, 
    Nome VARCHAR(100) NOT NULL,
    Cognome VARCHAR(100) NOT NULL,
    Password VARCHAR(60) NOT NULL,
    isAdmin BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_empty_utente_nome CHECK (LENGTH(Nome) > 0),
    CONSTRAINT check_empty_utente_cognome CHECK (LENGTH(Cognome) > 0),
    CONSTRAINT check_empty_utente_email CHECK (LENGTH(Email) > 0),
    CONSTRAINT check_empty_utente_pass CHECK (LENGTH(Password) > 0)
);

CREATE TABLE Team
(
    IdTeam BIGSERIAL PRIMARY KEY,
    Nome VARCHAR(100) UNIQUE NOT NULL,
    IdAdmin BIGINT NOT NULL,
    CONSTRAINT fk_team_admin FOREIGN KEY (IdAdmin) REFERENCES Utente(IdUtente) ON DELETE RESTRICT,
    CONSTRAINT check_empty_team_nome CHECK (LENGTH(Nome) > 0)
);

CREATE TABLE Progetto
(
    IdProgetto BIGSERIAL PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Stato Stato_Progetto NOT NULL,
    IdTeam BIGINT NOT NULL,
    IdAdmin BIGINT NOT NULL,
    CONSTRAINT fk_progetto_team FOREIGN KEY (IdTeam) REFERENCES Team(IdTeam) ON DELETE CASCADE,
    CONSTRAINT fk_progetto_admin FOREIGN KEY (IdAdmin) REFERENCES Utente(IdUtente) ON DELETE RESTRICT,
    CONSTRAINT check_empty_progetto_nome CHECK (LENGTH(Nome) > 0)
);

CREATE TABLE Issue 
(
    IdIssue BIGSERIAL PRIMARY KEY,
    Titolo VARCHAR(200) NOT NULL,
    Descrizione TEXT NOT NULL,
    Tipo Tipo_Issue NOT NULL,
    Stato Stato_Issue NOT NULL DEFAULT 'TODO',
    IsArchiviato BOOLEAN DEFAULT FALSE,
    Immagine TEXT,
    Priorita Tipo_Priorita,
    IdUtente BIGINT NOT NULL,
    IdProgetto BIGINT NOT NULL,
    CONSTRAINT fk_issue_utente FOREIGN KEY (IdUtente) REFERENCES Utente(IdUtente) ON DELETE SET NULL,
    CONSTRAINT fk_issue_progetto FOREIGN KEY (IdProgetto) REFERENCES Progetto(IdProgetto) ON DELETE CASCADE,
    CONSTRAINT check_empty_issue_titolo CHECK (LENGTH(Titolo) > 0),
    CONSTRAINT check_empty_issue_descrizione CHECK (LENGTH(Descrizione) > 0)
);

CREATE TABLE Assegnazioni
(
    IdUtente BIGINT NOT NULL,
    IdIssue BIGINT NOT NULL,
    PRIMARY KEY (IdUtente, IdIssue),
    CONSTRAINT fk_assegnazioni_utente FOREIGN KEY (IdUtente) REFERENCES Utente(IdUtente) ON DELETE CASCADE,
    CONSTRAINT fk_assegnazioni_issue FOREIGN KEY (IdIssue) REFERENCES Issue(IdIssue) ON DELETE CASCADE
);

CREATE TABLE Partecipanti
(
    IdUtente BIGINT NOT NULL,
    IdTeam BIGINT NOT NULL,
    PRIMARY KEY (IdUtente, IdTeam),
    CONSTRAINT fk_partecipanti_utente FOREIGN KEY (IdUtente) REFERENCES Utente(IdUtente) ON DELETE CASCADE,
    CONSTRAINT fk_partecipanti_team FOREIGN KEY (IdTeam) REFERENCES Team(IdTeam) ON DELETE CASCADE
);