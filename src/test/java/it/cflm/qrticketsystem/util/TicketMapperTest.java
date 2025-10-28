package it.cflm.qrticketsystem.util;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.model.Ticket;

/**
 * Test unitari per TicketMapper.
 */
class TicketMapperTest {

    @Test
    void toEntity_shouldMapRequestDtoToEntity() {
        // Given
        TicketRequestDTO requestDTO = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                "mario.rossi@example.com"
        );

        // When
        Ticket ticket = TicketMapper.toEntity(requestDTO);

        // Then
        assertThat(ticket).isNotNull();
        assertThat(ticket.getEventName()).isEqualTo(requestDTO.getEventName());
        assertThat(ticket.getUserName()).isEqualTo(requestDTO.getUserName());
        assertThat(ticket.getUserEmail()).isEqualTo(requestDTO.getUserEmail());
        assertThat(ticket.getId()).isNull(); // Not set yet
    }

    @Test
    void toResponseDTO_shouldMapEntityToResponseDto() {
        // Given
        UUID ticketId = UUID.randomUUID();
        LocalDateTime purchaseDate = LocalDateTime.now();
        
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setEventName("CFLM 2025 Party");
        ticket.setUserName("Mario Rossi");
        ticket.setUserEmail("mario.rossi@example.com");
        ticket.setValid(true);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setQrCodeData(ticketId.toString());

        // When
        TicketResponseDTO responseDTO = TicketMapper.toResponseDTO(ticket);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(ticketId);
        assertThat(responseDTO.getEventName()).isEqualTo(ticket.getEventName());
        assertThat(responseDTO.getUserName()).isEqualTo(ticket.getUserName());
        assertThat(responseDTO.getUserEmail()).isEqualTo(ticket.getUserEmail());
        assertThat(responseDTO.isValid()).isTrue();
        assertThat(responseDTO.getPurchaseDate()).isEqualTo(purchaseDate);
        assertThat(responseDTO.getQrCodeData()).isEqualTo(ticketId.toString());
    }

    @Test
    void toEntity_shouldHandleNullValues() {
        // Given
        TicketRequestDTO requestDTO = new TicketRequestDTO(null, null, null);

        // When
        Ticket ticket = TicketMapper.toEntity(requestDTO);

        // Then
        assertThat(ticket).isNotNull();
        assertThat(ticket.getEventName()).isNull();
        assertThat(ticket.getUserName()).isNull();
        assertThat(ticket.getUserEmail()).isNull();
    }

    @Test
    void toResponseDTO_shouldHandleNullQrCodeData() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setEventName("Test Event");
        ticket.setUserName("Test User");
        ticket.setUserEmail("test@example.com");
        ticket.setValid(false);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setQrCodeData(null);

        // When
        TicketResponseDTO responseDTO = TicketMapper.toResponseDTO(ticket);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getQrCodeData()).isNull();
        assertThat(responseDTO.isValid()).isFalse();
    }

    @Test
    void roundTrip_shouldMaintainDataIntegrity() {
        // Given
        TicketRequestDTO requestDTO = new TicketRequestDTO(
                "Round Trip Test Event",
                "Test User",
                "roundtrip@example.com"
        );

        // When
        Ticket ticket = TicketMapper.toEntity(requestDTO);
        ticket.setId(UUID.randomUUID());
        ticket.setQrCodeData(ticket.getId().toString());
        TicketResponseDTO responseDTO = TicketMapper.toResponseDTO(ticket);

        // Then
        assertThat(responseDTO.getEventName()).isEqualTo(requestDTO.getEventName());
        assertThat(responseDTO.getUserName()).isEqualTo(requestDTO.getUserName());
        assertThat(responseDTO.getUserEmail()).isEqualTo(requestDTO.getUserEmail());
    }

    @Test
    void toEntity_shouldReturnNull_whenInputIsNull() {
        // When
        Ticket ticket = TicketMapper.toEntity(null);

        // Then
        assertThat(ticket).isNull();
    }

    @Test
    void toResponseDTO_shouldReturnNull_whenInputIsNull() {
        // When
        TicketResponseDTO responseDTO = TicketMapper.toResponseDTO(null);

        // Then
        assertThat(responseDTO).isNull();
    }
}
