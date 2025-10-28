package it.cflm.qrticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la risposta della validazione di un biglietto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketValidationResponseDTO {
    private boolean valid;
    private String message;
    private String eventName;
    private String userName;
}
