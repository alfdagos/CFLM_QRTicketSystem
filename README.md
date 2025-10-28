# CFLM QR Ticket System

## 📋 Descrizione

Sistema di gestione biglietti per eventi CFLM basato su **QR Code**, realizzato con **Java Spring Boot** e **H2 Database** (configurabile per PostgreSQL). Offre funzionalità complete per la generazione, visualizzazione e verifica dei biglietti, rendendolo ideale per eventi, conferenze o qualsiasi scenario che richieda un sistema di ingresso con validazione.

## ✨ Caratteristiche Principali

* ✅ **Generazione Biglietti Unici** con ID UUID
* 🔲 **QR Code Dinamici** per ogni biglietto
* 📱 **Scanner QR Code** via webcam per la reception
* ✔️ **Validazione Biglietti** con prevenzione duplicati
* 📊 **API REST** documentate con OpenAPI/Swagger
* 🔒 **Validazione Input** con Bean Validation
* 🚨 **Gestione Errori Centralizzata**
* 📝 **Logging Strutturato**
* 🧪 **Test Coverage Completo** - 66 test con JUnit 5, Mockito e AssertJ
* 🎨 **UI Responsive** con Thymeleaf e poster SVG personalizzato
* 🎯 **Architettura Pulita** con pattern DTO, Mapper e Service Layer

## 🏗️ Architettura

Il progetto segue un'architettura a livelli ben strutturata:

```
📦 qr-ticket-system
├── 🎯 controller/          # REST Controllers & Web Controllers
├── 🔧 service/             # Business Logic Layer
├── 💾 repository/          # Data Access Layer (Spring Data JPA)
├── 📋 model/               # JPA Entities
├── 📤 dto/                 # Data Transfer Objects
├── ⚠️ exception/           # Custom Exceptions & Global Handler
├── ⚙️ config/              # Configuration Classes
└── 🛠️ util/                # Utility Classes
```

## 🛠️ Tecnologie Utilizzate

### Backend:
* **Java 21**
* **Spring Boot 3.5.7**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
* **Lombok** - Riduzione boilerplate
* **ZXing** - Generazione QR Code
* **H2 Database** - Database in memoria (dev)

### Frontend:
* **Thymeleaf** - Template engine
* **HTML5/CSS3** - Struttura e stile
* **JavaScript** - Interattività
* **HTML5-QRCode** - Scanner QR lato browser

### Testing & Documentation:
* **JUnit 5 Jupiter** - Testing framework
* **Mockito** - Mocking framework con @MockBean
* **AssertJ** - Fluent assertions
* **Spring Boot Test** - Testing utilities (@WebMvcTest, @DataJpaTest)
* **Jakarta Bean Validation** - DTO validation testing
* **SpringDoc OpenAPI** - Documentazione API automatica

## ⚙️ Prerequisiti

* **Java Development Kit (JDK)**: versione 17 o superiore
* **Apache Maven**: 3.6+ per la gestione delle dipendenze
* **Un IDE**: (IntelliJ IDEA, Eclipse, VS Code con estensioni Java)

## � Installazione e Avvio

### 1. Clona il Repository

```bash
git clone https://github.com/alfdagos/CFLM_QRTicketSystem.git
cd CFLM_QRTicketSystem
```

### 2. Compila il Progetto

```bash
mvn clean install
```

### 3. Esegui i Test

```bash
mvn test
```

### 4. Avvia l'Applicazione

```bash
mvn spring-boot:run
```

L'applicazione sarà disponibile su `http://localhost:8080`

## 📚 Utilizzo

### 🎫 Creazione Biglietto

1. Vai su `http://localhost:8080/`
2. Compila il form con:
   - Nome Evento
   - Nome Partecipante
   - Email
3. Clicca su "Genera Biglietto"
4. Visualizza il biglietto con il QR Code generato

### 📱 Verifica Biglietti (Reception)

1. Vai su `http://localhost:8080/reception`
2. Inserisci la password di accesso: `admin`
3. Autorizza l'accesso alla webcam
4. Scansiona il QR Code del biglietto
5. Visualizza il risultato della validazione

### 🔌 API REST

