package com.example.dex2jar_file.tool;

import java.io.File;
import java.io.InputStream;

public interface IDexToJava {
    int evecuteCmdBuilder(String fileName,String packageName);
    String evecuteJad(String filePath,String fileName);
}
