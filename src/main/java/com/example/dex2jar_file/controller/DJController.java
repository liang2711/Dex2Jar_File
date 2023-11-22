package com.example.dex2jar_file.controller;

import com.example.dex2jar_file.Bean.MultipartConfig;
import com.example.dex2jar_file.tool.DexToJava;
import com.example.dex2jar_file.tool.IDexToJava;
import com.example.dex2jar_file.tool.JarFileZipper;
import org.apache.tomcat.Jar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/dex2java")
public class DJController {
    private static final String JARFILE_DIRECTORY="D:\\develop\\idea\\Dex2Jar_File\\src\\main\\resources\\convert_file\\";

    @RequestMapping("/test")
    public String hellWord(){
        return "Hello Word !";
    }

    @RequestMapping(value = "/outClassToJava",method = RequestMethod.POST)
    public String outClassToJava(@RequestParam("file")MultipartFile file){
        MultipartConfig.log.info("outClassToJava-----------------");

        IDexToJava iDexToJava=new DexToJava();

        String fileName="resourcess";
        String resqones="the fail";
        if (file==null){
            MultipartConfig.log.info("file is null");
            return "code:500";
        }
        File isDirectory=new File(JARFILE_DIRECTORY);
        if (!isDirectory.exists()){
            isDirectory.mkdir();
        }
        File classFile=new File(JARFILE_DIRECTORY +fileName+".class");
        try {
            file.transferTo(classFile);
            resqones=iDexToJava.evecuteJad(JARFILE_DIRECTORY,fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resqones;
    }

    @RequestMapping(value = "/outDex",method = RequestMethod.POST)
    public int outDex(@RequestParam("file") MultipartFile file,@RequestParam("packageName") String packageName){
        MultipartConfig.log.info("DJController------------------------");
        String directoryPath=null;
        String filePath=null;
        int codestate=0;

        if (file==null){
            MultipartConfig.log.info("file is null");
            return 1;
        }
        directoryPath=JARFILE_DIRECTORY+packageName+"\\";
        File isDirFile=new File(directoryPath);
        if (!isDirFile.exists()){
            isDirFile.mkdir();
        }
        IDexToJava dexToJava=new DexToJava(directoryPath);

        filePath=directoryPath+file.getOriginalFilename();
        File destFile=new File(filePath);
        try {
            file.transferTo(destFile);
            codestate=dexToJava.evecuteCmdBuilder(file.getOriginalFilename(),packageName);
            MultipartConfig.log.info("packageName:"+packageName);
        } catch (IOException e) {
            return 1;
        }
        return codestate;
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> inJava(@RequestParam String fileName,
                           @RequestParam String filePackageName){
        MultipartConfig.log.info("DJController------------------------");
        Path jarFile=Paths.get(JARFILE_DIRECTORY+filePackageName).resolve(fileName+".jar");
        try {
            Resource resource=new UrlResource(jarFile.toUri());
            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+resource.getFilename()+"\"")
                        .body(resource);
            }else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/dlzip")
    public ResponseEntity<Resource> inJavaZip(@RequestParam String filePackageName){
        JarFileZipper.zipFileByType(JARFILE_DIRECTORY+filePackageName,".jar",JARFILE_DIRECTORY+filePackageName+"\\JarFile.zip");
        Path path=Paths.get(JARFILE_DIRECTORY+filePackageName+"\\JarFile.zip");
        ResponseEntity<Resource> response;
        try {
            Resource resource=new UrlResource(path.toUri());
            if (resource.exists()){
                response=ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+resource.getFilename()+"\"")
                        .body(resource);
                return response;
            }else
                return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
