package it.cflm.qrticketsystem.service;

import it.cflm.qrticketsystem.config.QRCodeConfig;
import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.dto.TicketValidationResponseDTO;
import it.cflm.qrticketsystem.exception.QRCodeGenerationException;
import it.cflm.qrticketsystem.exception.TicketAlreadyUsedException;
import it.cflm.qrticketsystem.exception.TicketNotFoundException;
import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.repository.TicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servizio per la gestione della logica di business relativa ai biglietti.
 * Include la generazione di QR Code e la validazione dei biglietti.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final QRCodeConfig qrCodeConfig;

    /**
     * Genera un'immagine QR Code come array di byte (PNG).
     *
     * @param text Il testo da codificare nel QR Code.
     * @return L'immagine del QR Code come array di byte.
     * @throws QRCodeGenerationException Se si verifica un errore durante la generazione del QR Code.
     */
    private byte[] generateQrCodeImage(String text) {
        try {
            log.debug("Generazione QR Code per: {}", text);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                text, 
                BarcodeFormat.QR_CODE, 
                qrCodeConfig.getWidth(), 
                qrCodeConfig.getHeight()
            );
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, qrCodeConfig.getFormat(), pngOutputStream);
            log.debug("QR Code generato con successo");
            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Errore durante la generazione del QR Code", e);
            throw new QRCodeGenerationException("Errore durante la generazione del QR Code", e);
        }
    }

    /**
     * Crea un nuovo biglietto, genera il suo QR Code e lo salva nel database.
     *
     * @param requestDTO Dati della richiesta di creazione biglietto
     * @return DTO contenente i dati del biglietto creato
     */
    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO requestDTO) {
        log.info("Creazione nuovo biglietto per evento: {}, utente: {}", 
                requestDTO.getEventName(), requestDTO.getUserName());
        
        Ticket ticket = new Ticket();
        ticket.setEventName(requestDTO.getEventName());
        ticket.setUserName(requestDTO.getUserName());
        ticket.setUserEmail(requestDTO.getUserEmail());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setValid(true);

        // Genera un UUID per il biglietto
        UUID ticketId = UUID.randomUUID();
        ticket.setId(ticketId);

        String qrCodeContent = ticketId.toString();
        byte[] qrCodeImage = generateQrCodeImage(qrCodeContent);

        ticket.setQrCodeData(qrCodeContent);
        ticket.setQrCodeImage(qrCodeImage);

        Ticket savedTicket = ticketRepository.save(ticket);
        
        log.info("Biglietto creato con successo, ID: {}", savedTicket.getId());
        
        return mapToResponseDTO(savedTicket);
    }

    /**
     * Recupera un biglietto tramite il suo ID.
     *
     * @param id L'ID del biglietto.
     * @return Il biglietto se trovato
     * @throws TicketNotFoundException se il biglietto non viene trovato
     */
    public Ticket getTicketById(UUID id) {
        log.debug("Ricerca biglietto con ID: {}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }
    
    /**
     * Recupera l'immagine del QR Code di un biglietto.
     *
     * @param id L'ID del biglietto.
     * @return L'immagine del QR Code come array di byte
     * @throws TicketNotFoundException se il biglietto non viene trovato
     */
    public byte[] getQrCodeImage(UUID id) {
        log.debug("Recupero QR Code per biglietto ID: {}", id);
        Ticket ticket = getTicketById(id);
        return ticket.getQrCodeImage();
    }

    /**
     * Valida un biglietto marcandolo come "usato" se è ancora valido.
     *
     * @param id L'ID del biglietto da validare.
     * @return DTO contenente il risultato della validazione
     * @throws TicketNotFoundException se il biglietto non viene trovato
     * @throws TicketAlreadyUsedException se il biglietto è già stato usato
     */
    @Transactional
    public TicketValidationResponseDTO validateTicket(UUID id) {
        log.info("Tentativo di validazione biglietto ID: {}", id);
        
        Ticket ticket = ticketRepository.findById(id)
                .or(() -> ticketRepository.findByQrCodeData(id.toString()))
                .orElseThrow(() -> new TicketNotFoundException(id));
        
        if (!ticket.isValid()) {
            log.warn("Biglietto già usato, ID: {}", id);
            throw new TicketAlreadyUsedException(id);
        }
        
        ticket.setValid(false);
        ticketRepository.save(ticket);
        
        log.info("Biglietto validato con successo, ID: {}", id);
        
        return TicketValidationResponseDTO.builder()
                .valid(true)
                .message("Biglietto valido e registrato come usato")
                .eventName(ticket.getEventName())
                .userName(ticket.getUserName())
                .build();
    }
    
    /**
     * Mappa un'entità Ticket in un DTO di risposta.
     *
     * @param ticket L'entità ticket da mappare
     * @return Il DTO di risposta
     */
    private TicketResponseDTO mapToResponseDTO(Ticket ticket) {
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .eventName(ticket.getEventName())
                .userName(ticket.getUserName())
                .userEmail(ticket.getUserEmail())
                .purchaseDate(ticket.getPurchaseDate())
                .isValid(ticket.isValid())
                .qrCodeData(ticket.getQrCodeData())
                .build();
    }
}
