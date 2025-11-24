# 🐞 BugBoard26

> **Progetto di Ingegneria del Software - A.A. 2025/2026**
>
> *Università degli Studi di Napoli Federico II*

**BugBoard26** è una piattaforma distribuita per la gestione collaborativa di *issue* e *bug tracking* in progetti software. Il sistema permette ai team di segnalare problemi, monitorarne lo stato e tracciare le attività di risoluzione in modo intuitivo e centralizzato.

---

## 👥 Team di Sviluppo

| Matricola | Studente | GitHub |
| :--- | :--- | :--- |
| **N86005174** | **Giuseppe Paolo Esposito** | [@kiyx](https://github.com/kiyx) |
| **N86004987** | **Virginia Antonia Esposito** | [@virginiaesposito](https://github.com/virginiaesposito) |

---

## 🚀 Funzionalità Implementate

Il progetto implementa un sottoinsieme specifico di requisiti funzionali, assegnati sulla base della matricola del gruppo:

### 1. Autenticazione e Gestione Utenti
* Sistema di login sicuro basato su email e password.
* Distinzione dei ruoli tra **Amministratore** e **Utente Standard**.
* Gestione della sicurezza e integrità delle credenziali.

### 2. Creazione e Gestione Issue
* Segnalazione di issue con **Titolo** e **Descrizione** obbligatori.
* Classificazione per tipologia: `Question`, `Bug`, `Documentation`, `Feature`.
* Supporto per livelli di priorità e allegati (immagini).
* Stato iniziale automatico impostato su `Todo`.

### 3. Dashboard e Visualizzazione
* Vista riepilogativa di tutte le issue presenti nel sistema.
* Funzionalità di **filtro** e **ordinamento** avanzato (per stato, tipologia, priorità, ecc.).

### 13. Archiviazione (Admin)
* Funzionalità dedicata agli amministratori per **archiviare** i bug risolti o non più rilevanti.
* I bug archiviati vengono rimossi dalle viste principali ma rimangono consultabili in una sezione storica dedicata.

---

## 🏗️ Architettura del Sistema

L'applicazione è progettata come un **sistema distribuito Client-Server**, garantendo il totale disaccoppiamento tra la logica di presentazione e la logica di business.

### 🔙 Backend (Core Logic)
Il cuore del sistema, responsabile della logica di business e della persistenza. Segue una **Layered Architecture** implementando il modello **Boundary-Control-Entity (BCE)**:
* **Boundary Layer (API REST):** Espone endpoint HTTP tramite Controller. Gestisce la validazione dei DTO in ingresso e la serializzazione delle risposte JSON.
* **Control Layer (Service):** Incapsula la logica applicativa, coordinando le operazioni tra i controller e il livello dati.
* **Entity Layer (Persistence):** Mappa le entità di dominio sul database relazionale tramite ORM.

### 🖥️ Frontend (Presentation)
Applicazione Desktop indipendente che comunica con il server esclusivamente tramite API REST. Adotta il pattern **Model-View-ViewModel (MVVM)**:
* **View (FXML):** Definisce la struttura dell'interfaccia utente, priva di logica.
* **ViewModel:** Agisce da mediatore, esponendo dati e comandi alla View tramite *Data Binding*.
* **Service Gateway:** Gestisce la comunicazione di rete, isolando la UI dai dettagli delle chiamate HTTP.

---

## 🛠️ Stack Tecnologico

Le scelte tecnologiche sono state guidate dai principi di modularità, testabilità e astrazione richiesti dal progetto.

### Backend
* **Language:** Java 17+
* **Framework:** **Spring Boot** (Web, Data JPA, Security). Scelto per il supporto nativo alla *Dependency Injection* e la facilità di creazione di API RESTful testabili.
* **Testing:** JUnit 5 & Mockito.

### Frontend
* **Framework:** **JavaFX** 17+. Scelto per la creazione di interfacce desktop moderne e reattive.
* **Librerie:** Gson/Jackson (JSON parsing), HttpClient (Networking).

### Data & Persistence
* **Database:** **PostgreSQL**.
* **Hosting:** **Supabase** (utilizzato esclusivamente come provider *Database-as-a-Service*).
* **Accesso Dati:** Standard **JDBC** tramite **Spring Data JPA**.
> *Nota: Non viene utilizzato alcun servizio MBaaS proprietario per la logica, garantendo l'indipendenza dal provider e la conformità ai requisiti di progetto.*

### DevOps & Tools
* **Container:** **Docker**. Il backend è containerizzato per garantire un ambiente di esecuzione isolato e replicabile.
* **VCS:** Git & GitHub (con GitHub Desktop).
* **Build Tool:** Maven.

---

## 📂 Struttura del Repository

```text
BugBoard26/
├── backend/          # Spring Boot Application
│   ├── src/main/java/com/unina/bugboard
│   │   ├── api       # Boundary (Controllers)
│   │   ├── service   # Control (Business Logic)
│   │   └── model     # Entity (Domain & Repo)
│   └── Dockerfile
│
├── frontend/         # JavaFX Application
│   ├── src/main/resources/fxml  # Views
│   └── src/main/java/com/unina/client
│       ├── viewmodel
│       └── service   # API Consumers
│
└── docs/             # Documentazione di progetto
