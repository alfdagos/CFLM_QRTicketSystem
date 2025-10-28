package it.cflm.qrticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO per la risposta contenente i dati del biglietto.
 * Non espone tutti i campi interni dell'entità.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {
    private UUID id;
    private String eventName;
    private String userName;
    private String userEmail;
    private LocalDateTime purchaseDate;
    private boolean isValid;
    private String qrCodeData;
}