#### Crea Biglietto
```http
POST /api/tickets
Content-Type: application/json

{
  "eventName": "CFLM 2025 Party",
  "userName": "Mario Rossi",
  "userEmail": "mario.rossi@example.com"
}
```

#### Verifica Biglietto
```http
POST /reception/verify/{ticketId}
```

#### Ottieni QR Code
```http
GET /qrcode/{ticketId}
```

### 📖 Documentazione API

Accedi alla documentazione Swagger: `http://localhost:8080/swagger-ui.html`

### 💾 Console H2 Database

Per visualizzare il database in memoria:
* URL: `http://localhost:8080/h2-console`
* JDBC URL: `jdbc:h2:mem:testdb`
* Username: `sa`
* Password: (lascia vuoto)

## 🧪 Testing

Il progetto include una **suite completa di 66 test** che coprono tutti i livelli dell'applicazione.

### Esegui tutti i test:
```bash
mvn test
```

### Test Coverage per Layer:

#### 1️⃣ **Controller Layer** (11 test - `TicketControllerTest`)
- ✅ Rendering pagina index
- ✅ Creazione biglietto (form e API REST)
- ✅ Visualizzazione dettaglio biglietto
- ✅ Download immagine QR Code
- ✅ Pagina scanner reception
- ✅ Verifica biglietto (valido, già usato, non trovato)
- ✅ Validazione input (errori 400)

#### 2️⃣ **Service Layer** (6 test - `TicketServiceTest`)
- ✅ Creazione biglietto con generazione QR Code
- ✅ Recupero biglietto per ID
- ✅ Validazione biglietto valido
- ✅ Gestione biglietto già utilizzato
- ✅ Gestione biglietto non trovato

#### 3️⃣ **Repository Layer** (11 test - `TicketRepositoryTest`)
- ✅ Operazioni CRUD complete (save, findById, update, delete)
- ✅ Query personalizzate (findByQrCodeData)
- ✅ Generazione UUID automatica
- ✅ Persistenza immagini QR Code (BLOB)
- ✅ Case sensitivity nelle ricerche

#### 4️⃣ **Mapper Utility** (7 test - `TicketMapperTest`)
- ✅ Conversione DTO → Entity
- ✅ Conversione Entity → DTO
- ✅ Gestione valori null
- ✅ Round-trip conversion (integrità dati)

#### 5️⃣ **Model Layer** (10 test - `TicketTest`)
- ✅ Valori di default corretti
- ✅ Getters e Setters
- ✅ Gestione null values
- ✅ Toggle stato validità
- ✅ Array byte per QR Code (fino a 1000 bytes)
- ✅ Formattazione UUID
- ✅ LocalDateTime precision
- ✅ Limiti VARCHAR(255)
- ✅ Email con caratteri speciali

#### 6️⃣ **DTO Validation** (11 test - `TicketDTOValidationTest`)
- ✅ Validazione @NotBlank per tutti i campi
- ✅ Validazione @Email per userEmail
- ✅ Validazione @Size per lunghezze min/max
- ✅ Gestione campi null
- ✅ Gestione whitespace-only
- ✅ Email complesse (subdomain, plus addressing)
- ✅ Supporto caratteri Unicode

#### 7️⃣ **Exception Handler** (6 test - `GlobalExceptionHandlerTest`)
- ✅ TicketNotFoundException → 404
- ✅ TicketAlreadyUsedException → 409
- ✅ QRCodeGenerationException → 500
- ✅ Validation errors → 400 con dettagli
- ✅ Generic exceptions → 500

#### 8️⃣ **Configuration** (3 test - `QRCodeConfigTest`)
- ✅ Caricamento proprietà QR Code (width, height, format)
- ✅ Validazione valori positivi
- ✅ Spring Boot context loading

#### 9️⃣ **Integration Test** (1 test - `QrticketsystemApplicationTests`)
- ✅ Application context loads successfully

### Test Statistics:
```
✅ Total Tests: 66
✅ Failures: 0
✅ Errors: 0
✅ Skipped: 0
✅ Success Rate: 100%
```

### Esegui test specifici:
```bash
# Solo test del controller
mvn test -Dtest=TicketControllerTest

# Solo test del service
mvn test -Dtest=TicketServiceTest

# Solo test di validazione
mvn test -Dtest=TicketDTOValidationTest
```

