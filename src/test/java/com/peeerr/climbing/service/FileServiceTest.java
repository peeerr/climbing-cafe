package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.file.File;
import com.peeerr.climbing.domain.file.FileRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.dto.file.request.FileUploadRequest;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private StorageManagement storageManagement;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FileService fileService;

    @DisplayName("여러 개의 파일을 받아 저장한다.")
    @Test
    void uploadFiles() throws Exception {
        //given
        Long postId = 1L;
        FileUploadRequest request = FileUploadRequest.of(postId, List.of());
        Post post = Post.builder().build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(storageManagement.storeFiles(request.getFiles())).willReturn(List.of());
        given(fileRepository.saveAll(List.of())).willReturn(List.of());

        //when
        fileService.uploadFiles(request);

        //then
        then(postRepository).should().findById(postId);
        then(storageManagement).should().storeFiles(request.getFiles());
        then(fileRepository).should().saveAll(List.of());
    }

    @DisplayName("여러 개의 파일을 받아 저장하는데 파일과 관계가 맺어진 게시물이 없으면 예외를 던진다.")
    @Test
    void uploadFilesWithNonExistPostId() throws Exception {
        //given
        Long postId = 1L;
        FileUploadRequest request = FileUploadRequest.of(postId, Collections.emptyList());

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> fileService.uploadFiles(request));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("id에 해당하는 파일의 deleted 컬럼을 true 로 업데이트 한다. (유저 권한 기준 삭제)")
    @Test
    void updateDeleteFlag() throws Exception {
        //given
        Long fileId = 1L;
        File file = File.builder()
                .deleted(false)
                .build();

        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        //when
        fileService.updateDeleteFlag(fileId);

        //then
        assertThat(file.isDeleted()).isEqualTo(true);

        then(fileRepository).should().findById(fileId);
    }

    @DisplayName("id를 받아 파일을 삭제하는데 해당하는 파일이 없으면 예외를 던진다. (유저 권한 기준 삭제)")
    @Test
    void removePostWithNonExistPostId() throws Exception {
        //given
        Long fileId = 1L;

        given(fileRepository.findById(fileId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> fileService.updateDeleteFlag(fileId));

        then(fileRepository).should().findById(fileId);
    }

}
