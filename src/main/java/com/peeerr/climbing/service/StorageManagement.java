package com.peeerr.climbing.service;

import com.peeerr.climbing.dto.file.FileStoreDto;
import com.peeerr.climbing.exception.ex.DirectoryCreateException;
import com.peeerr.climbing.exception.ex.FileStoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class StorageManagement {

    @Value("${file.path}")
    private String baseFilePath;

    public List<FileStoreDto> storeFiles(List<MultipartFile> files) {
        List<FileStoreDto> storeFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storeFiles.add(storeFile(file));
            }
        }

        return storeFiles;
    }

    public FileStoreDto storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;
        String filePath = getFullFilePath(filename);

        Path storeDirectoryPath = getStoreDirectoryPath();

        try {
            if (!Files.exists(storeDirectoryPath)) {
                Files.createDirectories(storeDirectoryPath);
            }
        } catch (Exception e) {
            throw new DirectoryCreateException("파일을 저장할 디렉토리 생성에 실패했습니다.");
        }

        try {
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            throw new FileStoreException("파일 저장에 실패했습니다.");
        }

        return FileStoreDto.of(originalFilename, filename, filePath);
    }

    private String getFullFilePath(String filename) {
        return Paths.get(getStoreDirectoryPath().toString(), filename).toString();
    }

    private Path getStoreDirectoryPath() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String month = formatAsTwoDigits(LocalDateTime.now().getMonthValue());
        String day = formatAsTwoDigits(LocalDateTime.now().getDayOfMonth());

        return Paths.get(baseFilePath, year, month, day);
    }

    /**
     * 파라미터가 1~9면 앞에 0을 붙여 두자리로 만들고, 스트링 타입으로 리턴
     * @param num
     * @return
     */
    private String formatAsTwoDigits(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }

}
