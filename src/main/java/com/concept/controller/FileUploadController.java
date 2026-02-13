package com.concept.controller;

import com.concept.service.EntryFileService;
import com.concept.service.ServiceResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

        // TODO: Client's actual IP Address could be masked, for example if a reverse proxy were used.
        //       Would need to inspect request headers, e.g. X-Forwarded-For
        final String ipAddress = request.getRemoteAddr();

        final ServiceResponse response = entryFileService.handle(ipAddress, file, request.getRequestURL().toString());

        if(response.outcomePath().isPresent()) {
            final Path path = response.outcomePath().get();
            final Resource resource = new InputStreamResource(Files.newInputStream(path));

            return ResponseEntity
                    .status(response.status())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=OutcomeFile.json")
                    .contentLength(Files.size(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);
        }

        return ResponseEntity.status(response.status()).body(response.message().orElse(""));
    }
}
