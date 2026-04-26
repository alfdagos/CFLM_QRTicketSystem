# language: it
Feature: Creazione biglietto con QR Code
  Come utente del sistema
  Voglio poter creare un biglietto per un evento CFLM
  In modo da ricevere un QR Code unico per accedere all'evento

  Background:
    Dato che il sistema è operativo
    E il database è raggiungibile

  Scenario: Creazione biglietto con dati validi tramite form web
    Dato che mi trovo sulla pagina principale
    Quando compilo il campo "Nome Evento" con "CFLM Party 2025"
    E compilo il campo "Nome Partecipante" con "Mario Rossi"
    E compilo il campo "Email" con "mario.rossi@example.com"
    E clicco su "Genera Biglietto"
    Allora vengo reindirizzato alla pagina di dettaglio del biglietto
    E vedo un QR Code generato
    E il biglietto ha stato "non utilizzato"

  Scenario: Creazione biglietto tramite API REST con dati validi
    Dato che invio una richiesta POST a "/api/tickets"
    Con il body JSON:
      """
      {
        "eventName": "CFLM Party 2025",
        "userName": "Mario Rossi",
        "userEmail": "mario.rossi@example.com"
      }
      """
    Allora la risposta ha status code 201
    E il body contiene un campo "ticketId" non nullo
    E il body contiene un campo "qrCodeUrl" non nullo
    E il campo "valid" è uguale a true

  Scenario: Creazione biglietto con email non valida
    Dato che invio una richiesta POST a "/api/tickets"
    Con il body JSON:
      """
      {
        "eventName": "CFLM Party 2025",
        "userName": "Mario Rossi",
        "userEmail": "email-non-valida"
      }
      """
    Allora la risposta ha status code 400
    E il body contiene il messaggio di errore relativo al campo "userEmail"

  Scenario: Creazione biglietto con campi obbligatori mancanti
    Dato che invio una richiesta POST a "/api/tickets"
    Con il body JSON:
      """
      {
        "eventName": "",
        "userName": "",
        "userEmail": ""
      }
      """
    Allora la risposta ha status code 400
    E il body contiene errori di validazione per tutti e tre i campi

  Scenario: Creazione biglietto con nome evento troppo lungo
    Dato che invio una richiesta POST a "/api/tickets"
    Con il body JSON con il campo "eventName" di 256 caratteri
    Allora la risposta ha status code 400
    E il body contiene il messaggio di errore relativo al campo "eventName"

  Scenario: Ogni biglietto creato ha un ID univoco
    Dato che creo un primo biglietto per "Mario Rossi" all'evento "CFLM 2025"
    E creo un secondo biglietto per "Luigi Bianchi" all'evento "CFLM 2025"
    Allora i due biglietti hanno ID UUID diversi
    E i due biglietti hanno QR Code diversi
