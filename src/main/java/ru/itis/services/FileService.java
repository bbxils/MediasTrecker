package ru.itis.services;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {
    String saveFile(InputStream file, String originalFileName, String contentType, Long size);
}
