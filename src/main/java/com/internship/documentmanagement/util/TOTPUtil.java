package com.internship.documentmanagement.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TOTPUtil {
    private static final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private static final TimeProvider timeProvider = new SystemTimeProvider();
    private static final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private static final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    public static String generateSecret() {
        return secretGenerator.generate();
    }
    public static boolean verifyCode(String secret, String code) {
        try {
            return codeVerifier.isValidCode(secret, code);
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateQRCodeDataUrl(String secret, String email, String issuer)
            throws WriterException, IOException {

        String authenticatorUri = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                issuer, email, secret, issuer
        );

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(
                authenticatorUri,
                BarcodeFormat.QR_CODE,
                300, 300
        );

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        javax.imageio.ImageIO.write(image, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String base64Image = Base64.encodeBase64String(imageBytes);
        return "data:image/png;base64," + base64Image;
    }

    public static String getManualEntryKey(String secret) {
        return secret;
    }
}
