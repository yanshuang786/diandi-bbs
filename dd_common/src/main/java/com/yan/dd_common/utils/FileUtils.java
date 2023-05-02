package com.yan.dd_common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author yanshuang
 * @date 2023/4/30 12:11
 */
@Slf4j
public class FileUtils {

    // 图片类型
    private static List<String> pictureTypes = new ArrayList<>();

    static {
        pictureTypes.add("jpg");
        pictureTypes.add("jpeg");
        pictureTypes.add("bmp");
        pictureTypes.add("gif");
        pictureTypes.add("png");
    }

    /**
     * 判断是否是图片
     *
     * @param fileName
     * @return
     */
    public static Boolean isPicture(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        String expandName = getPicExpandedName(fileName);
        return pictureTypes.contains(expandName);
    }

    /**
     * 判断是否为markdown文件
     *
     * @param fileName
     * @return
     */
    public static Boolean isMarkdown(String fileName) {
        return fileName.indexOf(".md") > -1;
    }


    /**
     * 获取后缀名
     *
     * @param fileName
     * @return
     */
    public static String getPicExpandedName(String fileName) {
        String ext = "";
        if (StringUtils.isNotBlank(fileName) &&
                StringUtils.contains(fileName, ".")) {
            ext = StringUtils.substring(fileName, fileName.lastIndexOf(".") + 1);
        }
        ext = ext.toLowerCase();
        if (ext == null || ext.length() < 1) {
            ext = "jpg";
        }

        return ext;
    }

    /**
     * 根据OriginalFilename获取FileName【去除文件后缀】
     * @param originalFilename
     * @return
     */
    public static String getFileName(String originalFilename) {
        String fileName = "";
        if (StringUtils.isNotBlank(originalFilename) &&
                StringUtils.contains(originalFilename, ".")) {
            fileName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 从Request中获取文件
     *
     * @return
     */
    public static List<MultipartFile> getMultipartFileList(HttpServletRequest request) {
        List<MultipartFile> files = new ArrayList<>();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                    request.getSession().getServletContext());
            if (request instanceof MultipartHttpServletRequest) {
                // 将request变成多部分request
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Iterator<String> iter = multiRequest.getFileNames();
                // 检查form中是否有enctype="multipart/form-data"
                if (multipartResolver.isMultipart(request) && iter.hasNext()) {
                    // 获取multiRequest 中所有的文件名
                    while (iter.hasNext()) {
                        List<MultipartFile> fileRows = multiRequest
                                .getFiles(iter.next().toString());
                        if (fileRows != null && fileRows.size() != 0) {
                            for (MultipartFile file : fileRows) {
                                if (file != null && !file.isEmpty()) {
                                    files.add(file);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("解析MultipartRequest错误", ex);
        }
        return files;
    }

    public static void main(String[] args) {
        System.out.println(pictureTypes.contains("png"));
    }
}
