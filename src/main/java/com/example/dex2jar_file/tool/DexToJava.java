package com.example.dex2jar_file.tool;

import ch.qos.logback.core.model.processor.ProcessingPhase;
import com.example.dex2jar_file.Bean.MultipartConfig;
import com.example.dex2jar_file.controller.DJController;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DexToJava implements IDexToJava{
    static ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private String dexFilePath="D:\\develop\\idea\\Dex2Jar_File\\src\\main\\resources\\convert_file";

    public DexToJava(String dexFilePath){
        this.dexFilePath=dexFilePath;
    }
    private DexToJava(){}

    public static DexToJava getInstance(){
        return new DexToJava();
    }

    /**
     * Convert the dex file to a ja file
     */
    @Override
    public int evecuteCmdBuilder(String dexFileName,String packageName) {
        //current path
        String currentfilePath=System.getProperty("user.dir")+ File.separator+"bin";


        String command="D:\\develop\\jdk\\decompile\\dex2jar-v2.3\\d2j -f "+dexFilePath+dexFileName+" -o "+dexFilePath+dexFileName+".jar";
        ProcessBuilder cmd=new ProcessBuilder("cmd.exe","/c",command);


        Process process= null;
        StringBuffer output=null;
        try {
            process = cmd.start();
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            output=new StringBuffer();
            String line;
            while((line=reader.readLine())!=null){
                output.append(line).append("\n");
            }
            int exitcode=process.waitFor();

            if (exitcode==1)
                return 1;

        } catch (Exception e) {
            MultipartConfig.log.info(output.toString());
            e.printStackTrace();
        }
        return 0;
    }
    /*
    * convert the class file to java file
    * */

    @Override
    public String evecuteJad(String filePath,String fileName) {
//        MultipartConfig.log.info("outClassToJava--------evecuteJad  "+filePath+"   "+filePath);
        if (filePath==null)
            return "class to java fail";
        File file=new File(filePath+fileName+".class");
        if (!file.exists()){
            MultipartConfig.log.info("resourcess is unexists");
            return "class to java fail";
        }
        String outputFileName=filePath+fileName+".java";
        String inputFileName=filePath+fileName+".class";

        try {
//            ProcessBuilder processBuilder=new ProcessBuilder(
//                    "D:\\develop\\jdk\\decompile\\jad\\jad","-o","-sjava",inputFileName);
//            Process process=processBuilder.start();
            String command="D:\\develop\\jdk\\decompile\\jad\\jad -o -p "+inputFileName+" > "+outputFileName;
            ProcessBuilder cmd=new ProcessBuilder("cmd.exe","/c",command);
            Process process=null;
            process=cmd.start();
            int exitCode=process.waitFor();
            if (exitCode!=0){
                MultipartConfig.log.info("evecute jad fail");
            }

            File outFile=new File(outputFileName);
            if (!outFile.exists())
                return "class to java fail";

            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(outFile)));
            String line;
            StringBuilder stringBuilder=new StringBuilder();
            while ((line=reader.readLine())!=null){
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
        return "class to java fail";
    }

    public static void allC2J(List<File> list,String packageName){
        if (list==null)
            return;
        int splitIndex=list.size()/2;

        c2jTool(list.subList(0,splitIndex),packageName);
        c2jTool(list.subList(splitIndex,list.size()),packageName);
    }

    private static void c2jTool(List<File> list,String packageName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String appPath=packageName.replace('.','/');
                try {
                    for (File file:list){
                        JarFile jarFile=new JarFile(file);
                        Enumeration<JarEntry> entries=jarFile.entries();
                        while (entries.hasMoreElements()){
                            JarEntry entry=entries.nextElement();
                            if (!entry.isDirectory()&&entry.getName().endsWith(".class")){
                                String className=entry.getName();
                                if (className.contains("$")||!className.endsWith(".class")||!className.contains("example"))
                                    continue;
                                MultipartConfig.log.info("className:"+className);
                                byte[] b=b2Stream(jarFile.getInputStream(entry));
                                String[] filenames=className.split("/");
                                c2j(b,filenames[filenames.length-1],packageName);
                            }
                        }
                    }
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
    private static void c2j(byte[] bytes,String className,String packageName){
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                OutputStream out;
                try {
                    out=new FileOutputStream(DJController.JARFILE_DIRECTORY+packageName+"\\"+className);
                    out.write(bytes);

                    DexToJava.getInstance().evecuteJad(DJController.JARFILE_DIRECTORY+packageName+"\\",className.split("\\.")[0]);

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public static byte[] b2Stream(InputStream jarInputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[] buffer=new byte[4096];
        int bytesRead;
        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
