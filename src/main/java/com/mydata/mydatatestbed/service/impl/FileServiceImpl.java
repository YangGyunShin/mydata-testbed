package com.mydata.mydatatestbed.service.impl;

import com.mydata.mydatatestbed.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public FileInfo saveFile(MultipartFile file, String subDir) {
        try {
            // 1. 원본 파일명 추출 (예: "report.pdf")
            String originalFilename = file.getOriginalFilename();

            // 2. 확장자 추출 (예: ".pdf")
            //    - 파일명에 "."이 있으면 마지막 "." 이후를 확장자로
            //    - 없으면 빈 문자열
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            // 3. 저장용 파일명 생성 (UUID로 중복 방지)
            //    예: "550e8400-e29b-41d4-a716-446655440000.pdf"
            String savedFilename = UUID.randomUUID() + extension;

            // 4. 업로드 경로 생성 (예: "./uploads/board")
            Path uploadPath = Paths.get(uploadDir, subDir);

            // 5. 디렉토리가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 6. 최종 파일 경로 (예: "./uploads/board/550e8400-....pdf")
            Path filePath = uploadPath.resolve(savedFilename);

            // 7. 실제 파일 저장
            file.transferTo(filePath.toFile());

            // 8. 저장 결과 반환 (경로, 원본명, 크기)
            return new FileInfo(filePath.toString(), originalFilename, file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ignored) {
        }
    }
}