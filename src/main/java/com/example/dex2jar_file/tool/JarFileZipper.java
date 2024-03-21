package com.example.dex2jar_file.tool;

import com.example.dex2jar_file.Bean.MultipartConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarFileZipper {
    public static void zipFileByType(String directoryPath
            ,String fileType,String zipFilePath){
        List<File> matchingFile=getFilesByType(new File(directoryPath),fileType);
        if (!matchingFile.isEmpty()){
            byte[] buffer=new byte[1024];
            try {
                ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(zipFilePath));
                for (File file:matchingFile){
                    if (file==null || !file.exists()){
                        MultipartConfig.log.info("file is null file="+file.getPath());
                        continue;
                    }
                    FileInputStream fileInputStream=new FileInputStream(file);
                    ZipEntry entry=new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(entry);
                    int length;
                    while ((length=fileInputStream.read(buffer))!=-1){
                        zipOutputStream.write(buffer,0,length);

                    }
                    fileInputStream.close();
                }
                zipOutputStream.finish();

                zipOutputStream.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static List<File> getFilesByType(File directory,String fileType){
        List<File> matchingFiles=new ArrayList<>();

        if (directory.exists()&&directory.isDirectory()){
            File[] files=directory.listFiles();
            if (files!=null){
                for (File file:files){
                    if (file.getName().endsWith(fileType)){
                        matchingFiles.add(file);
                    }
                }
            }
        }
        return matchingFiles;
    }
}
