package com.tomasz.peopledbweb.biz.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Repository
@Slf4j
public class FileStorageRepository {
    @Value("${STORAGE_FOLDER}")
    private String storageFolder;
    public void save(String originalFilename, InputStream inputStream){
        Path filePath = Path.of(storageFolder).resolve(originalFilename).normalize();
        try {
            Files.copy(inputStream, filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Resource findByName(String name){
        Path filePath = Path.of(storageFolder).resolve(name).normalize();
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAllByName(Iterable<String> fileNames) {
        try {
        for(String filename: fileNames){
                Path filePath = Path.of(storageFolder).resolve(filename).normalize();
                log.info("Try to delete "+ filePath);
                if(!filename.equals("default.png")){
                    Files.deleteIfExists(filePath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
