package com.example.dex2jar_file.Bean;

import org.springframework.web.multipart.MultipartFile;

public class OutBean {
    private MultipartFile file;
    private String packageName;

    public MultipartFile getFile() {
        return file;
    }

    public String getPackageName() {
        return packageName;
    }
}
