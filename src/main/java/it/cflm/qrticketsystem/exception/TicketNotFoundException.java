package it.cflm.qrticketsystem.exception;

import java.util.UUID;

/**
 * Eccezione lanciata quando un biglietto non viene trovato.
 */
public class TicketNotFoundException extends RuntimeException {
    
    public TicketNotFoundException(UUID ticketId) {
        super("Biglietto non trovato con ID: " + ticketId);
    }
    
    public TicketNotFoundException(String message) {
        super(message);
    }
}
