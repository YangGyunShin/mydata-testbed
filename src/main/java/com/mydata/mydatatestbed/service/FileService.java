package com.mydata.mydatatestbed.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileInfo saveFile(MultipartFile file, String subDir);

    void deleteFile(String filePath);

    record FileInfo(String path, String originalName, Long size) {}
}