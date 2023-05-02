package com.yan.dd_picture.service;

import com.yan.bbs.service.Impl.SuperService;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.SystemConfig;
import com.yan.dd_picture.entity.File;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件服务类
 *
 * @author 陌溪
 * @since 2018-09-17
 */
public interface FileService extends SuperService<File> {


    /**
     * 通过fileIds获取图片信息
     *
     * @param fileIds
     * @param code
     * @return
     */
    public String getPicture(String fileIds, String code);

    /**
     * 批量文件上传
     *
     * @param request
     * @param multipartFileList
     * @param systemConfig
     * @return
     */
    String batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList, SystemConfig systemConfig);


    /**
     * CKeditor图像中的图片上传
     *
     * @param request
     * @return
     */
    R ckeditorUploadFile(HttpServletRequest request);

    /**
     * CKeditor上传 复制的图片
     *
     * @return
     */
    Object ckeditorUploadCopyFile();

    /**
     * 工具栏 “插入\编辑超链接”的文件上传
     *
     * @return
     */
    Object ckeditorUploadToolFile(HttpServletRequest request);

}
