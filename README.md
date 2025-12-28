# 🐞 BugBoard26

> **Progetto di Ingegneria del Software - A.A. 2025/2026**
>
> *Università degli Studi di Napoli Federico II*

**BugBoard26** è una piattaforma enterprise distribuita per il *bug tracking* e la gestione collaborativa di progetti software. Il sistema offre un'architettura robusta per la segnalazione di anomalie, la gestione dei team di sviluppo e il monitoraggio del ciclo di vita del software.

---

## 👥 Team di Sviluppo

| Matricola | Studente |
| :--- | :--- |
| **N86005174** | **Giuseppe Paolo Esposito** |
| **N86004987** | **Virginia Antonia Esposito** |

---

## 🚀 Funzionalità Implementate

Il progetto copre le seguenti funzionalità specifiche:

### 1. Autenticazione e Gestione Utenti
* **Login Sicuro:** Autenticazione basata su **JWT (JSON Web Token)** stateless.
* **Gestione Ruoli:** Distinzione dei permessi tra **Amministratore** e **Utente Standard**.
* **Registrazione:** Creazione nuovi account con validazione dei dati e hashing delle password.

### 2. Creazione e Gestione Issue
* **Reporting:** Segnalazione di issue con campi strutturati (Titolo, Descrizione, Priorità).
* **Supporto Multimediale:** Caricamento e visualizzazione di immagini allegate (codifica Base64).
* **Tipologie:** Classificazione in `Bug`, `Feature`, `Documentation`, `Question`.
* **Stati:** Gestione del ciclo di vita (`Todo` -> `Assigned` -> `In Progress` -> `Resolved`).

### 3. Dashboard e Visualizzazione
* **Visualizzazione Tabellare:** Elenco interattivo delle issue con anteprima delle immagini.
* **Filtraggio Avanzato:** Filtri dinamici per *Stato*, *Priorità* e *Tipologia*, con ricerca testuale.
* **Viste Contestuali:** Separazione tra "Le mie Issue" (create/assegnate) e la visione globale (per Admin).

### 13. Archiviazione (Admin)
* **Storico:** Funzionalità riservata agli amministratori per archiviare i bug risolti.
* **Audit:** Le issue archiviate vengono rimosse dalla dashboard operativa principale ma rimangono consultabili in una sezione dedicata ("Archivio").

---

## 🏗️ Architettura del Sistema

L'applicazione segue un'architettura **Client-Server Distribuita**.

### 🔙 Backend (Spring Boot)
Il core del sistema adotta una **Layered Architecture** rigorosa per garantire la separazione delle responsabilità:
* **Controller Layer:** Espone le API REST e gestisce la validazione delle richieste (DTO).
* **Service Layer:** Implementa la logica di business (es. regole di assegnazione, controlli sui team).
* **Repository Layer (JPA):** Gestisce l'accesso ai dati su PostgreSQL utilizzando **Spring Data JPA**.

### 🖥️ Frontend (Java Swing)
Un *Rich Client* desktop moderno e reattivo.
* **Pattern MVC:**
    * **Model:** DTO e Service per la gestione dei dati e la comunicazione HTTP.
    * **View:** Interfacce grafiche realizzate con **Swing**, **FlatLaf** (per un look moderno) e **MigLayout**. Le view sono componenti passivi.
    * **Controller:** Gestiscono la logica di presentazione, coordinano i flussi e le chiamate asincrone ai servizi.
* **Async UI:** Utilizzo di Thread separati per le operazioni di rete per mantenere l'interfaccia sempre responsiva.

---

## 🛠️ Stack Tecnologico

### Backend
* **Java 17+**
* **Spring Boot 3** (Web, Security, Data JPA)
* **PostgreSQL** (Database Relazionale)
* **Hibernate** (ORM)
* **Lombok** (Boilerplate reduction)

### Frontend
* **Java Swing** (GUI Toolkit)
* **FlatLaf** (Look and Feel)
* **MigLayout** (Layout Manager)
* **SwingX** (Componenti estesi)
* **Jackson** (JSON Processing)

### DevOps & Tools
* **Maven** (Dependency Management)
* **Docker** (Containerizzazione - *In Roadmap*)

---

## ⚙️ Installazione e Avvio

### 1. Configurazione Database
Assicurarsi di avere PostgreSQL in esecuzione e importare gli script SQL forniti nella cartella `docs/database`.
Configurare le credenziali nel file `backend/src/main/resources/application.properties`.

### 2. Avvio Backend
```bash
cd backend
mvn spring-boot:run
