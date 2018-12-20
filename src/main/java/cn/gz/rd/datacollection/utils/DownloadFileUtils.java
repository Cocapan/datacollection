package cn.gz.rd.datacollection.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文件下载工具类
 * @author Peng Xiaodong
 */
public class DownloadFileUtils {

    /**
     * 获取文件名称
     * @param userAgent http请求头，用于判断所使用的浏览器
     * @param fileName 原始的文件名称
     * @return 编码后的文件名称
     * @throws UnsupportedEncodingException
     */
    public static String getFileNameByExplore(String userAgent, String fileName)
            throws UnsupportedEncodingException {
        // IE内核的浏览器特殊处理
        if (StringUtils.containsAny(userAgent, "MSIE", "Trident")) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        return fileName;
    }
}
