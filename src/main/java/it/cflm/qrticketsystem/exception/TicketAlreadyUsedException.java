package it.cflm.qrticketsystem.exception;

import java.util.UUID;

/**
 * Eccezione lanciata quando si tenta di validare un biglietto già usato.
 */
public class TicketAlreadyUsedException extends RuntimeException {
    
    public TicketAlreadyUsedException(UUID ticketId) {
        super("Il biglietto con ID " + ticketId + " è già stato utilizzato");
    }
}
