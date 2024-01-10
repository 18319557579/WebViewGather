package com.example.webviewgather.packet.other

import android.net.Uri
import java.io.File
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern

object ImplementFile {
    /**
     * 生成文件的临时文件名称
     */
    @JvmStatic
    fun getTempFile(url: String, filePath: String): File {
        val parentFile = File(filePath).parentFile
        val md5 = stringToMD5(url)
        return File(parentFile.absolutePath, "$md5.temp")
    }

    private fun stringToMD5(string: String): String? {
        val hash: ByteArray = try {
            MessageDigest.getInstance("MD5").digest(string.toByteArray(charset("UTF-8")))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b.toInt() and 0xFF < 0x10) hex.append("0")
            hex.append(Integer.toHexString(b.toInt() and 0xFF))
        }
        return hex.toString()
    }

    /**
     * 格式化大小
     */
    @JvmStatic
    fun byteFormat(bytes: Long): String {
        val fileSize = BigDecimal(bytes)
        val megabyte = BigDecimal(1024 * 1024)
        var returnValue = fileSize.divide(megabyte, 2, BigDecimal.ROUND_UP).toFloat()
        if (returnValue > 1) {
            return returnValue.toString() + "MB"
        }
        val kilobyte = BigDecimal(1024)
        returnValue = fileSize.divide(kilobyte, 2, BigDecimal.ROUND_UP).toFloat()
        return returnValue.toString() + "KB"
    }

    /**
     * 获取url最后的文件名
     */
    @JvmStatic
    fun getName(url: String): String? {
        val uri = Uri.parse(url)
        return uri.lastPathSegment
    }

    /**
     * 去掉文件名的后缀，获得文件名
     */
    @JvmStatic
    fun getCleanName(wholeFileName: String): String {
        return Pattern.compile("[.][^.]*$").matcher(wholeFileName).replaceFirst("")
    }
}