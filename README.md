# QR Ticket System

Questo progetto è un sistema di gestione di biglietti per le feste gasanti del CFLM basato su **QR Code**, realizzato con **Java Spring Boot** per il backend e **PostgreSQL** come database. Offre funzionalità complete per la generazione, visualizzazione e verifica dei biglietti, rendendolo ideale per eventi, conferenze o qualsiasi scenario che richieda un sistema di ingresso con validazione.

## 🚀 Funzionalità

* **Generazione di Biglietti Unici:** Ogni "acquisto" di biglietto crea una voce unica nel database con un ID univoco.

* **QR Code Dinamici:** Per ogni biglietto viene generato un QR Code contenente l'ID univoco del biglietto.

* **Visualizzazione Biglietto Utente:** Un URL dedicato permette all'utente di visualizzare il proprio biglietto, inclusi i dettagli e l'immagine del QR Code.

* **Interfaccia Reception/Verifica:** Una pagina dedicata consente al personale di reception di scansionare i QR Code (tramite webcam del dispositivo) e verificare istantaneamente la validità del biglietto, marcandolo come "usato" per prevenire accessi duplicati.

* **Persistenza Dati:** Tutti i dati dei biglietti e il loro stato di validità sono memorizzati in un database PostgreSQL.

* **Frontend Semplificato:** Le pagine HTML sono servite tramite Thymeleaf, offrendo un'interfaccia utente funzionale per l'interazione con il sistema.

## 🛠️ Tecnologie Utilizzate

