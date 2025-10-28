package it.cflm.qrticketsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test unitari per la classe Ticket.
 */
class TicketTest {

    @Test
    void ticket_shouldBeCreatedWithDefaultValues() {
        // When
        Ticket ticket = new Ticket();

        // Then
        assertThat(ticket.getId()).isNull();
        assertThat(ticket.getEventName()).isNull();
        assertThat(ticket.getUserName()).isNull();
        assertThat(ticket.getUserEmail()).isNull();
        assertThat(ticket.getPurchaseDate()).isNull();
        assertThat(ticket.isValid()).isFalse(); // Primitive boolean defaults to false
        assertThat(ticket.getQrCodeData()).isNull();
        assertThat(ticket.getQrCodeImage()).isNull();
    }

    @Test
    void ticket_shouldSetAndGetAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        String eventName = "CFLM 2025 Party";
        String userName = "Mario Rossi";
        String userEmail = "mario.rossi@example.com";
        LocalDateTime purchaseDate = LocalDateTime.now();
        boolean isValid = true;
        String qrCodeData = "qr-code-data";
        byte[] qrCodeImage = new byte[]{1, 2, 3, 4, 5};

        // When
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setEventName(eventName);
        ticket.setUserName(userName);
        ticket.setUserEmail(userEmail);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setValid(isValid);
        ticket.setQrCodeData(qrCodeData);
        ticket.setQrCodeImage(qrCodeImage);

        // Then
        assertThat(ticket.getId()).isEqualTo(id);
        assertThat(ticket.getEventName()).isEqualTo(eventName);
        assertThat(ticket.getUserName()).isEqualTo(userName);
        assertThat(ticket.getUserEmail()).isEqualTo(userEmail);
        assertThat(ticket.getPurchaseDate()).isEqualTo(purchaseDate);
        assertThat(ticket.isValid()).isEqualTo(isValid);
        assertThat(ticket.getQrCodeData()).isEqualTo(qrCodeData);
        assertThat(ticket.getQrCodeImage()).isEqualTo(qrCodeImage);
    }

    @Test
    void ticket_shouldHandleNullValues() {
        // When
        Ticket ticket = new Ticket();
        ticket.setId(null);
        ticket.setEventName(null);
        ticket.setUserName(null);
        ticket.setUserEmail(null);
        ticket.setPurchaseDate(null);
        ticket.setQrCodeData(null);
        ticket.setQrCodeImage(null);

        // Then
        assertThat(ticket.getId()).isNull();
        assertThat(ticket.getEventName()).isNull();
        assertThat(ticket.getUserName()).isNull();
        assertThat(ticket.getUserEmail()).isNull();
        assertThat(ticket.getPurchaseDate()).isNull();
        assertThat(ticket.getQrCodeData()).isNull();
        assertThat(ticket.getQrCodeImage()).isNull();
    }

    @Test
    void ticket_shouldToggleValidStatus() {
        // Given
        Ticket ticket = new Ticket();

        // When & Then
        ticket.setValid(true);
        assertThat(ticket.isValid()).isTrue();

        ticket.setValid(false);
        assertThat(ticket.isValid()).isFalse();
    }

    @Test
    void ticket_shouldHandleQrCodeImageBytes() {
        // Given
        Ticket ticket = new Ticket();
        byte[] largeQrCodeImage = new byte[1000];
        for (int i = 0; i < largeQrCodeImage.length; i++) {
            largeQrCodeImage[i] = (byte) (i % 256);
        }

        // When
        ticket.setQrCodeImage(largeQrCodeImage);

        // Then
        assertThat(ticket.getQrCodeImage()).hasSize(1000);
        assertThat(ticket.getQrCodeImage()).isEqualTo(largeQrCodeImage);
    }

    @Test
    void ticket_shouldHandleEmptyQrCodeImage() {
        // Given
        Ticket ticket = new Ticket();
        byte[] emptyQrCodeImage = new byte[0];

        // When
        ticket.setQrCodeImage(emptyQrCodeImage);

        // Then
        assertThat(ticket.getQrCodeImage()).isEmpty();
    }

    @Test
    void ticket_shouldStoreUUIDAsId() {
        // Given
        Ticket ticket = new Ticket();
        UUID uuid = UUID.randomUUID();

        // When
        ticket.setId(uuid);

        // Then
        assertThat(ticket.getId()).isEqualTo(uuid);
        assertThat(ticket.getId().toString()).hasSize(36); // UUID string format
    }

    @Test
    void ticket_shouldStorePurchaseDateWithTime() {
        // Given
        Ticket ticket = new Ticket();
        LocalDateTime now = LocalDateTime.now();

        // When
        ticket.setPurchaseDate(now);

        // Then
        assertThat(ticket.getPurchaseDate()).isEqualTo(now);
        assertThat(ticket.getPurchaseDate().getYear()).isEqualTo(now.getYear());
        assertThat(ticket.getPurchaseDate().getMonth()).isEqualTo(now.getMonth());
        assertThat(ticket.getPurchaseDate().getDayOfMonth()).isEqualTo(now.getDayOfMonth());
    }

    @Test
    void ticket_shouldAllowLongEventNames() {
        // Given
        Ticket ticket = new Ticket();
        String longEventName = "A".repeat(255);

        // When
        ticket.setEventName(longEventName);

        // Then
        assertThat(ticket.getEventName()).hasSize(255);
        assertThat(ticket.getEventName()).isEqualTo(longEventName);
    }

    @Test
    void ticket_shouldAllowEmailWithSpecialCharacters() {
        // Given
        Ticket ticket = new Ticket();
        String complexEmail = "mario.rossi+test@sub-domain.example.com";

        // When
        ticket.setUserEmail(complexEmail);

        // Then
        assertThat(ticket.getUserEmail()).isEqualTo(complexEmail);
        assertThat(ticket.getUserEmail()).contains("@");
        assertThat(ticket.getUserEmail()).contains("+");
    }
}
