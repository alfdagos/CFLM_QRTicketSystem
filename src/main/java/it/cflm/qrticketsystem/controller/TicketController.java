package it.cflm.qrticketsystem.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.dto.TicketValidationResponseDTO;
import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller per la gestione delle richieste web e delle API relative ai biglietti.
 */
@Controller
@RequestMapping("/")
@Tag(name = "Ticket Controller", description = "Gestione dei biglietti e QR Code")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * Mappa la richiesta GET alla root ("/") per visualizzare la pagina principale dell'evento.
     *
     * @param model Il modello per passare dati alla vista Thymeleaf.
     * @return Il nome della vista Thymeleaf (index.html).
     */
    @GetMapping("/")
    public String index(Model model) {
        // Puoi aggiungere qui dati dinamici per il poster dell'evento o altre informazioni
        model.addAttribute("eventName", "Non succederà più! CFLM 2025 Party");
        return "index"; // Riferimento a src/main/resources/templates/index.html
    }

    /**
     * Pagina di login personalizzata.
     *
     * @return Il nome della vista Thymeleaf (login.html).
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Mappa la richiesta POST a "/tickets" per creare un nuovo biglietto.
     * I dati del biglietto sono passati come parametri di richiesta.
     *
     * @param eventName Il nome dell'evento.
     * @param userName Il nome dell'utente.
     * @param userEmail L'email dell'utente.
     * @return ResponseEntity contenente il biglietto creato.
     */
    @Operation(summary = "Crea un nuovo biglietto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Biglietto creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDTO> createTicket(
            @RequestParam String eventName,
            @RequestParam String userName,
            @RequestParam String userEmail) {
        
        log.info("Richiesta creazione biglietto per evento: {}", eventName);
        
        TicketRequestDTO requestDTO = new TicketRequestDTO(eventName, userName, userEmail);
        TicketResponseDTO newTicket = ticketService.createTicket(requestDTO);
        
        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }
    
    /**
     * API REST per creare un biglietto con validazione.
     *
     * @param requestDTO DTO con i dati del biglietto
     * @return ResponseEntity contenente il biglietto creato
     */
    @Operation(summary = "Crea un nuovo biglietto (API REST con validazione)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Biglietto creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/api/tickets")
    public ResponseEntity<TicketResponseDTO> createTicketApi(
            @Valid @RequestBody TicketRequestDTO requestDTO) {
        
        log.info("Richiesta API creazione biglietto per evento: {}", requestDTO.getEventName());
        
        TicketResponseDTO newTicket = ticketService.createTicket(requestDTO);
        
        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }

    /**
     * Mappa la richiesta GET a "/ticket/{ticketId}" per visualizzare i dettagli di un singolo biglietto.
     *
     * @param ticketId L'UUID del biglietto.
     * @param model Il modello per passare dati alla vista Thymeleaf.
     * @return Il nome della vista Thymeleaf (ticket_detail.html).
     */
    @GetMapping("/ticket/{ticketId}")
    public String viewTicket(
            @Parameter(description = "ID del biglietto") @PathVariable UUID ticketId, 
            Model model) {
        
        log.debug("Visualizzazione biglietto ID: {}", ticketId);
        
        Ticket ticket = ticketService.getTicketById(ticketId);
        model.addAttribute("ticket", ticket);
        
        // Converte l'array di byte dell'immagine QR in una stringa Base64 per l'embedding nell'HTML
        String base64QrCode = java.util.Base64.getEncoder().encodeToString(ticket.getQrCodeImage());
        model.addAttribute("qrCodeBase64", base64QrCode);
        
        return "ticket_detail";
    }

    /**
     * Mappa la richiesta GET a "/qrcode/{ticketId}" per recuperare l'immagine del QR Code direttamente.
     * Utile se si desidera visualizzare il QR Code come un'immagine stand-alone.
     *
     * @param ticketId L'UUID del biglietto.
     * @return ResponseEntity contenente l'immagine PNG del QR Code.
     */
    @Operation(summary = "Ottieni l'immagine PNG del QR Code di un biglietto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR Code trovato"),
            @ApiResponse(responseCode = "404", description = "Biglietto non trovato")
    })
    @GetMapping(value = "/qrcode/{ticketId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCodeImage(
            @Parameter(description = "ID del biglietto") @PathVariable UUID ticketId) {
        
        log.debug("Richiesta QR Code per biglietto ID: {}", ticketId);
        
        byte[] qrCodeImage = ticketService.getQrCodeImage(ticketId);
        return new ResponseEntity<>(qrCodeImage, HttpStatus.OK);
    }

    /**
     * Mappa la richiesta GET a "/reception" per visualizzare la pagina dello scanner QR Code.
     * Ora protetta da Spring Security - solo utenti con ruolo RECEPTION o ADMIN possono accedere.
     *
     * @param model Il modello per passare dati alla vista Thymeleaf.
     * @return Il nome della vista Thymeleaf (reception_scanner.html).
     */
    @GetMapping("/reception")
    public String reception(Model model) {
        return "reception_scanner"; // Riferimento a src/main/resources/templates/reception_scanner.html
    }

    /**
     * Mappa la richiesta POST a "/reception/verify/{ticketId}" per verificare un biglietto.
     * Questa API verrà chiamata dal frontend (JavaScript) dopo la scansione di un QR Code.
     *
     * @param ticketId L'UUID del biglietto da verificare.
     * @return ResponseEntity contenente il risultato della validazione.
     */
    @Operation(summary = "Verifica un biglietto tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Biglietto valido e registrato"),
            @ApiResponse(responseCode = "404", description = "Biglietto non trovato"),
            @ApiResponse(responseCode = "409", description = "Biglietto già utilizzato")
    })
    @PostMapping("/reception/verify/{ticketId}")
    public ResponseEntity<TicketValidationResponseDTO> verifyTicket(
            @Parameter(description = "ID del biglietto") @PathVariable UUID ticketId) {
        
        log.info("Richiesta verifica biglietto ID: {}", ticketId);
        
        TicketValidationResponseDTO response = ticketService.validateTicket(ticketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
