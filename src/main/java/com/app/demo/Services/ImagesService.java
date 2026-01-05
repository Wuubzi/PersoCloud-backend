package com.app.demo.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImagesService {

    @Value("${storage.images}")
    private String imagePath;

    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB

    public String guardarImagen(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }


        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("La imagen no puede superar los 2MB");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new RuntimeException("El archivo no es una imagen válida");
        }


        String fileName = UUID.randomUUID() + ".jpg";
        Path outputPath = Paths.get(imagePath, fileName);

        // 📸 Normalizar a JPG
        ImageIO.write(image, "jpg", outputPath.toFile());

        // 🔒 Validar MIME final
        String mimeType = Files.probeContentType(outputPath);
        if (mimeType == null || !mimeType.startsWith("image/")) {
            Files.deleteIfExists(outputPath);
            throw new RuntimeException("Tipo de archivo no permitido");
        }

        return fileName;
    }

    public String guardarImagenBase64(String base64) throws IOException {

        if (base64 == null || base64.isBlank()) {
            return null;
        }

        String base64Data = base64.contains(",")
                ? base64.split(",")[1]
                : base64;

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        if (imageBytes.length > MAX_IMAGE_SIZE) {
            throw new RuntimeException("La imagen no puede superar los 2MB");
        }

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            throw new RuntimeException("El archivo no es una imagen válida");
        }

        String fileName = UUID.randomUUID() + ".jpg";
        Path outputPath = Paths.get(imagePath, fileName);

        ImageIO.write(image, "jpg", outputPath.toFile());

        return fileName;
    }

    public void eliminarImagen(String fileName) throws IOException {
        if (fileName == null) return;
        Files.deleteIfExists(Paths.get(imagePath, fileName));
    }
}
