package com.baidu.sjws.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import sun.security.pkcs.PKCS7;

import java.io.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtils {

    /**
     * Desc：从zip文件中抽取指定的文件，并存放到指定目录
     * @param zipFileName 待解析的zip文件
     * @param extractFileName 需要抽取的文件名称
     * @param outputFolder 从zip中抽取出的文件存放路径
     * @throws java.io.IOException
     */
    public static String extractFileFromZip(String zipFileName, String extractFileName, String outputFolder) throws IOException {
        OutputStream outputStream = null;
        String extratedFileName = null;
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
                if (!entry.isDirectory() && entry.getName().endsWith(extractFileName)) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    String filePath = entry.getName();
                    String fileName;
                    if (filePath.contains("\\")) {
                        fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
                    } else {
                        fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                    }
                    if (!outputFolder.endsWith(File.separator)) {
                        outputFolder += File.separator;
                    }
                    extratedFileName = outputFolder + fileName;
                    File outFile = new File(extratedFileName);
                    outputStream = new FileOutputStream(outFile);
                    int cha = 0;
                    while ((cha = inputStream.read()) != -1) {
                        outputStream.write(cha);
                    }
                    IOUtils.closeQuietly(outputStream);
                    break;
                }
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return extratedFileName;
    }

    private static char[] toChars(byte[] mSignature) {
        byte[] sig = mSignature;
        final int N = sig.length;
        final int N2 = N * 2;
        char[] text = new char[N2];
        for (int j = 0; j < N; j++) {
            byte v = sig[j];
            int d = (v >> 4) & 0xf;
            text[j * 2] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
            d = v & 0xf;
            text[j * 2 + 1] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return text;
    }

    private static String createSignInt(String md5) {
        if (md5 == null || md5.length() < 32)
            return "-1";
        String sign = md5.substring(8, 8 + 16);
        long id1 = 0;
        long id2 = 0;
        String s = "";
        for (int i = 0; i < 8; i++) {
            id2 *= 16;
            s = sign.substring(i, i + 1);
            id2 += Integer.parseInt(s, 16);
        }
        for (int i = 8; i < sign.length(); i++) {
            id1 *= 16;
            s = sign.substring(i, i + 1);
            id1 += Integer.parseInt(s, 16);
        }
        long id = (id1 + id2) & 0xFFFFFFFFL;
        return String.valueOf(id);
    }

    public static Map<String, String> getMd5FromRSAFile(String rsaFile) throws IOException, CertificateEncodingException {
        Map<String, String> md5Map = new HashMap<String, String>(2);

        FileInputStream fileInputStream = new FileInputStream(rsaFile);
        PKCS7 pkcs7 = new PKCS7(fileInputStream);
        X509Certificate x509Certificate = pkcs7.getCertificates()[0];

        String charSig = new String(toChars(x509Certificate.getEncoded()));
        String md5 = DigestUtils.md5Hex(charSig.getBytes());
        md5Map.put("md5", md5);
        md5Map.put("signMd5", createSignInt(md5));

        //close file stream and delete template rsa file
        fileInputStream.close();
        File file = new File(rsaFile);
        file.delete();
        return md5Map;
    }

    public static void main(String[] args) throws IOException {
        String file = "/Users/huangqingwei/Downloads/HiMarket/HiMarket.apk";
        String outputFolder = "/Users/huangqingwei/";
        String extractFileName = "res/drawable-hdpi-v4/icon.png";
        extractFileFromZip(file, extractFileName, outputFolder);
        extractFileFromZip(file, ".RSA", outputFolder);
    }
}