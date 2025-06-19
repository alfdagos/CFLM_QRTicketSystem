package it.cflm.qrticketsystem.service;

import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.repository.TicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servizio per la gestione della logica di business relativa ai biglietti.
 * Include la generazione di QR Code e la validazione dei biglietti.
 */
@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Genera un'immagine QR Code come array di byte (PNG).
     *
     * @param text Il testo da codificare nel QR Code.
     * @param width La larghezza dell'immagine del QR Code.
     * @param height L'altezza dell'immagine del QR Code.
     * @return L'immagine del QR Code come array di byte.
     * @throws WriterException Se si verifica un errore durante la codifica del QR Code.
     * @throws IOException Se si verifica un errore durante la scrittura dell'immagine.
     */
    public byte[] generateQrCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    /**
     * Crea un nuovo biglietto, genera il suo QR Code e lo salva nel database.
     *
     * @param eventName Il nome dell'evento.
     * @param userName Il nome dell'utente.
     * @param userEmail L'email dell'utente.
     * @return Il biglietto creato e salvato.
     * @throws WriterException Se si verifica un errore durante la generazione del QR Code.
     * @throws IOException Se si verifica un errore I/O durante la generazione del QR Code.
     */
    public Ticket createTicket(String eventName, String userName, String userEmail) throws WriterException, IOException {
        Ticket ticket = new Ticket();
        ticket.setEventName(eventName);
        ticket.setUserName(userName);
        ticket.setUserEmail(userEmail);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setValid(true); // Il biglietto è valido al momento della creazione

        // Genera un UUID per il biglietto prima di salvarlo, così possiamo usarlo come contenuto del QR Code
        UUID ticketId = UUID.randomUUID();
        ticket.setId(ticketId); // Imposta l'ID generato manualmente

        String qrCodeContent = ticketId.toString(); // Il contenuto del QR Code sarà l'ID del biglietto
        byte[] qrCodeImage = generateQrCodeImage(qrCodeContent, 200, 200); // Genera l'immagine del QR Code

        ticket.setQrCodeData(qrCodeContent); // Salva il contenuto testuale del QR Code
        ticket.setQrCodeImage(qrCodeImage); // Salva l'immagine del QR Code

        return ticketRepository.save(ticket); // Salva il biglietto nel database
    }

    /**
     * Recupera un biglietto tramite il suo ID.
     *
     * @param id L'ID del biglietto.
     * @return Un Optional contenente il biglietto se trovato, altrimenti vuoto.
     */
    public Optional<Ticket> getTicketById(UUID id) {
        return ticketRepository.findById(id);
    }

    /**
     * Valida un biglietto marcandolo come "usato" se è ancora valido.
     *
     * @param id L'ID del biglietto da validare.
     * @return true se il biglietto è stato validato con successo, false altrimenti (già usato o non trovato).
     */
    public boolean validateTicket(UUID id) {
        Optional<Ticket> optionalTicketId = ticketRepository.findById(id);
        Optional<Ticket> optionalTicket = ticketRepository.findByQrCodeData(id.toString());
        if (optionalTicket.isPresent()||optionalTicketId.isPresent()) {
            Ticket ticket = optionalTicket.get();
            if (ticket.isValid()) {
                ticket.setValid(false); // Marca il biglietto come usato
                ticketRepository.save(ticket); // Aggiorna il biglietto nel database
                return true; // Biglietto valido e ora usato
            }
        }
        return false; // Biglietto non trovato o già usato
    }
}
