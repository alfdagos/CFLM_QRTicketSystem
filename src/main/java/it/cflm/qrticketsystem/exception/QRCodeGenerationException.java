package it.cflm.qrticketsystem.exception;

/**
 * Eccezione lanciata quando si verifica un errore nella generazione del QR Code.
 */
public class QRCodeGenerationException extends RuntimeException {
    
    public QRCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
