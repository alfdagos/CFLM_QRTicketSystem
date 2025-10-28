package it.cflm.qrticketsystem.util;

import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.model.Ticket;
import lombok.experimental.UtilityClass;

/**
 * Utility class per la conversione tra entità e DTO.
 */
@UtilityClass
public class TicketMapper {
    
    /**
     * Converte un'entità Ticket in un DTO di risposta.
     *
     * @param ticket L'entità Ticket
     * @return Il DTO di risposta
     */
    public static TicketResponseDTO toResponseDTO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        
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
    
    /**
     * Converte un DTO di richiesta in un'entità Ticket (senza ID e QR code).
     *
     * @param requestDTO Il DTO di richiesta
     * @return L'entità Ticket
     */
    public static Ticket toEntity(TicketRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        Ticket ticket = new Ticket();
        ticket.setEventName(requestDTO.getEventName());
        ticket.setUserName(requestDTO.getUserName());
        ticket.setUserEmail(requestDTO.getUserEmail());
        
        return ticket;
    }
}
