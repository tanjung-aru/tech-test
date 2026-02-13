package com.concept.io.input;

import com.concept.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class MultipartFileHandler {

    private final BiFunction<String, String, String> filenameFormatter;
    private final FileUtils fileUtils;
    private final FileProcessor downStreamProcessor;

    @Value("${files.upload.save-file:true}")
    private Boolean saveFile;

    @Value("${files.upload.directory:files/upload}")
    private String uploadDirectory;

    public Path handle(MultipartFile multipartFile, String requestId) throws IOException {
        final InputStream inputStream;
        if(saveFile) {
            final Path path = Paths.get(uploadDirectory, filenameFormatter.apply(requestId, ".txt"));
            fileUtils.saveFromInputStream(multipartFile.getInputStream(), path);
            inputStream = Files.newInputStream(path);
        } else {
            inputStream = multipartFile.getInputStream();
        }
        return downStreamProcessor.processInputStream(inputStream, requestId);
    }
}
