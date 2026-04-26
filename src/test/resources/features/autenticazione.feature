# language: it
Feature: Autenticazione e controllo accessi
  Come sistema di gestione eventi
  Voglio garantire che solo gli utenti autorizzati accedano alle funzionalità sensibili
  In modo da proteggere il processo di verifica dei biglietti

  Scenario: Login con credenziali admin valide
    Dato che mi trovo sulla pagina di login
    Quando inserisco username "admin" e password "admin123"
    E clicco su "Accedi"
    Allora vengo reindirizzato alla pagina principale
    E ho accesso a tutte le funzionalità

  Scenario: Login con credenziali reception valide
    Dato che mi trovo sulla pagina di login
    Quando inserisco username "reception" e password "reception123"
    E clicco su "Accedi"
    Allora vengo reindirizzato alla pagina reception scanner
    E ho accesso solo alle funzionalità di verifica biglietti

  Scenario: Login con credenziali errate
    Dato che mi trovo sulla pagina di login
    Quando inserisco username "admin" e password "passwordSbagliata"
    E clicco su "Accedi"
    Allora rimango sulla pagina di login
    E viene mostrato un messaggio di errore "Credenziali non valide"

  Scenario: Accesso alla reception senza autenticazione
    Dato che non sono autenticato
    Quando navigo verso "/reception"
    Allora vengo reindirizzato alla pagina di login

  Scenario: Accesso alle API REST senza token CSRF
    Dato che non sono autenticato
    Quando invio una richiesta POST a "/api/tickets" senza token CSRF
    Allora la risposta ha status code 403

  Scenario: Logout dalla sessione reception
    Dato che sono autenticato come "reception"
    Quando clicco sul pulsante "Logout"
    Allora la sessione viene terminata
    E vengo reindirizzato alla pagina di login
    E non posso più accedere a "/reception" senza ri-autenticarmi

  Scenario: Utente con ruolo USER tenta di accedere alla reception
    Dato che sono autenticato come "user" con password "user123"
    Quando navigo verso "/reception"
    Allora ricevo un errore 403 Forbidden