* **Backend:**

    * Java (versione 17+)

    * Spring Boot (versione 3.x.x)

    * Spring Data JPA (per l'interazione ORM con il database)

    * Lombok (per ridurre il boilerplate code)

    * ZXing (per la generazione dei QR Code)

* **Database:**

    * PostgreSQL

* **Frontend:**

    * Thymeleaf (per il templating HTML)

    * HTML, CSS, JavaScript

    * HTML5-QR Code (libreria JavaScript per la scansione lato browser)

## ⚙️ Prerequisiti

Assicurati di avere installati i seguenti strumenti sul tuo sistema:

* **Java Development Kit (JDK):** Versione 17 o superiore.

* **Apache Maven:** Per la gestione delle dipendenze e la compilazione del progetto.

* **PostgreSQL:** Un'istanza del database PostgreSQL in esecuzione.

* **Un IDE:** (Es. IntelliJ IDEA, Eclipse, VS Code con estensioni Java) per aprire e gestire il progetto.

## 🗄️ Configurazione del Database

1. **Crea il Database:**
   Apri il tuo client PostgreSQL (es. `psql` o pgAdmin) ed esegui il seguente comando per creare il database:
```sql
      CREATE DATABASE qr_ticket_system;
```
2. **Crea un Utente (Opzionale ma consigliato):**
   Se non vuoi usare l'utente `postgres` predefinito, puoi creare un nuovo utente e assegnare i permessi al database:
```sql
      CREATE USER your_username WITH PASSWORD 'your_password';
      GRANT ALL PRIVILEGES ON DATABASE qr_ticket_db TO your_username;
```
Sostituisci `your_username` e `your_password` con le credenziali che desideri utilizzare.

3. **Configura `application.properties`:**
   Nel file `src/main/resources/application.properties` del progetto, aggiorna le credenziali di connessione al database:
```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/qr_ticket_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.hibernate.ddl-auto=update # 'create' per la prima esecuzione, 'update' per le successive
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    
    spring.thymeleaf.cache=false
```
La proprietà `spring.jpa.hibernate.ddl-auto=update` farà sì che Spring Boot crei automaticamente la tabella `tickets` al primo avvio, se non esiste.

## 🏗️ Struttura del Progetto

Il progetto segue la tipica struttura di un'applicazione Spring Boot:
```
qr-ticket-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── it/
│   │   │       └── cflm/
│   │   │           └── qrticketsystem/
│   │   │               ├── QrTicketSystemApplication.java    // Classe principale dell'applicazione
│   │   │               ├── controller/
│   │   │               │   └── TicketController.java         // Controller Web e API REST
│   │   │               ├── model/
│   │   │               │   └── Ticket.java                   // Entità JPA per i Biglietti
│   │   │               ├── repository/
│   │   │               │   └── TicketRepository.java         // Repository Spring Data JPA
│   │   │               └── service/
│   │   │                   └── TicketService.java            // Logica di business e generazione QR
│   │   └── resources/
│   │       ├── application.properties  // Configurazione del database
│   │       ├── static/                 // Risorse statiche (CSS, JS, immagini)
│   │       │   └── img/
│   │       │       └── poster.jpg      // Immagine di esempio per il poster dell'evento
│   │       └── templates/              // Template HTML con Thymeleaf
│   │           ├── index.html          // Pagina principale/Acquisto biglietto
│   │           ├── reception_scanner.html // Pagina scanner per la reception
│   │           ├── ticket_detail.html  // Pagina dettagli del biglietto
│   │           └── ticket_not_found.html // Pagina di errore per biglietto non trovato
│   └── test/
│       └── java/
│           └── it/
│               └── cflm/
│                   └── qrticketsystem/
│                       └── QrTicketSystemApplicationTests.java // Test di esempio
└── pom.xml                             // File di configurazione Maven
```

## 🚀 Compilazione ed Esecuzione

1. **Clona il Repository (o crea il progetto):**

```bash
    git clone https://github.com/tuo-username/qr-ticket-system.git
    cd qr-ticket-system
```

*Nota:* Se stai riutilizzando il codice fornito senza un repository GitHub, assicurati di aver ricreato la struttura delle cartelle `it/cflm/qrticketsystem` e copiato i file sorgente al loro interno.

2. **Compila il Progetto:**
   Apri un terminale nella directory principale del progetto ed esegui:
```bash
    mvn clean install
```

Questo scaricherà tutte le dipendenze e compilerà l'applicazione.

3. **Aggiungi l'Immagine del Poster:**
   Crea la cartella `src/main/resources/static/img/` e inserisci un'immagine denominata `poster.jpg` al suo interno. Questa verrà visualizzata sulla pagina iniziale.

4. **Esegui l'Applicazione:**
   Puoi eseguire l'applicazione in due modi:

* **Tramite Maven:**

```bash
  mvn spring-boot:run
```

* **Tramite JAR eseguibile:**
  Dopo la compilazione, troverai un file JAR nella directory `target/` (es. `qr-ticket-system-0.0.1-SNAPSHOT.jar`). Eseguilo con:

```bash 
  java -jar target/qr-ticket-system-0.0.1-SNAPSHOT.jar
```

L'applicazione si avvierà su `http://localhost:8080` per impostazione predefinita.

## 🎯 Utilizzo dell'Applicazione

Una volta che l'applicazione è in esecuzione:

1. **Pagina Principale / Acquisto Biglietti:**

* Apri il tuo browser e vai a `http://localhost:8080/`.

* Vedrai una pagina di benvenuto con un modulo.

* Compila i campi (Nome Evento, Nome, Email) e clicca su **"Genera Biglietto"**.

* Verrai reindirizzato alla pagina di dettaglio del biglietto appena creato.

2. **Visualizzazione Biglietto:**

* Dopo aver generato un biglietto, la pagina mostrerà i dettagli del biglietto e l'immagine del QR Code associato.

* L'URL sarà nel formato `http://localhost:8080/ticket/{ID_DEL_BIGLIETTO}`. Puoi condividere questo link con l'utente del biglietto.

3. **Lettore QR Code (Reception):**

* Vai a `http://localhost:8080/reception`.

* Per accedere allo scanner, ti verrà chiesta una password. Per questo progetto dimostrativo, la password è `admin`.

* Dopo aver inserito la password, il browser chiederà l'autorizzazione per accedere alla tua webcam. Concedi l'accesso.

* Posiziona un QR Code di un biglietto (generato in precedenza) davanti alla webcam. La libreria HTML5-QR Code lo scansionerà automaticamente.

* Il sistema invierà l'ID scansionato al backend per la verifica. Un messaggio indicherà se il biglietto è `valido e usato` o `non valido o già usato`.

## 🔒 Considerazioni sulla Sicurezza e Miglioramenti Futuri

Questo progetto è un punto di partenza. Per un'applicazione pronta per la produzione, si raccomandano i seguenti miglioramenti:

* **Autenticazione e Autorizzazione Reali:** La password `admin` per la reception è per scopi dimostrativi. Implementa un sistema di autenticazione e autorizzazione robusto utilizzando **Spring Security** (con JWT, OAuth2, ecc.) per proteggere gli endpoint API e le pagine sensibili.

* **Crittografia QR Code:** Per una maggiore sicurezza, i dati all'interno del QR Code potrebbero essere crittografati o firmati digitalmente per prevenire la manomissione.

* **Validazione Input:** Aggiungi una validazione più rigorosa dei dati di input (`@Valid` e DTO in Spring Boot) per prevenire vulnerabilità e garantire l'integrità dei dati.

* **Gestione degli Errori:** Implementa un'intercettazione e una gestione degli errori più sofisticate per fornire feedback chiari all'utente in caso di problemi.

* **Frontend Avanzato:** Per un'esperienza utente più moderna e reattiva, integra un framework JavaScript come **React**, **Angular** o **Vue.js** per il frontend, comunicando con il backend Spring Boot tramite API REST.

* **Notifiche:** Aggiungi funzionalità per l'invio di email di conferma con il biglietto allegato dopo l'acquisto.

* **Test:** Scrivi test unitari, di integrazione e end-to-end per garantire l'affidabilità dell'applicazione.

Grazie per aver esplorato il progetto! Sentiti libero di contribuire, segnalare bug o suggerire miglioramenti

    

   
      