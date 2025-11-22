<head>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">

  <style>
    :root {
      --bg-color: #f8fafc;
      --card-bg: #ffffff;
      --text-primary: #1e293b;
      --text-secondary: #64748b;
      --border-color: #e2e8f0;
      --accent-pk-bg: #fff7ed;
      --accent-pk-text: #c2410c;
      --accent-pk-border: #ffedd5;
      --accent-fk-bg: #eff6ff;
      --accent-fk-text: #2563eb;
      --accent-fk-border: #dbeafe;
      --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
      --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
    }

    body {
      font-family: 'Inter', system-ui, -apple-system, sans-serif;
      background-color: var(--bg-color);
      color: var(--text-primary);
      line-height: 1.6;
      padding: 2rem;
      max-width: 900px;
      margin: 0 auto;
    }

    /* Titoli delle Entità */
    h3 {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-top: 2.5rem;
      margin-bottom: 0.75rem;
      display: flex;
      align-items: center;
    }
    
    h3::before {
      content: '';
      display: inline-block;
      width: 6px;
      height: 24px;
      background-color: var(--text-primary);
      margin-right: 12px;
      border-radius: 4px;
    }

    /* Blocco Definizione Schema (la riga principale) */
    .entity-definition {
      font-family: 'JetBrains Mono', 'Menlo', monospace;
      background-color: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 1rem 1.25rem;
      box-shadow: var(--shadow-sm);
      font-size: 0.9rem;
      color: var(--text-secondary);
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 6px;
    }

    /* Blocco Vincoli (Chiavi Esterne) */
    .constraints {
      margin-top: 8px;
      margin-left: 4px;
      padding-left: 16px;
      border-left: 2px solid #cbd5e1;
      font-size: 0.9rem;
      color: var(--text-secondary);
    }

    .constraints ul {
      list-style: none;
      padding: 0;
      margin: 0.5rem 0 0 0;
    }

    .constraints li {
      margin-bottom: 6px;
      font-family: 'JetBrains Mono', monospace;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    /* Stilizzazione Chiavi Primarie (PK) - Badge Ambra */
    pk {
      text-decoration: none;
      background-color: var(--accent-pk-bg);
      color: var(--accent-pk-text);
      border: 1px solid var(--accent-pk-border);
      padding: 2px 8px;
      border-radius: 6px;
      font-weight: 600;
      font-size: 0.85em;
      display: inline-flex;
      align-items: center;
    }
    
    /* Aggiunge icona chiave opzionale via CSS */
    pk::before {
      content: "PK";
      font-size: 0.7em;
      margin-right: 6px;
      opacity: 0.7;
      text-transform: uppercase;
    }

    /* Stilizzazione Chiavi Esterne (FK) - Badge Blu */
    fk {
      text-decoration: none;
      background-color: var(--accent-fk-bg);
      color: var(--accent-fk-text);
      border: 1px solid var(--accent-fk-border);
      padding: 2px 8px;
      border-radius: 6px;
      font-weight: 500;
      font-size: 0.85em;
      display: inline-flex;
      align-items: center;
    }

    fk::after {
      content: "FK";
      font-size: 0.7em;
      margin-left: 6px;
      opacity: 0.6;
    }

    /* Le frecce nei vincoli */
    .constraints li::before {
      content: "↳";
      color: #94a3b8;
      font-size: 1.2em;
    }

  </style>
</head>

<body>

### Utente

<div class="entity-definition">
Partecipante(<pk>IdUtente</pk>, Email, Nome, Cognome, Password, isAdmin)
</div>

### Issue

<div class="entity-definition">
Issue(<pk>IdIssue</pk>, Titolo, Descrizione, Priorita, Immagine, Tipo, Stato, isArchiviato, <fk>idUtente</fk>, <fk>idProgetto</fk>)
</div>
<div class="constraints">
  Chiavi esterne:
  <ul>
    <li><fk>IdUtente</fk> → Utente(<pk>IdUtente</pk>)</li>
    <li><fk>IdProgetto</fk> → Progetto(<pk>IdProgetto</pk>)</li>
  </ul>
</div>

### Team

<div class="entity-definition">
Team(<pk>IdTeam</pk>, Nome, <fk>IdAdmin</fk>)
</div>
<div class="constraints">
  Chiavi esterne:
  <ul>
    <li><fk>IdAdmin</fk> → Utente(<pk>IdUtente</pk>)</li>
  </ul>
</div>

### Progetto

<div class="entity-definition">
Progetto(<pk>IdProgetto</pk>, Nome, Stato, <fk>IdTeam</fk>, <fk>IdAdmin</fk>)
</div>
<div class="constraints">
  Chiavi esterne:
  <ul>
    <li><fk>IdTeam</fk> → Team(<pk>IdTeam</pk>)</li>
    <li><fk>IdAdmin</fk> → Utente(<pk>IdUtente</pk>)</li>
  </ul>
</div>

### Assegnazioni

<div class="entity-definition">
Assegnazioni(<fk>IdUtente</fk>, <fk>IdIssue</fk>)
</div>  
<div class="constraints">
  Chiavi esterne:
  <ul>
    <li><fk>IdUtente</fk> → Utente(<pk>IdUtente</pk>)</li>
    <li><fk>IdIssue</fk> → Issue(<pk>IdIssue</pk>)</li>
  </ul>
</div>

### Partecipanti

<div class="entity-definition">
Partecipanti(<fk>IdUtente</fk>, <fk>IdTeam</fk>)
</div>
<div class="constraints">
  Chiavi esterne:
  <ul>
    <li><fk>IdUtente</fk> → Utente(<pk>IdUtente</pk>)</li>
    <li><fk>IdTeam</fk> → Team(<pk>IdTeam</pk>)</li>
  </ul>
</div>

</body>