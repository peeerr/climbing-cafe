package com.peeerr.climbing.service;

import com.peeerr.climbing.entity.File;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.entity.Post;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.dto.file.FileStoreDto;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.exception.EntityNotFoundException;
import com.peeerr.climbing.exception.FileAlreadyDeletedException;
import com.peeerr.climbing.exception.UnauthorizedAccessException;
import com.peeerr.climbing.util.S3FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

    private final S3FileUploader s3FileUploader;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Transactional
    public List<String> getFilesByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        List<String> filenames = new ArrayList<>();
        for (File file : post.getFiles()) {
            filenames.add(file.getFilename());
        }

        return s3FileUploader.getFiles(filenames);
    }

    @Transactional
    public void uploadFiles(Long loginId, Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        List<FileStoreDto> storedFiles = s3FileUploader.uploadFiles(files);

        List<File> fileEntities = new ArrayList<>();
        for (FileStoreDto fileDto : storedFiles) {
            File fileEntity = File.builder()
                    .post(post)
                    .originalFilename(fileDto.getOriginalFilename())
                    .filename(fileDto.getFilename())
                    .filePath(fileDto.getFilePath())
                    .build();

            fileEntities.add(fileEntity);
        }

        fileRepository.saveAll(fileEntities);
    }

    @Transactional
    public void updateDeleteFlag(Long loginId, Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.FILE_NOT_FOUND));

        if (!file.getPost().getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        if (file.isDeleted()) {
            throw new FileAlreadyDeletedException(ErrorMessage.FILE_ALREADY_DELETED);
        }

        file.changeDeleted(true);
    }

}
