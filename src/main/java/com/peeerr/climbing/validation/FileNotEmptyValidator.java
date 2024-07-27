package com.peeerr.climbing.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        log.info(">>> 파일 유효성 검사 시작");
        log.info(">>> 결과 {}", !(files == null || files.isEmpty() || files.stream().anyMatch(file -> file == null || file.isEmpty())));
        return !(files == null || files.isEmpty() || files.stream().anyMatch(file -> file == null || file.isEmpty()));
    }

}
