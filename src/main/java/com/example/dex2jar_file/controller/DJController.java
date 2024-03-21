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
import java.util.List;

@RestController
@RequestMapping("/dex2java")
public class DJController {
    public static final String JARFILE_DIRECTORY="D:\\develop\\idea\\Dex2Jar_File\\src\\main\\resources\\convert_file\\";

    @RequestMapping("/test")
    public String hellWord(){
        return "Hello Word !";
    }

    @RequestMapping(value = "/outClassToJava",method = RequestMethod.POST)
    public String outClassToJava(@RequestParam("file")MultipartFile file,@RequestParam String data){
        MultipartConfig.log.info("outClassToJava-----------------");

        IDexToJava iDexToJava=DexToJava.getInstance();
        String resqones="the fail";
        if (file==null || data==null){
            MultipartConfig.log.info("file is null");
            return "code:500";
        }
        MultipartConfig.log.info("file name :"+data);
        File isDirectory=new File(JARFILE_DIRECTORY);
        if (!isDirectory.exists()){
            isDirectory.mkdir();
        }
        String className=data.split(";")[0];
        String packageName=data.split(";")[1]+"\\";
        File classFile=new File(JARFILE_DIRECTORY+packageName+className+".class");
        try {
            file.transferTo(classFile);
            resqones=iDexToJava.evecuteJad(JARFILE_DIRECTORY+packageName,className);
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
        //本地应用资源路径
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
    //打包应用jar
    @GetMapping("/dlzip")
    public ResponseEntity<Resource> inJarZip(@RequestParam String filePackageName,@RequestParam String fileType){
        MultipartConfig.log.info("packageName:"+filePackageName+"  filetype:"+fileType);
        String fileName;
        if (fileType.equals(".jar")){
            fileName="JarFile.zip";
        }else if (fileType.equals(".java")){
            fileName="JavaFile.zip";
        }else {
            return ResponseEntity.notFound().build();
        }
        JarFileZipper.zipFileByType(JARFILE_DIRECTORY+filePackageName,fileType,JARFILE_DIRECTORY+filePackageName+"\\"+fileName);
        Path path=Paths.get(JARFILE_DIRECTORY+filePackageName+"\\"+fileName);
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

    @GetMapping("/all")
    public int all(@RequestParam String filePackageName){
        MultipartConfig.log.info("all      packageName:"+filePackageName);
        if (filePackageName==null)
            return 500;
        List<File> list=JarFileZipper.getFilesByType(new File(JARFILE_DIRECTORY+filePackageName),"jar");
        DexToJava.allC2J(list,filePackageName);
        return 200;
    }
}
