# language: it
Feature: Validazione biglietto alla reception
  Come operatore di reception
  Voglio poter scansionare e validare i QR Code dei biglietti
  In modo da consentire l'accesso solo ai partecipanti con biglietto valido

  Background:
    Dato che sono autenticato come "reception" con password "reception123"
    E mi trovo sulla pagina reception scanner

  Scenario: Validazione di un biglietto valido
    Dato che esiste un biglietto con ID "abc-123" non ancora utilizzato
    Quando invio una richiesta POST a "/reception/verify/abc-123"
    Allora la risposta ha status code 200
    E il campo "valid" è uguale a true
    E il campo "message" contiene "valido"
    E il biglietto viene marcato come utilizzato

  Scenario: Tentativo di riutilizzo di un biglietto già validato
    Dato che esiste un biglietto con ID "abc-456" già utilizzato
    Quando invio una richiesta POST a "/reception/verify/abc-456"
    Allora la risposta ha status code 409
    E il campo "valid" è uguale a false
    E il campo "message" contiene "già utilizzato"
    E il biglietto non cambia stato

  Scenario: Validazione di un biglietto inesistente
    Dato che non esiste nessun biglietto con ID "id-inesistente"
    Quando invio una richiesta POST a "/reception/verify/id-inesistente"
    Allora la risposta ha status code 404
    E il campo "valid" è uguale a false
    E il campo "message" contiene "non trovato"

  Scenario: Scanner QR Code via webcam — biglietto valido
    Dato che mi trovo sulla pagina reception scanner
    Quando il browser acquisisce il QR Code del biglietto "abc-123" tramite webcam
    Allora viene mostrato un messaggio di conferma verde
    E viene mostrato il nome del partecipante
    E viene mostrato il nome dell'evento

  Scenario: Scanner QR Code via webcam — biglietto già usato
    Dato che mi trovo sulla pagina reception scanner
    Quando il browser acquisisce il QR Code del biglietto "abc-456" già utilizzato
    Allora viene mostrato un messaggio di errore rosso
    E viene mostrato il testo "biglietto già utilizzato"

  Scenario: Scanner QR Code via webcam — biglietto non trovato
    Dato che mi trovo sulla pagina reception scanner
    Quando il browser acquisisce un QR Code non riconosciuto dal sistema
    Allora viene mostrato un messaggio di errore rosso
    E viene mostrato il testo "biglietto non trovato"
