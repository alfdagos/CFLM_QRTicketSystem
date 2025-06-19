package it.cflm.qrticketsystem.repository;

import it.cflm.qrticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository per l'accesso ai dati dell'entità Ticket.
 * Spring Data JPA fornisce automaticamente i metodi CRUD.
 */
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByQrCodeData(String qrCodeData);
}
