package com.peeerr.climbing.dto.file.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class FileUploadRequest {

    @NotNull
    private Long postId;
    private List<MultipartFile> files;

    public static FileUploadRequest of(Long postId, List<MultipartFile> files) {
        return new FileUploadRequest(postId, files);
    }

}
