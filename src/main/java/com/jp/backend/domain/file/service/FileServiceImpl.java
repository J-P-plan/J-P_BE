package com.jp.backend.domain.file.service;

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final Uploader uploader;
    private final S3Uploader s3Uploader;
    private final JpaFileRepository jpaFileRepository;
    private final UserService userService;

    // 유저 프로필 사진 업로드
    @Override
    @Transactional
    public String uploadProfile(MultipartFile file, String email) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }

        User user = userService.verifyUser(email);

        String[] info = uploadImage(file); // 이미지 업로드 하고 버킷 이름이랑 url 받음
        File fileEntity = File.builder()
                .bucket(info[1])
                .url(info[0])
                .fileType(File.FileType.IMAGE)
                .user(user)
                .build();

        if (user.getProfile() != null) {
            jpaFileRepository.delete(user.getProfile());

            // S3에서도 파일 교체
            String oldFileUrl = user.getProfile().getUrl();
            String oldFileName = oldFileUrl.substring(oldFileUrl.lastIndexOf("/") + 1);
            s3Uploader.updateFile(file, oldFileName);
        }

        jpaFileRepository.save(fileEntity);
        user.setProfile(fileEntity);
        return fileEntity.getUrl();
    }

    private String[] uploadImage(MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }
        return uploader.upload(file);
    }

    @Override
    public void deleteProfile(String email) {
        User user = userService.verifyUser(email);

        if (user.getProfile() != null) {
            user.setProfile(null);

            String fileUrl = user.getProfile().getUrl();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 최종 슬래시 이후의 문자열이 파일 이름
            s3Uploader.deleteFile(fileName); // S3에서 파일 삭제
        }
    }

    // 하나의 파일 업로드 / 업로드 후 해당 파일 url 반환
    // TODO 그런데 어차피 다중까지 하려면 아예 처음부터 list로 받아서 해야하니까
    //   하나의 파일 업로드만이 아니라 그냥 아예 list로 받아서 하나든 다중이든 하나의 메서드로 처리해야할 듯
    //   그냥 아예 이 기회에 파일 다중 업로드 메서드 만들면서 리뷰에 파일 올리는 거까지 한꺼번에 하자
    @Override
    @Transactional
    public String uploadFile(MultipartFile file, String email) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }

        User user = userService.verifyUser(email);

        File.FileType fileType = determineFileType(file.getContentType());
        String[] info = uploader.upload(file);

        File fileEntity = File.builder()
                .bucket(info[1])
                .url(info[0])
                .fileType(fileType)
                .user(user)
                .build();

        // 파일 정보 저장
        jpaFileRepository.save(fileEntity);
        // Todo reviewFile 저장 어케할지

        return fileEntity.getUrl();
    }

    // 다중 파일 업로드 / 업로드 후 파일 url list 반환
    // TODO 그런데 for문으로 하나씩 업로드 말고 한번에 업로드 하는 방법은 없을까
    @Override
    @Transactional
    public List<String> uploadFiles(List<MultipartFile> files, String email) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = uploadFile(file, email);
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    private File.FileType determineFileType(String contentType) {
        if (contentType != null) {
            if (contentType.startsWith("image")) {
                return File.FileType.IMAGE;
            } else if (contentType.startsWith("video")) {
                return File.FileType.VIDEO;
            } else if (contentType.equals("application/pdf")) {
                return File.FileType.PDF;
            }
        }
        throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
    }
}
