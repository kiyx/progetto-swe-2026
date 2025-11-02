#  BugBoard26 - Progetto di Ingegneria del Software 2025/26

Questo repository contiene il progetto per il corso di Ingegneria del Software, A.A. 2025/2026. L'obiettivo è realizzare un sistema distribuito per la gestione di issue (BugBoard), seguendo i pattern di progettazione e le best practice di ingegneria.

**Committente:** Università degli Studi di Napoli Federico II

---

## 👥 Membri del Team

* **Giuseppe Paolo Esposito** - ([Kiyo](https://github.com/kiyx))
* **Virginia Antonia Esposito** - ([Virginia](https://github.com/virginiaesposito))

---

## 🏗️ Architettura di Sistema

L'applicazione segue un'architettura a **sistema distribuito** (Client-Server), come richiesto dalle specifiche, composta da 3 macro-componenti indipendenti.



1.  **Frontend (Client) 💻:** Un'applicazione desktop **JavaFX** indipendente. Gestisce solo la logica di presentazione (pattern **MVC**) e comunica con il backend esclusivamente tramite API REST.
2.  **Backend (Server) ⚙️:** Un'applicazione **Spring Boot** (Java) che espone API REST. Contiene tutta la logica di business (pattern **BCE**) e non ha conoscenza del frontend.
3.  **Database (Persistence) 🗄️:** Un database **PostgreSQL** gestito. È accessibile *solo* dal Backend.

---

## 🛠️ Stack Tecnologico

### Backend

### Frontend

### Database & Deploy

### Sviluppo & Gestione
* **Git & GitHub** (per il versioning)
* **GitHub Desktop** (per la gestione visuale dei branch)
* **Maven** (per la gestione delle dipendenze)
* **IntelliJ IDEA** (come IDE)
* **Scene Builder** (per la progettazione FXML)
