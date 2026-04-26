# 📋 Specifiche Gherkin — CFLM QR Ticket System

Questa directory contiene le **specifiche comportamentali** del sistema scritte in formato **Gherkin** (BDD — Behaviour Driven Development), in lingua italiana.

## 📁 Struttura dei File

| File | Descrizione |
|------|-------------|
| `creazione_biglietto.feature` | Creazione biglietti tramite form web e API REST, validazione input |
| `validazione_biglietto.feature` | Verifica QR Code alla reception, gestione stati biglietto |
| `autenticazione.feature` | Login, logout, RBAC e protezione endpoint |
| `generazione_qrcode.feature` | Generazione, recupero e unicità dei QR Code |
| `visualizzazione_biglietto.feature` | Pagine di dettaglio biglietto e gestione not-found |
| `gestione_errori.feature` | Gestione centralizzata eccezioni (404, 409, 500, 400) |
| `sicurezza.feature` | CSRF, BCrypt, SQL Injection, sessioni e header sicurezza |
| `integrazione_sistema.feature` | Flussi end-to-end e verifica avvio applicazione |

## ▶️ Come Eseguire le Specifiche

Per eseguire questi scenari come test automatizzati, è necessario aggiungere una dipendenza **Cucumber** al `pom.xml`:

```xml
<!-- Cucumber per BDD -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>7.18.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.18.0</version>
    <scope>test</scope>
</dependency>
```

Successivamente, implementare i **Step Definitions** in Java nella directory:
```
src/test/java/it/cflm/qrticketsystem/bdd/steps/
```

## 🧩 Convenzioni

- Ogni `.feature` corrisponde a una funzionalità principale del sistema
- La keyword `Background` definisce il contesto comune agli scenari del file
- Ogni scenario segue il pattern **Dato / Quando / Allora** (Given / When / Then)
- I dati di esempio sono realistici e coerenti con le credenziali e gli endpoint documentati nel README principale
