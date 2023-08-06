package com.beam.uploadfile.fileupload;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    private boolean isVulnerableExtension(String fileExtension) {
        List<String> vulnerableExtensions = Arrays.asList("php", "java","py","_exe", "a6p", "ac", "acr", "action", "air", "apk", "app",
                "applescript", "awk", "bas", "bat", "bin","cgi", "chm", "cmd", "com","cpl", "crt", "csh", "dek", "dld", "dll", "dmg", "drv", "ds", "ebm", "elf",
                "emf", "esh", "exe", "ezs", "fky", "frs", "fxp", "gadget", "gpe",
                "bin", "chm","jar", "jsp", "lnk", "mrc", "msi", "scr", "url", "vbs", "json");
        return vulnerableExtensions.contains(fileExtension.toLowerCase());
    }

    @PostMapping("/upload")
    public FileUpload saveUser(FileUpload fileUpload, @RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? FilenameUtils.getExtension(originalFilename) : "";
        if (file.isEmpty()) {
            throw new RuntimeException("Empty file");
        }
        if (isVulnerableExtension(fileExtension)){
            fileUploadService.saveFile(fileUpload, file, httpServletRequest);
        }
        return fileUpload;
    }
}
