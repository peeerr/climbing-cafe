package com.peeerr.climbing.dto.file.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FileUploadRequest {

    @NotNull
    private Long postId;
    private List<MultipartFile> files;

}
