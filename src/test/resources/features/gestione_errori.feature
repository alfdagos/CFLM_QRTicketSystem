# language: it
Feature: Gestione centralizzata degli errori
  Come sistema
  Voglio gestire tutti gli errori in modo consistente
  In modo da fornire risposte chiare e utili agli utenti e ai client API

  Scenario: Biglietto non trovato restituisce 404
    Dato che non esiste nessun biglietto con ID "id-inesistente"
    Quando invio una richiesta GET a "/api/tickets/id-inesistente"
    Allora la risposta ha status code 404
    E il body JSON contiene il campo "error" con valore "Ticket Not Found"
    E il body JSON contiene il campo "message" non vuoto
    E il body JSON contiene il campo "timestamp"

  Scenario: Biglietto già utilizzato restituisce 409
    Dato che esiste un biglietto con ID "abc-456" già utilizzato
    Quando invio una richiesta POST a "/reception/verify/abc-456"
    Allora la risposta ha status code 409
    E il body JSON contiene il campo "error" con valore "Ticket Already Used"

  Scenario: Errore di generazione QR Code restituisce 500
    Dato che il servizio di generazione QR Code solleva un'eccezione
    Quando creo un biglietto valido
    Allora la risposta ha status code 500
    E il body JSON contiene il campo "error" con valore "QR Code Generation Error"

  Scenario: Errori di validazione input restituiscono 400 con dettagli
    Dato che invio una richiesta POST a "/api/tickets"
    Con il body JSON con campi non validi
    Allora la risposta ha status code 400
    E il body JSON contiene un array "errors" con i dettagli di ogni campo non valido
    E ogni elemento dell'array contiene il nome del campo e il messaggio di errore

  Scenario: Eccezione generica non gestita restituisce 500
    Dato che si verifica un errore interno non previsto durante l'elaborazione
    Allora la risposta ha status code 500
    E il body JSON contiene il campo "error" con valore "Internal Server Error"
    E non vengono esposti dettagli tecnici sensibili
