package com.peeerr.climbing.service;

import com.peeerr.climbing.entity.File;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.entity.Post;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.exception.EntityNotFoundException;
import com.peeerr.climbing.exception.UnauthorizedAccessException;
import com.peeerr.climbing.util.S3FileUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private S3FileUploader s3FileUploader;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FileService fileService;

    @DisplayName("게시물 ID에 해당하는 모든 파일 URL 을 조회한다.")
    @Test
    void getFilesByPostId() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;

        Post post = Post.builder()
                .member(Member.builder().id(memberId).build())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(s3FileUploader.getFiles(List.of())).willReturn(List.of());

        //when
        List<String> fileUrls = fileService.getFilesByPostId(postId);

        //then
        then(postRepository).should().findById(postId);
        then(s3FileUploader).should().getFiles(List.of());

        assertThat(fileUrls).isEmpty();
    }

    @DisplayName("여러 개의 파일을 받아 저장한다.")
    @Test
    void uploadFiles() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;
        List<MultipartFile> files = List.of();
        Post post = Post.builder()
                .member(Member.builder().id(memberId).build())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(s3FileUploader.uploadFiles(files)).willReturn(List.of());
        given(fileRepository.saveAll(List.of())).willReturn(List.of());

        Long loginId = memberId;

        //when
        fileService.uploadFiles(loginId, postId, files);

        //then
        then(postRepository).should().findById(postId);
        then(s3FileUploader).should().uploadFiles(files);
        then(fileRepository).should().saveAll(List.of());
    }

    @DisplayName("여러 개의 파일을 받아 저장하는데 파일과 관계가 맺어진 게시물이 없으면 예외를 던진다.")
    @Test
    void uploadFilesWithNonExistPostId() throws Exception {
        //given
        Long postId = 1L;
        List<MultipartFile> files = List.of();

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> fileService.uploadFiles(1L, postId, files));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("[파일 업로드 권한X] 게시물 작성자와 로그인 사용자가 일치하지 않으면 예외를 던진다.")
    @Test
    void uploadFilesWithNoPermission() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;
        List<MultipartFile> files = List.of();
        Post post = Post.builder()
                .member(Member.builder().id(memberId).build())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        Long loginId = 2L;

        //when & then
        assertThrows(UnauthorizedAccessException.class,
                () -> fileService.uploadFiles(loginId, postId, files));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("id에 해당하는 파일의 deleted 컬럼을 true 로 업데이트 한다. (유저 권한 기준 삭제)")
    @Test
    void updateDeleteFlag() throws Exception {
        //given
        Long fileId = 1L;
        Long memberId = 1L;

        Post post = Post.builder()
                .member(Member.builder().id(memberId).build())
                .build();
        File file = File.builder()
                .post(post)
                .deleted(false)
                .build();

        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        Long loginId = memberId;

        //when
        fileService.updateDeleteFlag(loginId, fileId);

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
                () -> fileService.updateDeleteFlag(1L, fileId));

        then(fileRepository).should().findById(fileId);
    }

    @DisplayName("[파일 업로드 권한X] 게시물 작성자와 로그인 사용자가 일치하지 않으면 예외를 던진다.")
    @Test
    void removePostWithNoPermission() throws Exception {
        //given
        Long fileId = 1L;
        Long memberId = 1L;

        Post post = Post.builder()
                .member(Member.builder().id(memberId).build())
                .build();
        File file = File.builder()
                .post(post)
                .deleted(false)
                .build();

        Long loginId = 2L;

        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        //when & then
        assertThrows(UnauthorizedAccessException.class,
                () -> fileService.updateDeleteFlag(loginId, fileId));

        then(fileRepository).should().findById(fileId);
    }

}
