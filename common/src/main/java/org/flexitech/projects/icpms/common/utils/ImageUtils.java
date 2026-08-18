package org.flexitech.projects.icpms.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUtils {

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.context.path}")
	private String imageContextPath;

	@Value("${server.servlet.context-path}")
	private String applicationContextPath;

	private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

	public String uploadImage(MultipartFile file, String entityType, Long entityId) throws IOException {
		validateImageFile(file);
		String originalFileName = file.getOriginalFilename();
		String fileExtension = getFileExtension(originalFileName);
		String uniqueFileName = generateUniqueFileName(fileExtension);
		String entityDirectory = entityType.toLowerCase() + "s";
		String uploadPath = Paths.get(imagePath, entityDirectory, entityId.toString()).toString();
		File directory = new File(uploadPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(uploadPath, uniqueFileName);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		return Paths.get(imageContextPath, entityDirectory, entityId.toString(), uniqueFileName).toString()
				.replace("\\", "/");
	}

	public String uploadImage(File file, String entityType, Long entityId) throws IOException {
		validateImageFile(file);
		String fileExtension = getFileExtension(file.getName());
		String uniqueFileName = generateUniqueFileName(fileExtension);
		String entityDirectory = entityType.toLowerCase() + "s";
		String uploadPath = Paths.get(imagePath, entityDirectory, entityId.toString()).toString();
		File directory = new File(uploadPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path destinationPath = Paths.get(uploadPath, uniqueFileName);
		Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		return Paths.get(imageContextPath, entityDirectory, entityId.toString(), uniqueFileName).toString()
				.replace("\\", "/");
	}

	public void deleteImage(String imageUrl) throws IOException {
		if (imageUrl == null || !imageUrl.startsWith(imageContextPath)) {
			return;
		}
		String relativePath = imageUrl.substring(imageContextPath.length());
		Path filePath = Paths.get(imagePath, relativePath);
		if (Files.exists(filePath)) {
			Files.delete(filePath);
			deleteEmptyParentDirectories(filePath.getParent());
		}
	}

	public void deleteAllEntityImages(String entityType, Long entityId) throws IOException {
		String entityDirectory = entityType.toLowerCase() + "s";
		Path entityPath = Paths.get(imagePath, entityDirectory, entityId.toString());
		if (Files.exists(entityPath)) {
			Files.walk(entityPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	public String updateImage(MultipartFile newFile, String oldImageUrl, String entityType, Long entityId)
			throws IOException {
		if (oldImageUrl != null && !oldImageUrl.trim().isEmpty()) {
			deleteImage(oldImageUrl);
		}
		return uploadImage(newFile, entityType, entityId);
	}

	private void validateImageFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be null or empty");
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
		}
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
			throw new IllegalArgumentException("Invalid image type. Allowed types: JPEG, PNG, GIF, WEBP");
		}
	}

	private void validateImageFile(File file) throws IOException {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("File cannot be null or does not exist");
		}
		if (file.length() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
		}
		String contentType = Files.probeContentType(file.toPath());
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
			throw new IllegalArgumentException("Invalid image type. Allowed types: JPEG, PNG, GIF, WEBP");
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return ".jpg";
		}
		return fileName.substring(fileName.lastIndexOf("."));
	}

	private String generateUniqueFileName(String fileExtension) {
		return UUID.randomUUID().toString() + fileExtension;
	}

	private void deleteEmptyParentDirectories(Path directory) throws IOException {
		while (directory != null && Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				if (stream.findAny().isPresent()) {
					break;
				}
			}
			Files.delete(directory);
			directory = directory.getParent();
			if (directory != null && directory.toString().equals(imagePath)) {
				break;
			}
		}
	}

	public String getImageUrl(String relativePath) {
		if (relativePath == null || relativePath.trim().isEmpty()) {
			return null;
		}
		return applicationContextPath + relativePath;
	}

	public String copyImage(String sourceImageUrl, String targetEntityType, Long targetEntityId) throws IOException {
		if (sourceImageUrl == null || sourceImageUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("Source image URL cannot be null or empty");
		}
		if (!sourceImageUrl.startsWith(imageContextPath)) {
			throw new IllegalArgumentException("Invalid image URL: does not match image context path");
		}
		String relativePath = sourceImageUrl.substring(imageContextPath.length());
		Path sourcePath = Paths.get(imagePath, relativePath);
		if (!Files.exists(sourcePath)) {
			throw new IOException("Source image does not exist: " + sourcePath);
		}
		String entityDirectory = targetEntityType.toLowerCase() + "s";
		String targetDirectoryPath = Paths.get(imagePath, entityDirectory, targetEntityId.toString()).toString();
		File targetDir = new File(targetDirectoryPath);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		String fileExtension = getFileExtension(sourcePath.getFileName().toString());
		String newFileName = generateUniqueFileName(fileExtension);
		Path targetPath = Paths.get(targetDirectoryPath, newFileName);
		Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		return Paths.get(imageContextPath, entityDirectory, targetEntityId.toString(), newFileName).toString()
				.replace("\\", "/");
	}

	public String uploadImageFromBase64(String base64Image, String entityType, Long entityId) throws IOException {
		if (base64Image == null || base64Image.trim().isEmpty()) {
			throw new IllegalArgumentException("Base64 image string cannot be null or empty");
		}
		String[] parts = base64Image.split(",");
		String imageData;
		String contentType = null;
		if (parts.length == 2) {
			String header = parts[0];
			imageData = parts[1];
			if (header.contains("image/")) {
				contentType = header.substring(header.indexOf("image/"), header.indexOf(";"));
			}
		} else {
			imageData = base64Image;
		}
		byte[] imageBytes;
		try {
			imageBytes = Base64.getDecoder().decode(imageData);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid base64 encoding", e);
		}
		if (imageBytes.length > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
		}
		String fileExtension;
		if (contentType != null) {
			switch (contentType) {
				case "image/jpeg":
					fileExtension = ".jpg";
					break;
				case "image/png":
					fileExtension = ".png";
					break;
				case "image/gif":
					fileExtension = ".gif";
					break;
				case "image/webp":
					fileExtension = ".webp";
					break;
				default:
					fileExtension = ".jpg";
			}
		} else {
			fileExtension = detectExtensionFromBytes(imageBytes);
		}
		String uniqueFileName = generateUniqueFileName(fileExtension);
		String entityDirectory = entityType.toLowerCase() + "s";
		String uploadPath = Paths.get(imagePath, entityDirectory, entityId.toString()).toString();
		File directory = new File(uploadPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(uploadPath, uniqueFileName);
		Files.write(filePath, imageBytes);
		return Paths.get(imageContextPath, entityDirectory, entityId.toString(), uniqueFileName).toString()
				.replace("\\", "/");
	}

	private String detectExtensionFromBytes(byte[] bytes) {
		if (bytes.length >= 2) {
			if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
				return ".jpg";
			}
			if (bytes.length >= 4 && bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) {
				return ".png";
			}
			if (bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46) {
				return ".gif";
			}
			if (bytes.length >= 4 && bytes[0] == (byte) 0x52 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46 && bytes[3] == (byte) 0x46) {
				return ".webp";
			}
		}
		return ".jpg";
	}

	public String uploadImageFromUrl(String imageUrl, String entityType, Long entityId) throws IOException {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("Image URL cannot be null or empty");
		}
		URL url = new URL(imageUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(10000);
		conn.setInstanceFollowRedirects(true);
		conn.connect();
		int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("Failed to download image: HTTP " + responseCode);
		}
		String contentType = conn.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("URL does not point to an image. Content-Type: " + contentType);
		}
		long contentLength = conn.getContentLengthLong();
		if (contentLength > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("Image size exceeds maximum limit of 5MB");
		}
		byte[] imageBytes;
		try (InputStream inputStream = conn.getInputStream()) {
			imageBytes = inputStream.readAllBytes();
		}
		if (imageBytes.length > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("Image size exceeds maximum limit of 5MB");
		}
		String extension = getExtensionFromContentType(contentType);
		String fileName = "download_" + System.currentTimeMillis() + extension;
		Path tempFile = Files.createTempFile("img_", extension);
		Files.write(tempFile, imageBytes);
		try {
			return uploadImage(tempFile.toFile(), entityType, entityId);
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

	private String getExtensionFromContentType(String contentType) {
		if (contentType == null) return ".jpg";
		String lower = contentType.toLowerCase();
		if (lower.contains("jpeg") || lower.contains("jpg")) return ".jpg";
		if (lower.contains("png")) return ".png";
		if (lower.contains("gif")) return ".gif";
		if (lower.contains("webp")) return ".webp";
		return ".jpg";
	}
}