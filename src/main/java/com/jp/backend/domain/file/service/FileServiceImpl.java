package com.jp.backend.domain.file.service;

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.ReviewFile;
import com.jp.backend.domain.file.enums.FileTargetType;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaReviewFileRepository;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.repository.JpaReviewRepository;
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

import static com.jp.backend.domain.file.enums.FileTargetType.PROFILE;
import static com.jp.backend.domain.file.enums.FileTargetType.REVIEW;

@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    // TODO 여기 생성자가 S3UploaderConfig에서 다 들어가야하는데... 이거 일단 보류
    private final Uploader uploader;
    private final S3Uploader s3Uploader;
    private final JpaFileRepository jpaFileRepository;
    private final JpaReviewFileRepository reviewFileRepository;
    private final JpaReviewRepository reviewRepository;
    private final UserService userService;

    // 유저 프로필 사진 업로드
    @Override
    @Transactional
    public String uploadProfile(MultipartFile file, String email) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }

        User user = userService.verifyUser(email);

        String[] info = uploadProfileImage(file); // 이미지 업로드 하고 버킷 이름이랑 url 받음
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
            s3Uploader.updateFile(file, oldFileName, PROFILE);
        }

        jpaFileRepository.save(fileEntity);
        user.setProfile(fileEntity);
        return fileEntity.getUrl();
    }

    // 프로필 이미지 업로드
    private String[] uploadProfileImage(MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }
        return uploader.upload(file, PROFILE);
    }

    @Override
    public void deleteProfile(String email) {
        User user = userService.verifyUser(email);

        if (user.getProfile() != null) {
            user.setProfile(null);

            String fileUrl = user.getProfile().getUrl();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 최종 슬래시 이후의 문자열이 파일 이름
            s3Uploader.deleteFile(fileName); // S3에서 파일 삭제 //TODO 이 부분 삭제 안되는 거 해결
        }
    }

    // 하나의 파일 업로드 / 업로드 후 해당 파일 url 반환
    @Override
    @Transactional
    public String uploadFile(MultipartFile file, String email, Long targetId, FileTargetType fileTargetType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
        }
        // TODO response에 파일 경로 나오도록 했으니까 어디에 추가한건지 파일 정보 나오겠지..? 한번 확인

        User user = userService.verifyUser(email);

        File.FileType fileType = determineFileType(file.getContentType());
        String[] info = uploader.upload(file, fileTargetType);

        File fileEntity = File.builder()
                .bucket(info[1])
                .url(info[0])
                .fileType(fileType)
                .user(user)
                .build();

        // 파일 정보 저장
        jpaFileRepository.save(fileEntity);

        switch (fileTargetType) {
            case REVIEW: // 리뷰 파일
                Review review = reviewRepository.findById(targetId)
                        .orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));

                ReviewFile reviewFile = new ReviewFile();
                reviewFile.setFile(fileEntity);
                reviewFile.setReview(review);
                reviewFileRepository.save(reviewFile);
                break;
            // TODO 여행기 파일

            default:
                throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
        }

        return fileEntity.getUrl();
    }

    // 다중 파일 업로드 / 업로드 후 파일 url list 반환
    // TODO 그런데 for문으로 하나씩 업로드 말고 한번에 업로드 하는 방법은 없을까
    @Override
    @Transactional
    public List<String> uploadFiles(List<MultipartFile> files, String email, Long targetId, FileTargetType fileTargetType) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = uploadFile(file, email, targetId, fileTargetType);
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

    // 파일 경로 생성 메서드
    private String generateFilePath(FileTargetType fileTargetType, File.FileType fileType) {
        StringBuilder pathBuilder = new StringBuilder();

        switch (fileTargetType) {
            case PROFILE:
                pathBuilder.append("profile");
                break;
            case REVIEW:
                pathBuilder.append("review");
                break;
            case TRAVEL_DIARY:
                pathBuilder.append("travelDiary");
                break;
            default:
                throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
        }

        // 파일 타입에 따라 추가적인 경로를 설정
        if (fileType == File.FileType.IMAGE) {
            pathBuilder.append("/image");
        } else if (fileType == File.FileType.VIDEO) {
            pathBuilder.append("/video");
        } else if (fileType == File.FileType.PDF) {
            pathBuilder.append("/pdf");
        }

        return pathBuilder.toString();
    }
}
