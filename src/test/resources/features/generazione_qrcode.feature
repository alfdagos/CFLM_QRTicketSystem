# language: it
Feature: Generazione e recupero QR Code
  Come sistema
  Voglio generare QR Code univoci per ogni biglietto
  In modo che ogni partecipante possa essere identificato univocamente all'ingresso

  Background:
    Dato che il sistema è operativo

  Scenario: Il QR Code viene generato al momento della creazione del biglietto
    Dato che creo un biglietto per "Mario Rossi" all'evento "CFLM 2025"
    Allora il biglietto ha un QR Code associato
    E il QR Code è un'immagine PNG valida
    E il QR Code ha dimensioni 300x300 pixel

  Scenario: Recupero immagine QR Code tramite API
    Dato che esiste un biglietto con ID "abc-123"
    Quando invio una richiesta GET a "/qrcode/abc-123"
    Allora la risposta ha status code 200
    E il Content-Type è "image/png"
    E il body contiene i byte dell'immagine PNG

  Scenario: Recupero QR Code per biglietto inesistente
    Dato che non esiste nessun biglietto con ID "id-inesistente"
    Quando invio una richiesta GET a "/qrcode/id-inesistente"
    Allora la risposta ha status code 404

  Scenario: Il QR Code è leggibile e contiene l'ID del biglietto
    Dato che creo un biglietto e ottengo l'ID "abc-789"
    Quando decodifico il QR Code del biglietto
    Allora il contenuto del QR Code corrisponde all'ID del biglietto "abc-789"

  Scenario: Due biglietti distinti hanno QR Code diversi
    Dato che creo un biglietto per "Mario Rossi" e ottengo il QR Code A
    E creo un secondo biglietto per "Luigi Bianchi" e ottengo il QR Code B
    Allora il QR Code A è diverso dal QR Code B

  Scenario: Errore nella generazione del QR Code
    Dato che il servizio di generazione QR Code non è disponibile
    Quando creo un biglietto per "Mario Rossi" all'evento "CFLM 2025"
    Allora la risposta ha status code 500
    E il body contiene un messaggio di errore appropriato
