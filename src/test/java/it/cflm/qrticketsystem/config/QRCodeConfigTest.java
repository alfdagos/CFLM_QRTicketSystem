package it.cflm.qrticketsystem.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test per la configurazione QRCodeConfig.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "qrcode.width=350",
        "qrcode.height=350",
        "qrcode.format=PNG"
})
class QRCodeConfigTest {

    @Autowired
    private QRCodeConfig qrCodeConfig;

    @Test
    void qrCodeConfig_shouldLoadDefaultValues() {
        // Then
        assertThat(qrCodeConfig).isNotNull();
        assertThat(qrCodeConfig.getWidth()).isEqualTo(350);
        assertThat(qrCodeConfig.getHeight()).isEqualTo(350);
        assertThat(qrCodeConfig.getFormat()).isEqualTo("PNG");
    }

    @Test
    void qrCodeConfig_shouldHavePositiveDimensions() {
        // Then
        assertThat(qrCodeConfig.getWidth()).isPositive();
        assertThat(qrCodeConfig.getHeight()).isPositive();
    }

    @Test
    void qrCodeConfig_shouldHaveValidFormat() {
        // Then
        assertThat(qrCodeConfig.getFormat()).isNotBlank();
        assertThat(qrCodeConfig.getFormat()).isEqualToIgnoringCase("PNG");
    }
}
