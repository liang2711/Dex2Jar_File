package com.example.dex2jar_file.tool;

import ch.qos.logback.core.model.processor.ProcessingPhase;
import com.example.dex2jar_file.Bean.MultipartConfig;

import java.io.*;

public class DexToJava implements IDexToJava{
    private String dexFilePath="D:\\develop\\idea\\Dex2Jar_File\\src\\main\\resources\\convert_file";

    public DexToJava(String dexFilePath){
        this.dexFilePath=dexFilePath;
    }
    public DexToJava(){}
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

    @Override
    public String evecuteJad(String filePath,String fileName) {
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

            return stringBuilder.toString();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
        return "class to java fail";
    }

}
