package it.cflm.qrticketsystem.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import it.cflm.qrticketsystem.dto.TicketRequestDTO;
import it.cflm.qrticketsystem.dto.TicketResponseDTO;
import it.cflm.qrticketsystem.dto.TicketValidationResponseDTO;
import it.cflm.qrticketsystem.exception.TicketAlreadyUsedException;
import it.cflm.qrticketsystem.exception.TicketNotFoundException;
import it.cflm.qrticketsystem.model.Ticket;
import it.cflm.qrticketsystem.service.TicketService;

/**
 * Test unitari per TicketController.
 */
@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private TicketService ticketService;

    private Ticket ticket;
    private TicketResponseDTO responseDTO;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        
        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setEventName("CFLM 2025 Party");
        ticket.setUserName("Mario Rossi");
        ticket.setUserEmail("mario.rossi@example.com");
        ticket.setValid(true);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setQrCodeData(ticketId.toString());
        ticket.setQrCodeImage(new byte[]{1, 2, 3, 4, 5});

        responseDTO = new TicketResponseDTO(
                ticketId,
                "CFLM 2025 Party",
                "Mario Rossi",
                "mario.rossi@example.com",
                LocalDateTime.now(),
                true,
                ticketId.toString()
        );
    }

    @Test
    void index_shouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("eventName"))
                .andExpect(model().attribute("eventName", containsString("CFLM")));
    }

    @Test
    void createTicket_shouldReturnCreatedTicket() throws Exception {
        // Given
        when(ticketService.createTicket(any(TicketRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/tickets")
                        .param("eventName", "CFLM 2025 Party")
                        .param("userName", "Mario Rossi")
                        .param("userEmail", "mario.rossi@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.eventName").value("CFLM 2025 Party"))
                .andExpect(jsonPath("$.userName").value("Mario Rossi"))
                .andExpect(jsonPath("$.userEmail").value("mario.rossi@example.com"))
                .andExpect(jsonPath("$.valid").value(true));

        verify(ticketService, times(1)).createTicket(any(TicketRequestDTO.class));
    }

    @Test
    void createTicketApi_shouldReturnCreatedTicket() throws Exception {
        // Given
        when(ticketService.createTicket(any(TicketRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "eventName": "CFLM 2025 Party",
                                    "userName": "Mario Rossi",
                                    "userEmail": "mario.rossi@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.eventName").value("CFLM 2025 Party"));

        verify(ticketService, times(1)).createTicket(any(TicketRequestDTO.class));
    }

    @Test
    void createTicketApi_shouldReturnBadRequest_whenInvalidData() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "eventName": "",
                                    "userName": "",
                                    "userEmail": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).createTicket(any(TicketRequestDTO.class));
    }

    @Test
    void viewTicket_shouldReturnTicketDetailPage() throws Exception {
        // Given
        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);

        // When & Then
        mockMvc.perform(get("/ticket/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket_detail"))
                .andExpect(model().attributeExists("ticket"))
                .andExpect(model().attributeExists("qrCodeBase64"));

        verify(ticketService, times(1)).getTicketById(ticketId);
    }

    @Test
    void viewTicket_shouldReturnNotFound_whenTicketDoesNotExist() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(ticketService.getTicketById(nonExistentId))
                .thenThrow(new TicketNotFoundException("Biglietto non trovato con ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/ticket/{ticketId}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).getTicketById(nonExistentId);
    }

    @Test
    void getQrCodeImage_shouldReturnPngImage() throws Exception {
        // Given
        byte[] qrCodeBytes = new byte[]{1, 2, 3, 4, 5};
        when(ticketService.getQrCodeImage(ticketId)).thenReturn(qrCodeBytes);

        // When & Then
        mockMvc.perform(get("/qrcode/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(qrCodeBytes));

        verify(ticketService, times(1)).getQrCodeImage(ticketId);
    }

    @Test
    void reception_shouldReturnReceptionPage() throws Exception {
        mockMvc.perform(get("/reception"))
                .andExpect(status().isOk())
                .andExpect(view().name("reception_scanner"));
    }

    @Test
    void verifyTicket_shouldReturnValidResponse_whenTicketIsValid() throws Exception {
        // Given
        TicketValidationResponseDTO validationResponse = new TicketValidationResponseDTO(
                true,
                "Biglietto valido! Benvenuto Mario Rossi",
                "CFLM 2025 Party",
                "Mario Rossi"
        );
        when(ticketService.validateTicket(ticketId)).thenReturn(validationResponse);

        // When & Then
        mockMvc.perform(post("/reception/verify/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Benvenuto")))
                .andExpect(jsonPath("$.userName").value("Mario Rossi"))
                .andExpect(jsonPath("$.eventName").value("CFLM 2025 Party"));

        verify(ticketService, times(1)).validateTicket(ticketId);
    }

    @Test
    void verifyTicket_shouldReturnConflict_whenTicketAlreadyUsed() throws Exception {
        // Given
        when(ticketService.validateTicket(ticketId))
                .thenThrow(new TicketAlreadyUsedException(ticketId));

        // When & Then
        mockMvc.perform(post("/reception/verify/{ticketId}", ticketId))
                .andExpect(status().isConflict());

        verify(ticketService, times(1)).validateTicket(ticketId);
    }

    @Test
    void verifyTicket_shouldReturnNotFound_whenTicketDoesNotExist() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(ticketService.validateTicket(nonExistentId))
                .thenThrow(new TicketNotFoundException("Biglietto non trovato con ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(post("/reception/verify/{ticketId}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).validateTicket(nonExistentId);
    }
}
