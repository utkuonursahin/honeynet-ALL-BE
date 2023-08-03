package com.beam.uploadfile.diskservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DiskService {
    public String saveFile(String id,byte[] data) throws IOException {
        String filename = UUID.randomUUID().toString();
        String base = "C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\unrestricted-file-upload-BE\\src\\main\\resources\\static\\uploded";
        if (!Files.exists(Paths.get(base))) {
            Files.createDirectory(Paths.get(base));
        }
        Path path = Paths.get(base + File.separator+filename);
        Files.write(path, data);
        return filename;
    }
}