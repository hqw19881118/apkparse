package com.baidu.sjws;

import com.baidu.rc.ApkFile;
import com.baidu.rc.PackageInfo;
import com.baidu.sjws.utils.ApkUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final ObjectMapper mapper = new ObjectMapper();

    private static void saveApkInfo(String srcApk, String APKInfoDir, String apkInfoFile) throws IOException, CertificateEncodingException {
        File file = new File(srcApk);
        ApkFile apkFile = new ApkFile(file);
        PackageInfo packageInfo = apkFile.parsePackage();
        Map<String, Object> pkgInfoMap = new HashMap<String, Object>();
        pkgInfoMap.put("icons", Arrays.asList(packageInfo.icons));
        pkgInfoMap.put("vn", packageInfo.versionName);
        pkgInfoMap.put("vc", packageInfo.versionCode);
        pkgInfoMap.put("pkg", packageInfo.name);
        pkgInfoMap.put("label", packageInfo.label);

        //get md5 from rsa file
        String rsaFileName = ApkUtils.extractFileFromZip(srcApk, ".RSA", APKInfoDir);
        if (null == rsaFileName) {
            rsaFileName = ApkUtils.extractFileFromZip(srcApk, ".DSA", APKInfoDir);
        }
        Map<String, String> md5Map = ApkUtils.getMd5FromRSAFile(rsaFileName);
        pkgInfoMap.putAll(md5Map);

        String jsonData = mapper.writeValueAsString(pkgInfoMap);
        System.out.println(jsonData);

        // store apk info into file
        File outFile = new File(APKInfoDir + apkInfoFile);
        OutputStream outputStream = new FileOutputStream(outFile);
        try {
            outputStream.write(jsonData.getBytes());
            IOUtils.closeQuietly(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static void main(String[] args) throws IOException, CertificateEncodingException {
        if(args.length < 3) {
            showUsage();
            return;
        }
        String srcApk = args[0];
        String apkInfoFile = args[1];
        String apkInfoDir = args[2];
//        String srcApk = "D:\\downloads\\ruanjianxiazai_1.apk";
//        String apkInfoFile = "apk_info.txt";
//        String apkInfoDir = "D:\\downloads\\";

        // parse apk and get basic info
        if (!apkInfoDir.endsWith(File.separator)) {
            apkInfoDir += File.separator;
        }
        saveApkInfo(srcApk, apkInfoDir, apkInfoFile);
        // extract icon from apk if needed.
        if (args.length > 3) {
            String iconName = args[3];
            ApkUtils.extractFileFromZip(srcApk, iconName, apkInfoDir);
        }
    }

    public static void showUsage() {
        System.out.println("usage: apkparse.jar [srcApk] [apkInfoFile] [apkInfoDir] <iconName>");
    }
}
