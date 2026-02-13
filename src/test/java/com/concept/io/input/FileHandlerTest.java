package com.concept.io.input;

import com.concept.utils.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

    private static final String REQUEST_ID = "REQUEST-FOO123";

    @Mock
    private BiFunction<String, String, String> filenameFormatter;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private FileProcessor fileProcessor;

    @Mock
    private MultipartFile multipartFile;

    @TempDir
    Path tempDir;

    @Test
    void assert_that_file_is_saved_when_save_file_enabled() throws Exception {
        Path tempFile = Files.createTempFile(tempDir, REQUEST_ID, ".txt");
        when(filenameFormatter.apply(any(), any())).thenReturn(tempFile.getFileName().toString());
        doNothing().when(fileUtils).saveFromInputStream(any(), any());
        when(fileProcessor.process(any(), anyString())).thenReturn(tempFile);
        FileHandler fileHandler = new FileHandler(true, tempDir.toString(), filenameFormatter, fileUtils, fileProcessor);

        Path path = fileHandler.handle(multipartFile, REQUEST_ID);

        Assertions.assertThat(path).isEqualTo(tempFile);
        verify(fileUtils, times(1)).saveFromInputStream(any(), eq(path));
        verify(fileProcessor, times(1)).process(any(), eq(REQUEST_ID));
    }

    @Test
    void assert_that_file_is_not_saved_when_save_file_disabled() throws Exception {
        when(fileProcessor.process(any(), anyString())).thenReturn(tempDir);
        FileHandler fileHandler = new FileHandler(false, tempDir.toString(), filenameFormatter, fileUtils, fileProcessor);

        Path path = fileHandler.handle(multipartFile, REQUEST_ID);

        Assertions.assertThat(path).isEqualTo(tempDir);
        verify(filenameFormatter, never()).apply(any(), anyString());
        verify(fileUtils, never()).saveFromInputStream(any(), eq(path));
        verify(fileProcessor, times(1)).process(any(), eq(REQUEST_ID));
    }
}