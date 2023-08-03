package com.beam.uploadfile.fileupload;

import com.beam.uploadfile.diskservice.DiskService;
import com.beam.uploadfile.restservice.RestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class FileUploadService {
    private final FileUploadRepository fileUploadRepository;
    private final DiskService diskService;
    private final RestService restService;

    public void saveFile(FileUpload fileUpload, MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        String filename = diskService.saveFile(fileUpload.getId(), file.getBytes());
        String ipAddress = httpServletRequest.getRemoteAddr();
        FileUpload upload = new FileUpload();
        upload.setFileNameOriginal(file.getOriginalFilename())
                .setFileName(filename)
                .setOrigin(ipAddress)
                .setId(UUID.randomUUID().toString());
        restService.postSuspiciousFileActivity(upload);
        fileUploadRepository.save(upload);
    }
}