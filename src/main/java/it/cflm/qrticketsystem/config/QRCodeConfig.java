package it.cflm.qrticketsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configurazione esternalizzata per i parametri dei QR Code.
 */
@Configuration
@ConfigurationProperties(prefix = "qrcode")
@Data
public class QRCodeConfig {
    private int width = 300;
    private int height = 300;
    private String format = "PNG";
}
