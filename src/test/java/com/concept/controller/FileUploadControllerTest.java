package com.concept.controller;

import com.concept.service.EntryFileService;
import com.concept.service.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntryFileService entryFileService;

    @Test
    void returns_OK_and_file_contents() throws Exception {

        String fileContents = "<file contents>";
        when(entryFileService.handle(anyString(), any(), any()))
                .thenReturn(new ServiceResponse(HttpStatus.OK, createTempFile(fileContents)));

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt",
                "text/plain", "foobar".getBytes());

        mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(fileContents));
    }

    @Test
    void returns_FORBIDDEN_and_expected_message() throws Exception {

        String message = "Blocked Country";
        when(entryFileService.handle(anyString(), any(), any()))
                .thenReturn(new ServiceResponse(HttpStatus.FORBIDDEN, message));

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt",
                "text/plain", "foobar".getBytes());

        mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isForbidden())
                .andExpect(content().string(message));
    }

    private static Path createTempFile(String fileContents) throws IOException {
        Path path = Files.createTempFile("my-test-", ".txt");
        Files.writeString(path, fileContents);
        return path;
    }
}