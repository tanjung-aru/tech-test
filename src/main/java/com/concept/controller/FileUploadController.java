package com.concept.controller;

import com.concept.service.EntryFileService;
import com.concept.service.ServiceResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final EntryFileService entryFileService;

    public FileUploadController(EntryFileService entryFileService) {
        this.entryFileService = entryFileService;
    }

    @PostMapping("/upload-entry-file")
    ResponseEntity<?> uploadEntryFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

        final ServiceResponse response = entryFileService.handle(request.getRemoteAddr(), request.getRequestURL().toString(), file);

        if(response.outcomeFile().isPresent()) {
            final Path outcomeFile = response.outcomeFile().get();
            return ResponseEntity
                    .status(response.status())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=OutcomeFile.json")
                    .contentLength(Files.size(outcomeFile))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new InputStreamResource(Files.newInputStream(outcomeFile)));
        }

        return ResponseEntity.status(response.status()).body(response.message().orElse(""));
    }
}
