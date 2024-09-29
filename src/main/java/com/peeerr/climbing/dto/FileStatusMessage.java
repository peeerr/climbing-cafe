package com.peeerr.climbing.dto;

import com.peeerr.climbing.constant.FileUploadState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileStatusMessage {

    private String fileId;
    private FileUploadState status;

}
