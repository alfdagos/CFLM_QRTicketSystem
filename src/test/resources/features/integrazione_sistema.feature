# language: it
Feature: Integrazione end-to-end del sistema
  Come organizzatore di un evento CFLM
  Voglio un flusso completo di gestione biglietti
  In modo che l'intero processo dalla creazione alla validazione funzioni correttamente

  Scenario: Flusso completo — creazione e validazione biglietto
    Dato che il sistema è operativo
    Quando creo un biglietto per "Mario Rossi" all'evento "CFLM Party 2025" con email "mario@example.com"
    Allora ricevo un biglietto con un ID UUID valido e un QR Code
    Quando sono autenticato come "reception" e valido il biglietto appena creato
    Allora il biglietto viene marcato come utilizzato
    E il sistema restituisce un messaggio di conferma
    Quando tento di validare nuovamente lo stesso biglietto
    Allora il sistema restituisce un errore 409 "biglietto già utilizzato"

  Scenario: Flusso completo — biglietto non trovato in reception
    Dato che il sistema è operativo
    E sono autenticato come "reception"
    Quando valido un ID biglietto che non esiste nel sistema
    Allora il sistema restituisce un errore 404 "biglietto non trovato"
    E la pagina reception mostra un messaggio di errore appropriato

  Scenario: Il contesto Spring Boot si avvia correttamente
    Dato che l'applicazione viene avviata
    Allora il contesto Spring Boot si carica senza errori
    E tutti i bean necessari sono inizializzati correttamente
    E il server è in ascolto sulla porta 8080

  Scenario: Il database H2 è inizializzato correttamente all'avvio
    Dato che l'applicazione viene avviata
    Allora la tabella "tickets" esiste nel database H2
    E la tabella è vuota all'avvio dell'applicazione

  Scenario: La documentazione Swagger è accessibile
    Dato che il sistema è operativo
    Quando navigo verso "/swagger-ui.html"
    Allora la pagina Swagger UI viene caricata correttamente
    E tutti gli endpoint API sono documentati
