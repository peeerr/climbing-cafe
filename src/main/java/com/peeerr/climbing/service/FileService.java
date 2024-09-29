package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.File;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.FileStoreDto;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FileService {

    private final S3FileUploader s3FileUploader;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<String> getFilesByPostId(Long postId) {
        Post post = getPostById(postId);

        List<String> filenames = post.getFileNames();

        return s3FileUploader.getFiles(filenames);
    }

    public void uploadFile(Long loginId, Long postId, String fileName, byte[] fileData) {
        Post post = getPostById(postId);
        post.checkOwner(loginId);

        FileStoreDto storedFile = s3FileUploader.uploadFile(fileName, fileData);

        File fileEntity = File.builder()
                .post(post)
                .originalFilename(fileName)
                .filename(storedFile.getFilename())
                .filePath(storedFile.getFilePath())
                .build();

        fileRepository.save(fileEntity);
    }

    public void updateDeleteFlag(Long loginId, Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.FILE_NOT_FOUND));
        file.getPost().checkOwner(loginId);

        file.delete();
    }

}
