package it.cflm.qrticketsystem.service;

import it.cflm.qrticketsystem.config.QRCodeConfig;
import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.dto.TicketValidationResponseDTO;
import it.cflm.qrticketsystem.exception.TicketAlreadyUsedException;
import it.cflm.qrticketsystem.exception.TicketNotFoundException;
import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per il servizio TicketService.
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private QRCodeConfig qrCodeConfig;

    @InjectMocks
    private TicketService ticketService;

    private TicketRequestDTO validRequest;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        validRequest = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                "mario.rossi@example.com"
        );

        ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setEventName("CFLM 2025 Party");
        ticket.setUserName("Mario Rossi");
        ticket.setUserEmail("mario.rossi@example.com");
        ticket.setValid(true);
        ticket.setQrCodeData(ticket.getId().toString());

        when(qrCodeConfig.getWidth()).thenReturn(300);
        when(qrCodeConfig.getHeight()).thenReturn(300);
        when(qrCodeConfig.getFormat()).thenReturn("PNG");
    }

    @Test
    void createTicket_shouldCreateValidTicket() {
        // Given
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When
        TicketResponseDTO result = ticketService.createTicket(validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventName()).isEqualTo(validRequest.getEventName());
        assertThat(result.getUserName()).isEqualTo(validRequest.getUserName());
        assertThat(result.getUserEmail()).isEqualTo(validRequest.getUserEmail());
        assertThat(result.isValid()).isTrue();
        
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void getTicketById_shouldReturnTicket_whenExists() {
        // Given
        UUID ticketId = ticket.getId();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // When
        Ticket result = ticketService.getTicketById(ticketId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ticketId);
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void getTicketById_shouldThrowException_whenNotExists() {
        // Given
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ticketService.getTicketById(ticketId))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("Biglietto non trovato");
    }

    @Test
    void validateTicket_shouldMarkAsUsed_whenValid() {
        // Given
        UUID ticketId = ticket.getId();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When
        TicketValidationResponseDTO result = ticketService.validateTicket(ticketId);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessage()).contains("valido");
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void validateTicket_shouldThrowException_whenAlreadyUsed() {
        // Given
        UUID ticketId = ticket.getId();
        ticket.setValid(false); // Already used
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // When & Then
        assertThatThrownBy(() -> ticketService.validateTicket(ticketId))
                .isInstanceOf(TicketAlreadyUsedException.class)
                .hasMessageContaining("già stato utilizzato");
    }

    @Test
    void validateTicket_shouldThrowException_whenNotFound() {
        // Given
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        when(ticketRepository.findByQrCodeData(ticketId.toString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ticketService.validateTicket(ticketId))
                .isInstanceOf(TicketNotFoundException.class);
    }
}
