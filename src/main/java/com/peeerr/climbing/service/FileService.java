package com.peeerr.climbing.service;

import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.domain.file.File;
import com.peeerr.climbing.domain.file.FileRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.dto.file.FileStoreDto;
import com.peeerr.climbing.dto.file.request.FileUploadRequest;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.FileAlreadyDeletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

    private final StorageManagement storageManagement;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Transactional
    public List<String> uploadFiles(FileUploadRequest fileUploadRequest) {
        Post post = postRepository.findById(fileUploadRequest.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        List<FileStoreDto> files = storageManagement.storeFiles(fileUploadRequest.getFiles());

        List<File> fileEntities = new ArrayList<>();
        for (FileStoreDto file : files) {
            File fileEntity = File.builder()
                    .post(post)
                    .originalFilename(file.getOriginalFilename())
                    .filename(file.getFilename())
                    .filePath(file.getFilePath())
                    .build();

            fileEntities.add(fileEntity);
        }

        List<File> savedFiles = fileRepository.saveAll(fileEntities);

        List<String> filePaths = new ArrayList<>();
        for (File savedFile : savedFiles) {
            filePaths.add(savedFile.getFilePath());
        }

        return filePaths;
    }

    @Transactional
    public void updateDeleteFlag(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.FILE_NOT_FOUND));

        if (file.isDeleted()) {
            throw new FileAlreadyDeletedException(ErrorMessage.FILE_ALREADY_DELETED);
        }

        file.changeDeleted(true);
    }

}
