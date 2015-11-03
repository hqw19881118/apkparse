package com.baidu.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    /**
     * Desc：从zip文件中抽取指定的文件，并存放到指定目录
     * @param zipFileName 待解析的zip文件
     * @param extractFileName 需要抽取的文件名称
     * @param outputFolder 从zip中抽取出的文件存放路径
     * @throws IOException
     */
    public static void extractFileFromZip(String zipFileName, String extractFileName, String outputFolder) throws IOException {
        OutputStream outputStream = null;
        try {
            File outFolder = new File(outputFolder);
            if(outFolder.exists() && outFolder.isFile()){
                throw new IllegalArgumentException("Not an exists folder");
            }
            //create output directory is not exists
            if(!outFolder.exists() && !outFolder.mkdir()){
                throw new IOException("fail to create dest folder");
            }
            if (extractFileName == null || "".equals(extractFileName)){
                throw new IllegalArgumentException("please define which file to extract, current is empty!");
            }

            ZipFile zipFile = new ZipFile(zipFileName);
            Enumeration emu = zipFile.entries();
            while(emu.hasMoreElements()){
                ZipEntry entry = (ZipEntry)emu.nextElement();
                if (!entry.isDirectory() && extractFileName.equalsIgnoreCase(entry.getName())) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    String filePath = entry.getName();
                    String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1);
                    if (!outputFolder.endsWith(File.separator)) {
                        outputFolder += File.separator;
                    }
                    File outFile = new File(outputFolder + fileName);
                    outputStream = new FileOutputStream(outFile);
                    int cha = 0;
                    while ((cha = inputStream.read()) != -1) {
                        outputStream.write(cha);
                    }
                    IOUtils.closeQuietly(outputStream);
                }
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static void main(String[] args) throws IOException {
        String file = "/Users/huangqingwei/Downloads/HiMarket/HiMarket.apk";
        String outputFolder = "/Users/huangqingwei/";
        String extractFileName = "res/drawable-hdpi-v4/icon.png";
        extractFileFromZip(file, extractFileName, outputFolder);
    }
}