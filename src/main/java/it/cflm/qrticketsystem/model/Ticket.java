package it.cflm.qrticketsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entità rappresentante un biglietto.
 * Mappata alla tabella 'tickets' nel database.
 */
@Entity
@Table(name = "tickets")
@Data // Genera automaticamente getter, setter, toString, equals, hashCode
@NoArgsConstructor // Genera un costruttore senza argomenti
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Genera un UUID per l'ID del biglietto
    private UUID id;
    private String eventName;
    private String userName;
    private String userEmail;
    private LocalDateTime purchaseDate;
    private boolean isValid; // Indica se il biglietto è ancora valido (non usato)

    @Column(columnDefinition = "TEXT") // Per salvare la stringa contenente l'ID del QR code
    private String qrCodeData;

    @Lob // Per salvare l'immagine del QR code come BLOB (byte array)
    @Column(columnDefinition = "BYTEA") // Specifica il tipo di colonna per PostgreSQL
    private byte[] qrCodeImage;
}
