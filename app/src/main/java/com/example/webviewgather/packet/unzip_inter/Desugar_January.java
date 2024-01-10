package com.example.webviewgather.packet.unzip_inter;

import android.text.TextUtils;


import com.example.utilsgather.logcat.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Desugar_January {

    /**
     * 原生的解压方式
     * @param source 源压缩文件的路径
     * @param target 解压的目标路径
     * @param deleteSource 解压成功后，是否删除源文件
     */
    public static void decompressFile(String source, String target, boolean deleteSource) throws IOException {
        if(TextUtils.isEmpty(target)){
            return;
        }

        File file = new File(source);
        if(!file.exists()) {
            LogUtil.d("The original compressed file does not exist");
            return;
        }
        ZipFile zipFile = new ZipFile(file);

        LogUtil.d("Total size of the compressed file：" + file.length());

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));

        ZipEntry zipEntry = null;

        long unZipByte = 0;

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String fileName = zipEntry.getName();
            LogUtil.d("The name of the decompressed file：" + fileName);

            File temp = new File(target + File.separator + fileName);
            if(zipEntry.isDirectory()) {
                File dir = new File(target + File.separator + fileName);
                dir.mkdirs();
                continue;
            }
            if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
                temp.getParentFile().mkdirs();
            }
            byte[] buffer = new byte[1024];
            OutputStream os = new FileOutputStream(temp);
            // 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
            InputStream is = zipFile.getInputStream(zipEntry);
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);

                unZipByte += len;
                LogUtil.d("Get the decompression size in real time：" + unZipByte);
            }
            os.close();
            is.close();
        }
        zipInputStream.close();

        if (deleteSource && file.exists()) {
            file.delete();
            LogUtil.d("The source file has been deleted");
        }

    }


}
