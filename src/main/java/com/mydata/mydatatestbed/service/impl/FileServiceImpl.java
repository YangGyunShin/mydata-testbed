package com.mydata.mydatatestbed.service.impl;

import com.mydata.mydatatestbed.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 파일 저장
     * 
     * 흐름:
     * 1. 원본 파일명에서 확장자 추출
     * 2. UUID로 저장용 파일명 생성 (중복 방지)
     * 3. 업로드 디렉토리 절대 경로 생성
     * 4. 디렉토리 없으면 생성
     * 5. Files.copy()로 파일 저장 (transferTo보다 안전)
     * 6. 저장 결과 반환
     * 
     * 주의사항:
     * - transferTo()는 상대 경로를 Tomcat 임시 디렉토리 기준으로 해석할 수 있음
     * - Files.copy()는 절대 경로를 명확하게 사용하므로 더 안전함
     */
    @Override
    public FileInfo saveFile(MultipartFile file, String subDir) {
        try {
            // 1. 원본 파일명 추출 (예: "report.pdf")
            String originalFilename = file.getOriginalFilename();

            // 2. 확장자 추출 (예: ".pdf")
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 3. 저장용 파일명 생성 (UUID로 중복 방지)
            //    예: "550e8400-e29b-41d4-a716-446655440000.pdf"
            String savedFilename = UUID.randomUUID() + extension;

            // 4. 업로드 경로를 절대 경로로 변환
            //    - Paths.get(uploadDir).toAbsolutePath(): 상대 경로를 절대 경로로 변환
            //    - normalize(): ".." 같은 경로 정규화
            Path uploadPath = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();

            // 5. 디렉토리가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 6. 최종 파일 경로 (예: "/Users/.../uploads/board/550e8400-....pdf")
            Path filePath = uploadPath.resolve(savedFilename);

            // 7. Files.copy()로 파일 저장 (transferTo보다 안전)
            //    - REPLACE_EXISTING: 같은 이름 파일이 있으면 덮어쓰기 (UUID라 거의 없음)
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 8. 저장 결과 반환 (경로, 원본명, 크기)
            return new FileInfo(filePath.toString(), originalFilename, file.getSize());

        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 파일 삭제 실패는 무시 (이미 없거나 권한 문제)
        }
    }
}