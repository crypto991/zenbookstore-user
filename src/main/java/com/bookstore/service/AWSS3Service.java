package com.bookstore.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

public interface AWSS3Service {

    void uploadFile(MultipartFile multipartFile, Long id);

    public ByteArrayOutputStream downloadFile(String keyName);


}