## 📁 Struttura del Progetto

```
src/
├── main/
│   ├── java/it/cflm/qrticketsystem/
│   │   ├── QrTicketSystemApplication.java
│   │   ├── config/
│   │   │   ├── OpenApiConfig.java
│   │   │   └── QRCodeConfig.java
│   │   ├── controller/
│   │   │   └── TicketController.java
│   │   ├── dto/
│   │   │   ├── TicketRequestDTO.java
│   │   │   ├── TicketResponseDTO.java
│   │   │   └── TicketValidationResponseDTO.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── QRCodeGenerationException.java
│   │   │   ├── TicketAlreadyUsedException.java
│   │   │   └── TicketNotFoundException.java
│   │   ├── model/
│   │   │   └── Ticket.java
│   │   ├── repository/
│   │   │   └── TicketRepository.java
│   │   ├── service/
│   │   │   └── TicketService.java
│   │   └── util/
│   │       └── TicketMapper.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css
│       │   ├── js/
│       │   │   └── scanner.js
│       │   └── img/
│       │       └── poster.svg         # Poster evento personalizzato
│       └── templates/
│           ├── index.html
│           ├── reception_scanner.html
│           ├── ticket_detail.html
│           └── ticket_not_found.html
└── test/
    └── java/it/cflm/qrticketsystem/
        ├── config/
        │   └── QRCodeConfigTest.java
        ├── controller/
        │   └── TicketControllerTest.java
        ├── dto/
        │   └── TicketDTOValidationTest.java
        ├── exception/
        │   └── GlobalExceptionHandlerTest.java
        ├── model/
        │   └── TicketTest.java
        ├── repository/
        │   └── TicketRepositoryTest.java
        ├── service/
        │   └── TicketServiceTest.java
        ├── util/
        │   └── TicketMapperTest.java
        └── QrticketsystemApplicationTests.java
```

## � Configurazione

### application.properties

```properties
# Database H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update

# QR Code Settings
qrcode.width=300
qrcode.height=300
qrcode.format=PNG

# Logging
logging.level.it.cflm.qrticketsystem=DEBUG
```

### Per usare PostgreSQL:

1. Decommenta la dipendenza PostgreSQL in `pom.xml`
2. Aggiorna `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_ticket_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## 🔒 Sicurezza

### Implementazioni Attuali:
- ✅ Validazione input con Bean Validation
- ✅ Gestione sicura delle eccezioni
- ✅ Logging degli accessi
- ✅ Protezione SQL Injection (JPA)

### Miglioramenti Suggeriti per Produzione:
- 🔐 **Spring Security** per autenticazione/autorizzazione
- 🔑 **JWT** per l'API REST
- 🔒 **HTTPS** obbligatorio
- 📧 **Email Verification**
- 🚦 **Rate Limiting**
- 🔐 **Crittografia QR Code**

## 📈 Miglioramenti Implementati

### Highlights Recenti:

#### 🧪 **Testing Completo**
- **66 test** che coprono tutti i livelli dell'applicazione
- Test di integrazione con **@WebMvcTest** e **@DataJpaTest**
- Copertura completa di controller, service, repository, mapper, model, DTOs
- Test di validazione Jakarta Bean Validation
- Test di gestione eccezioni centralizzata

#### 🎨 **UI/UX Migliorata**
- Poster SVG personalizzato per eventi (cocktail & music theme)
- Design responsive con effetti neon
- Grafica vettoriale scalabile

#### 🏗️ **Architettura**
- 🎯 **DTO Pattern** per separazione API/Model
- 🚨 **Global Exception Handler** centralizzato
- ✔️ **Bean Validation** su tutti gli input
- 📝 **Logging SLF4J** strutturato
- 💉 **Constructor Injection** con Lombok
- 🔄 **@Transactional** per consistenza dati
- � **Mapper Utility** per conversioni DTO/Entity
- 📖 **OpenAPI/Swagger** documentation

### Test Patterns Utilizzati:
- **AAA Pattern** (Arrange-Act-Assert)
- **Given-When-Then** per BDD-style tests
- **MockBean** per isolation testing
- **TestEntityManager** per JPA testing
- **MockMvc** per integration testing
- **Fluent Assertions** con AssertJ

    

   
      
