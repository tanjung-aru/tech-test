package com.concept.io.input;

import com.concept.utils.FileUtils;
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
public class FileHandler {

    private final Boolean saveFile;
    private final String uploadDirectory;
    private final BiFunction<String, String, String> filenameFormatter;
    private final FileUtils fileUtils;
    private final FileProcessor fileProcessor;

    public FileHandler(@Value("${files.upload.save-file:true}") Boolean saveFile,
                       @Value("${files.upload.directory:files/upload}") String uploadDirectory,
                       BiFunction<String, String, String> filenameFormatter,
                       FileUtils fileUtils,
                       FileProcessor fileProcessor) {
        this.saveFile = saveFile;
        this.uploadDirectory = uploadDirectory;
        this.filenameFormatter = filenameFormatter;
        this.fileUtils = fileUtils;
        this.fileProcessor = fileProcessor;
    }

    public Path handle(MultipartFile multipartFile, String requestId) throws IOException {
        final InputStream inputStream;
        if(saveFile) {
            final Path path = Paths.get(uploadDirectory, filenameFormatter.apply(requestId, ".txt"));
            fileUtils.saveFromInputStream(multipartFile.getInputStream(), path);
            inputStream = Files.newInputStream(path);
        } else {
            inputStream = multipartFile.getInputStream();
        }
        return fileProcessor.process(inputStream, requestId);
    }
}
