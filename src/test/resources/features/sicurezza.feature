# language: it
Feature: Sicurezza del sistema
  Come sistema di gestione eventi
  Voglio applicare misure di sicurezza adeguate
  In modo da proteggere i dati dei partecipanti e l'integrità degli accessi

  Scenario: Protezione CSRF su richieste POST
    Dato che sono autenticato come utente valido
    Quando invio una richiesta POST a "/api/tickets" senza token CSRF
    Allora la risposta ha status code 403

  Scenario: Token CSRF incluso correttamente da Thymeleaf
    Dato che mi trovo sulla pagina principale
    Allora il form di creazione biglietto contiene un campo nascosto con il token CSRF

  Scenario: Le password sono memorizzate con BCrypt
    Dato che il sistema utilizza autenticazione in-memory
    Allora le password degli utenti "admin", "reception" e "user" sono codificate con BCrypt
    E non sono mai esposte in chiaro

  Scenario: SQL Injection non è possibile tramite i campi del form
    Dato che mi trovo sulla pagina principale
    Quando compilo il campo "Nome Partecipante" con "'; DROP TABLE tickets; --"
    E clicco su "Genera Biglietto"
    Allora il sistema gestisce l'input in modo sicuro tramite JPA
    E la tabella dei biglietti non viene compromessa

  Scenario: Le sessioni scadono dopo il logout
    Dato che sono autenticato come "reception"
    Quando effettuo il logout
    E utilizzo il cookie di sessione precedente per accedere a "/reception"
    Allora vengo reindirizzato alla pagina di login

  Scenario: Accesso all'H2 Console è limitato in sviluppo
    Dato che l'applicazione è in esecuzione in ambiente di sviluppo
    Quando navigo verso "/h2-console"
    Allora la console è accessibile solo in modalità development

  Scenario: Header di sicurezza presenti nelle risposte HTTP
    Dato che il sistema è operativo
    Quando invio una richiesta GET alla pagina principale
    Allora la risposta HTTP contiene header di sicurezza appropriati
