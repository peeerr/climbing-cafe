package com.peeerr.climbing.service;

import com.peeerr.climbing.exception.ErrorMessage;
import com.peeerr.climbing.domain.File;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.FileStoreDto;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class FileService {

    private final S3FileUploader s3FileUploader;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<String> getFilesByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.POST_NOT_FOUND));

        List<String> filenames = post.getFiles().stream()
                .map(File::getFilename)
                .toList();

        return s3FileUploader.getFiles(filenames);
    }

    public void uploadFiles(Long loginId, Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.POST_NOT_FOUND));

        checkOwner(loginId, post.getMember().getId());

        List<FileStoreDto> storedFiles = s3FileUploader.uploadFiles(files);

        List<File> fileEntities = storedFiles.stream()
                .map(fileDto -> File.builder()
                        .post(post)
                        .originalFilename(fileDto.getOriginalFilename())
                        .filename(fileDto.getFilename())
                        .filePath(fileDto.getFilePath())
                        .build())
                .toList();

        fileRepository.saveAll(fileEntities);
    }

    public void updateDeleteFlag(Long loginId, Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.FILE_NOT_FOUND));

        checkOwner(loginId, file.getPost().getMember().getId());

        if (file.isDeleted()) {
            throw new ClimbingException(ErrorMessage.FILE_NOT_FOUND);
        }

        file.changeDeleted(true);
    }

    private void checkOwner(Long loginId, Long ownerId) {
        if (!loginId.equals(ownerId)) {
            throw new ClimbingException(ErrorMessage.ACCESS_DENIED);
        }
    }

}
