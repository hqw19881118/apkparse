package com.baidu;

import com.baidu.rc.ApkFile;
import com.baidu.rc.PackageInfo;
import com.baidu.utils.ZipUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final ObjectMapper mapper;
    static{
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    private static void saveApkInfo(String srcApk, String apkInfoFileName) throws IOException {
        File file = new File(srcApk);
        ApkFile apkFile = new ApkFile(file);
        PackageInfo packageInfo = apkFile.parsePackage();
        Map<String, Object> pkgInfoMap = new HashMap<String, Object>();
        pkgInfoMap.put("icons", Arrays.asList(packageInfo.icons));
        pkgInfoMap.put("vn", packageInfo.versionName);
        pkgInfoMap.put("vc", packageInfo.versionCode);
        pkgInfoMap.put("pkg", packageInfo.name);
        pkgInfoMap.put("label", packageInfo.label);
        String jsonData = mapper.writeValueAsString(pkgInfoMap);
//        System.out.println(jsonData);

        // store apk info into file
        File outFile = new File(apkInfoFileName);
        OutputStream outputStream = new FileOutputStream(outFile);
        try {
            outputStream.write(jsonData.getBytes());
            IOUtils.closeQuietly(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length < 3) {
            showUsage();
            return;
        }
        String srcApk = args[0];
        String apkInfoFile = args[1];
        String apkInfoDir = args[2];

        // parse apk and get basic info
        if (!apkInfoDir.endsWith(File.separator)) {
            apkInfoDir += File.separator;
        }
        saveApkInfo(srcApk, apkInfoDir + apkInfoFile);
        // extract icon from apk if needed.
        if (args.length > 3) {
            String iconName = args[3];
            ZipUtils.extractFileFromZip(srcApk, iconName, apkInfoDir);
        }
    }

    public static void showUsage() {
        System.out.println("usage: apkparse.jar [srcApk] [apkInfoFile] [apkInfoDir] <iconName>");
    }
}
