package it.cflm.qrticketsystem.dto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Test di validazione per i DTO.
 */
class TicketDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void ticketRequestDTO_shouldBeValid_whenAllFieldsAreCorrect() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                "mario.rossi@example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenEventNameIsBlank() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "",
                "Mario Rossi",
                "mario.rossi@example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("eventName"));
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenUserNameIsBlank() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "",
                "mario.rossi@example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userName"));
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenUserEmailIsInvalid() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                "invalid-email"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userEmail"));
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenUserEmailIsBlank() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                ""
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userEmail"));
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenAllFieldsAreNull() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(null, null, null);

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(3);
    }

    @Test
    void ticketRequestDTO_shouldBeValid_withMinimumLength() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "ABC",
                "AB",
                "a@b.c"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_whenUserNameIsTooShort() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "A",
                "mario.rossi@example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("userName") &&
                v.getMessage().contains("size") || v.getMessage().contains("2")
        );
    }

    @Test
    void ticketRequestDTO_shouldBeValid_withComplexEmail() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party",
                "Mario Rossi",
                "mario.rossi+test@sub-domain.example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void ticketRequestDTO_shouldBeValid_withUnicodeCharacters() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "CFLM 2025 Party 🎉",
                "Françoise Müller",
                "francoise.muller@example.com"
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void ticketRequestDTO_shouldBeInvalid_withWhitespaceOnlyFields() {
        // Given
        TicketRequestDTO dto = new TicketRequestDTO(
                "   ",
                "   ",
                "   "
        );

        // When
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(4); // @NotBlank per eventName, userName e 2 errori per email (@NotBlank e @Email)
    }
}
