package org.flexitech.projects.icpms.common.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Component
public class QRCodeGenerator {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;

	/**
	 * Generates a QR code image as a temporary PNG file. The caller is responsible
	 * for deleting the file when done.
	 *
	 * @param content the text to encode in the QR code (e.g., coupon code or URL)
	 * @return a temporary File containing the QR code image
	 * @throws Exception if encoding fails
	 */
	public File generateQRImage(String content) throws Exception {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
		Path tempFile = Files.createTempFile("qrcode_", ".png");
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", tempFile);
		return tempFile.toFile();
	}

	public String generateRandomCouponCode() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
	}

	public String generateRandomOrderCode(String prefix) {
		String safePrefix = prefix == null ? "" : prefix;
		return safePrefix + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
	}

}