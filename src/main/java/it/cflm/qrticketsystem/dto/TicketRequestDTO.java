package it.cflm.qrticketsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la richiesta di creazione di un nuovo biglietto.
 * Contiene le validazioni dei dati di input.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {
    
    @NotBlank(message = "Il nome dell'evento è obbligatorio")
    @Size(min = 3, max = 200, message = "Il nome dell'evento deve essere tra 3 e 200 caratteri")
    private String eventName;
    
    @NotBlank(message = "Il nome utente è obbligatorio")
    @Size(min = 2, max = 100, message = "Il nome deve essere tra 2 e 100 caratteri")
    private String userName;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String userEmail;
}
