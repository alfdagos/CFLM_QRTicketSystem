package it.cflm.qrticketsystem.controller;

import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.service.TicketService;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller per la gestione delle richieste web e delle API relative ai biglietti.
 */
@Controller
@RequestMapping("/")
@Tag(name = "Ticket Controller", description = "Gestione dei biglietti e QR Code")
public class TicketController {

    @Autowired
    private TicketService ticketService;

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
     * Mappa la richiesta POST a "/tickets" per creare un nuovo biglietto.
     * I dati del biglietto sono passati come parametri di richiesta.
     *
     * @param eventName Il nome dell'evento.
     * @param userName Il nome dell'utente.
     * @param userEmail L'email dell'utente.
     * @return ResponseEntity contenente il biglietto creato o un messaggio di errore.
     */
    @Operation(summary = "Crea un nuovo biglietto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Biglietto creato con successo"),
            @ApiResponse(responseCode = "500", description = "Errore nella creazione del biglietto")
    })
    @PostMapping("/tickets")
    public ResponseEntity<Ticket> createTicket(@RequestParam String eventName,
                                             @RequestParam String userName,
                                             @RequestParam String userEmail) {
        try {
            Ticket newTicket = ticketService.createTicket(eventName, userName, userEmail);
            // Reindirizza l'utente alla pagina del biglietto appena creato
            // Nota: per un'API REST pura, si restituirebbe solo il JSON del biglietto.
            // Qui usiamo ResponseEntity per la semplicità della risposta API.
            // Per il reindirizzamento in un contesto web, si userebbe "redirect:/ticket/" + newTicket.getId();
            return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
        } catch (WriterException | IOException e) {
            // Logga l'errore per il debugging
            System.err.println("Errore durante la creazione del biglietto: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mappa la richiesta GET a "/ticket/{ticketId}" per visualizzare i dettagli di un singolo biglietto.
     *
     * @param ticketId L'UUID del biglietto.
     * @param model Il modello per passare dati alla vista Thymeleaf.
     * @return Il nome della vista Thymeleaf (ticket_detail.html o ticket_not_found.html).
     */
    @GetMapping("/ticket/{ticketId}")
    public String viewTicket(@PathVariable UUID ticketId, Model model) {
        return ticketService.getTicketById(ticketId)
                .map(ticket -> {
                    model.addAttribute("ticket", ticket);
                    // Converte l'array di byte dell'immagine QR in una stringa Base64 per l'embedding nell'HTML
                    String base64QrCode = java.util.Base64.getEncoder().encodeToString(ticket.getQrCodeImage());
                    model.addAttribute("qrCodeBase64", base64QrCode);
                    return "ticket_detail"; // Riferimento a src/main/resources/templates/ticket_detail.html
                })
                .orElse("ticket_not_found"); // Riferimento a src/main/resources/templates/ticket_not_found.html
    }

    /**
     * Mappa la richiesta GET a "/qrcode/{ticketId}" per recuperare l'immagine del QR Code direttamente.
     * Utile se si desidera visualizzare il QR Code come un'immagine stand-alone.
     *
     * @param ticketId L'UUID del biglietto.
     * @return ResponseEntity contenente l'immagine PNG del QR Code o un 404.
     */
    @Operation(summary = "Ottieni l'immagine PNG del QR Code di un biglietto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR Code trovato"),
            @ApiResponse(responseCode = "404", description = "Biglietto non trovato")
    })
    @GetMapping(value = "/qrcode/{ticketId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable UUID ticketId) {
        return ticketService.getTicketById(ticketId)
                .map(ticket -> new ResponseEntity<>(ticket.getQrCodeImage(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Mappa la richiesta GET a "/reception" per visualizzare l'interfaccia di scansione QR Code per la reception.
     *
     * @param model Il modello per passare dati alla vista Thymeleaf.
     * @return Il nome della vista Thymeleaf (reception_scanner.html).
     */
    @GetMapping("/reception")
    public String reception(Model model) {
        // In un'applicazione reale, qui ci sarebbe un sistema di autenticazione robusto (es. Spring Security)
        model.addAttribute("passwordRequired", true); // Simula la richiesta di una password
        return "reception_scanner"; // Riferimento a src/main/resources/templates/reception_scanner.html
    }

    /**
     * Mappa la richiesta POST a "/reception/verify/{ticketId}" per verificare un biglietto.
     * Questa API verrà chiamata dal frontend (JavaScript) dopo la scansione di un QR Code.
     *
     * @param ticketId L'UUID del biglietto da verificare.
     * @return ResponseEntity contenente un messaggio di stato (valido/non valido).
     */
    @Operation(summary = "Verifica un biglietto tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Biglietto valido"),
            @ApiResponse(responseCode = "400", description = "Biglietto non valido o già usato")
    })
    @PostMapping("/reception/verify/{ticketId}")
    public ResponseEntity<String> verifyTicket(@PathVariable UUID ticketId) {
        // Questa API dovrebbe essere protetta in un'applicazione reale (es. con un token JWT)
        if (ticketService.validateTicket(ticketId)) {
            return new ResponseEntity<>("Biglietto valido e usato.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Biglietto non valido o già usato.", HttpStatus.BAD_REQUEST);
        }
    }
}
