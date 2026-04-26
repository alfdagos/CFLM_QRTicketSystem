# language: it
Feature: Visualizzazione dettaglio biglietto
  Come partecipante
  Voglio poter visualizzare il mio biglietto con il relativo QR Code
  In modo da poterlo presentare all'ingresso dell'evento

  Background:
    Dato che il sistema è operativo

  Scenario: Visualizzazione pagina di dettaglio biglietto valido
    Dato che esiste un biglietto con ID "abc-123" per "Mario Rossi" all'evento "CFLM 2025"
    Quando navigo verso "/tickets/abc-123"
    Allora vedo il nome del partecipante "Mario Rossi"
    E vedo il nome dell'evento "CFLM 2025"
    E vedo l'email del partecipante
    E vedo il QR Code del biglietto
    E vedo la data di creazione del biglietto
    E vedo lo stato del biglietto

  Scenario: Visualizzazione pagina per biglietto inesistente
    Dato che non esiste nessun biglietto con ID "id-inesistente"
    Quando navigo verso "/tickets/id-inesistente"
    Allora vedo la pagina "Biglietto Non Trovato"
    E vedo un messaggio che indica che il biglietto non esiste

  Scenario: Il QR Code è visibile nella pagina di dettaglio
    Dato che esiste un biglietto con ID "abc-123"
    Quando navigo verso "/tickets/abc-123"
    Allora la pagina contiene un elemento immagine con src "/qrcode/abc-123"
    E l'immagine è visibile e caricata correttamente

  Scenario: La pagina di dettaglio è responsive
    Dato che esiste un biglietto con ID "abc-123"
    Quando navigo verso "/tickets/abc-123" da un dispositivo mobile
    Allora il layout si adatta correttamente alla dimensione dello schermo
    E tutti gli elementi sono visibili e utilizzabili
