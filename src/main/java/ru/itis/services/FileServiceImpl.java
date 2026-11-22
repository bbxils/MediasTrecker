package ru.itis.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileServiceImpl implements FileService {

    private final String uploadDir;

    public FileServiceImpl(String uploadDir) {
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(Path.of(this.uploadDir));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать папку для загрузок: " + uploadDir, e);
        }
    }

    @Override
    public String saveFile(InputStream file, String originalFileName, String contentType, Long size) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Можно загружать только изображения");
        }

        String name = originalFileName == null ? "" : originalFileName;
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot) : "";

        String storageFileName = UUID.randomUUID() + extension;
        Path path = Paths.get(uploadDir, storageFileName);

        try {
            Files.copy(file, path);
            return "uploads/" + storageFileName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }
}