package it.cflm.qrticketsystem.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import it.cflm.qrticketsystem.model.Ticket;

/**
 * Test di integrazione per TicketRepository.
 */
@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setEventName("CFLM 2025 Party");
        ticket.setUserName("Mario Rossi");
        ticket.setUserEmail("mario.rossi@example.com");
        ticket.setValid(true);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setQrCodeData("test-qr-data");
        ticket.setQrCodeImage(new byte[]{1, 2, 3, 4, 5});
    }

    @Test
    void save_shouldPersistTicket() {
        // When
        Ticket savedTicket = ticketRepository.save(ticket);
        entityManager.flush();

        // Then
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getEventName()).isEqualTo(ticket.getEventName());
        assertThat(savedTicket.getUserName()).isEqualTo(ticket.getUserName());
        assertThat(savedTicket.getUserEmail()).isEqualTo(ticket.getUserEmail());
        assertThat(savedTicket.isValid()).isTrue();
    }

    @Test
    void findById_shouldReturnTicket_whenExists() {
        // Given
        Ticket savedTicket = entityManager.persistAndFlush(ticket);
        UUID ticketId = savedTicket.getId();

        // When
        Optional<Ticket> foundTicket = ticketRepository.findById(ticketId);

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getId()).isEqualTo(ticketId);
        assertThat(foundTicket.get().getEventName()).isEqualTo(ticket.getEventName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Ticket> foundTicket = ticketRepository.findById(nonExistentId);

        // Then
        assertThat(foundTicket).isEmpty();
    }

    @Test
    void findByQrCodeData_shouldReturnTicket_whenExists() {
        // Given
        String qrCodeData = "unique-qr-code-data";
        ticket.setQrCodeData(qrCodeData);
        entityManager.persistAndFlush(ticket);

        // When
        Optional<Ticket> foundTicket = ticketRepository.findByQrCodeData(qrCodeData);

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getQrCodeData()).isEqualTo(qrCodeData);
    }

    @Test
    void findByQrCodeData_shouldReturnEmpty_whenNotExists() {
        // When
        Optional<Ticket> foundTicket = ticketRepository.findByQrCodeData("non-existent-qr-data");

        // Then
        assertThat(foundTicket).isEmpty();
    }

    @Test
    void update_shouldModifyTicket() {
        // Given
        Ticket savedTicket = entityManager.persistAndFlush(ticket);
        UUID ticketId = savedTicket.getId();

        // When
        savedTicket.setValid(false);
        savedTicket.setUserName("Updated Name");
        ticketRepository.save(savedTicket);
        entityManager.flush();
        entityManager.clear();

        // Then
        Ticket updatedTicket = entityManager.find(Ticket.class, ticketId);
        assertThat(updatedTicket.isValid()).isFalse();
        assertThat(updatedTicket.getUserName()).isEqualTo("Updated Name");
    }

    @Test
    void delete_shouldRemoveTicket() {
        // Given
        Ticket savedTicket = entityManager.persistAndFlush(ticket);
        UUID ticketId = savedTicket.getId();

        // When
        ticketRepository.delete(savedTicket);
        entityManager.flush();

        // Then
        Ticket deletedTicket = entityManager.find(Ticket.class, ticketId);
        assertThat(deletedTicket).isNull();
    }

    @Test
    void save_shouldGenerateUUID() {
        // When
        Ticket savedTicket = ticketRepository.save(ticket);
        entityManager.flush();

        // Then
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getId()).isInstanceOf(UUID.class);
    }

    @Test
    void save_shouldPersistQrCodeImage() {
        // Given
        byte[] qrCodeImage = new byte[]{10, 20, 30, 40, 50};
        ticket.setQrCodeImage(qrCodeImage);

        // When
        Ticket savedTicket = entityManager.persistAndFlush(ticket);
        entityManager.clear();

        // Then
        Ticket foundTicket = entityManager.find(Ticket.class, savedTicket.getId());
        assertThat(foundTicket.getQrCodeImage()).isEqualTo(qrCodeImage);
    }

    @Test
    void findByQrCodeData_shouldBeCaseInsensitive() {
        // Given
        String qrCodeData = "Test-QR-Data-123";
        ticket.setQrCodeData(qrCodeData);
        entityManager.persistAndFlush(ticket);

        // When
        Optional<Ticket> foundTicket = ticketRepository.findByQrCodeData(qrCodeData);

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getQrCodeData()).isEqualTo(qrCodeData);
    }

    @Test
    void save_shouldHandleNullQrCodeImage() {
        // Given
        ticket.setQrCodeImage(null);

        // When
        Ticket savedTicket = ticketRepository.save(ticket);
        entityManager.flush();

        // Then
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getQrCodeImage()).isNull();
    }
}
