package ru.daru_jo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class FileService {
    private final Map<String, File> files = new HashMap<>();

    public String addFile(String name, String body) {
        try {
            File file = File.createTempFile(String.valueOf(Objects.requireNonNull(body).hashCode()), ".tmp");
            log.info(file.getAbsolutePath());
            file.deleteOnExit();

            try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
                out.println(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            files.put(name, file);
            return name;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public File getFile(String name) {
        return files.get(name);
    }

    public void delFile(String name) {
        File file = files.get(name);
        if (file == null) {
            return;
        }
        boolean flag = file.delete();
        log.info("файл удален {}? {}", name, flag);
        files.remove(name);

    }

    @PostConstruct
    private void init() {

        files.put("hi", resourceToFile("hi.jpg"));
        files.put("menu", resourceToFile("menu.jpg"));
    }

    public File resourceToFile(String fileName) {

        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            File f = File.createTempFile(String.valueOf(Objects.requireNonNull(in).hashCode()), ".tmp");
            log.info(f.getAbsolutePath());
            f.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(f)) {
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return f;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
