package it.cflm.qrticketsystem.exception;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.cflm.qrticketsystem.controller.TicketController;
import it.cflm.qrticketsystem.service.TicketService;

/**
 * Test unitari per GlobalExceptionHandler.
 */
@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private TicketService ticketService;

    @Test
    void handleTicketNotFoundException_shouldReturnNotFoundStatus() throws Exception {
        // Given
        UUID ticketId = UUID.randomUUID();
        when(ticketService.getTicketById(ticketId))
                .thenThrow(new TicketNotFoundException("Biglietto non trovato con ID: " + ticketId));

        // When & Then
        mockMvc.perform(get("/ticket/{ticketId}", ticketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("non trovato")))
                .andExpect(jsonPath("$.status").value(404));

        verify(ticketService, times(1)).getTicketById(ticketId);
    }

    @Test
    void handleTicketAlreadyUsedException_shouldReturnConflictStatus() throws Exception {
        // Given
        UUID ticketId = UUID.randomUUID();
        when(ticketService.validateTicket(ticketId))
                .thenThrow(new TicketAlreadyUsedException(ticketId));

        // When & Then
        mockMvc.perform(post("/reception/verify/{ticketId}", ticketId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("già stato utilizzato")))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void handleQRCodeGenerationException_shouldReturnInternalServerError() throws Exception {
        // Given
        when(ticketService.createTicket(any()))
                .thenThrow(new QRCodeGenerationException("Errore nella generazione del QR Code", 
                        new Exception("Test exception")));

        // When & Then
        mockMvc.perform(post("/tickets")
                        .param("eventName", "Test Event")
                        .param("userName", "Test User")
                        .param("userEmail", "test@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("QR Code")))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "eventName": "",
                                    "userName": "",
                                    "userEmail": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void handleGeneralException_shouldReturnInternalServerError() throws Exception {
        // Given
        when(ticketService.getTicketById(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/ticket/{ticketId}", UUID.randomUUID()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void handleMethodArgumentNotValid_shouldIncludeFieldErrors() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "eventName": "",
                                    "userName": "A",
                                    "userEmail": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.status").value(400));
    }
}
