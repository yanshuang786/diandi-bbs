package com.yan.dd_picture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yan.bbs.service.Impl.SuperServiceImpl;
import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SQLConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.SystemConfig;
import com.yan.dd_common.enums.EFilePriority;
import com.yan.dd_common.enums.EOpenStatus;
import com.yan.dd_common.enums.EStatus;
import com.yan.dd_common.enums.RequestHolder;
import com.yan.dd_common.exception.InsertException;
import com.yan.dd_common.global.Constants;
import com.yan.dd_common.global.ErrorCode;
import com.yan.dd_common.utils.FileUtils;
import com.yan.dd_common.utils.JsonUtils;
import com.yan.dd_common.utils.ResultUtil;
import com.yan.dd_common.utils.StringUtils;
import com.yan.dd_picture.entity.File;
import com.yan.dd_picture.entity.FileSort;
import com.yan.dd_picture.mapper.FileMapper;
import com.yan.dd_picture.service.FileService;
import com.yan.dd_picture.service.FileSortService;
import com.yan.dd_picture.service.LocalFileService;
import com.yan.dd_picture.utils.AboutFileUtil;
import com.yan.dd_picture.utils.FeignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 文件服务实现类【上传需调用本地文件服务、七牛云文件服务、Minio文件服务】
 *
 * @author 陌溪
 * @since 2018-09-17
 */
@Slf4j
@Service
public class FileServiceImpl extends SuperServiceImpl<FileMapper, File> implements FileService {

//    @Autowired
//    FeignUtil feignUtil;

    @Autowired
    LocalFileService localFileService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileSortService fileSortService;


    @Autowired
    FeignUtil feignUtil;

    @Override
    public String getPicture(String fileIds, String code) {
        if (StringUtils.isEmpty(code)) {
            code = Constants.SYMBOL_COMMA;
        }
        if (StringUtils.isEmpty(fileIds)) {
            log.error(MessageConf.PICTURE_UID_IS_NULL);
            return ResultUtil.result(SysConf.ERROR, MessageConf.PICTURE_UID_IS_NULL);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> changeStringToString = StringUtils.changeStringToString(fileIds, code);
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(SQLConf.UID, changeStringToString);
            List<File> fileList = fileService.list(queryWrapper);
            if (fileList.size() > 0) {
                for (File file : fileList) {
                    if (file != null) {
                        Map<String, Object> remap = new HashMap<>();
                        // 获取七牛云地址
                        remap.put(SysConf.QI_NIU_URL, file.getQiNiuUrl());
                        // 获取Minio对象存储地址
                        remap.put(SysConf.MINIO_URL, file.getMinioUrl());
                        // 获取本地地址
                        remap.put(SysConf.URL, file.getPicUrl());
                        // 后缀名，也就是类型
                        remap.put(SysConf.EXPANDED_NAME, file.getPicExpandedName());
                        remap.put(SysConf.FILE_OLD_NAME, file.getFileOldName());
                        //名称
                        remap.put(SysConf.NAME, file.getPicName());
                        remap.put(SysConf.UID, file.getUid());
                        remap.put(SQLConf.FILE_OLD_NAME, file.getFileOldName());
                        list.add(remap);
                    }
                }
            }
            return ResultUtil.result(SysConf.SUCCESS, list);
        }
    }

    /**
     * 图片上传
     * @param request
     * @param filedatas
     * @param systemConfig
     * @return
     */
    @Override
    public String batchUploadFile(HttpServletRequest request, List<MultipartFile> filedatas, SystemConfig systemConfig) {

        // 判断是否开启
//        String uploadQiNiu = systemConfig.getUploadQiNiu();
        String uploadLocal = systemConfig.getUploadLocal();
//        String uploadMinio = systemConfig.getUploadMinio();

        // 判断来源
        String source = request.getParameter(SysConf.SOURCE);
        //如果是用户上传，则包含用户uid
        String userUid = "";
        //如果是管理员上传，则包含管理员uid
        String adminUid = "";
        //项目名
        String projectName = "";
        //模块名
        String sortName = "";

        // 判断图片来源
        if (SysConf.PICTURE.equals(source)) {
            // 当从vue-mogu-web网站过来的，直接从参数中获取
            userUid = request.getParameter(SysConf.USER_UID);
            adminUid = request.getParameter(SysConf.ADMIN_UID);
            projectName = request.getParameter(SysConf.PROJECT_NAME);
            sortName = request.getParameter(SysConf.SORT_NAME);
        } else if (SysConf.ADMIN.equals(source)) {
            // 当图片从mogu-admin传递过来的时候
            userUid = request.getAttribute(SysConf.USER_UID).toString();
            adminUid = request.getAttribute(SysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(SysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(SysConf.SORT_NAME).toString();
        } else {
            userUid = request.getAttribute(SysConf.USER_UID).toString();
            adminUid = request.getAttribute(SysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(SysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(SysConf.SORT_NAME).toString();
        }

        //projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }

        //TODO 检测用户上传，如果不是网站的用户就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtil.result(SysConf.ERROR, "请先注册");
        }

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.SORT_NAME, sortName);
        queryWrapper.eq(SQLConf.PROJECT_NAME, projectName);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort = null;
        if (fileSorts.size() >= 1) {
            fileSort = fileSorts.get(0);
        } else {
            return ResultUtil.result(SysConf.ERROR, "文件不被允许上传");
        }

        String sortUrl = fileSort.getUrl();
        //判断url是否为空，如果为空，使用默认
        if (StringUtils.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }

        List<File> lists = new ArrayList<>();
        //文件上传
        if (filedatas != null && filedatas.size() > 0) {
            for (MultipartFile filedata : filedatas) {
                String oldName = filedata.getOriginalFilename();
                long size = filedata.getSize();
                //获取扩展名，默认是jpg
                String picExpandedName = FileUtils.getPicExpandedName(oldName);
                //获取新文件名
                String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + picExpandedName;
                String localUrl = "";
                String qiNiuUrl = "";
                String minioUrl = "";
                try {
                    MultipartFile tempFileData = filedata;

                    // 判断是否能够上传至本地
                    if (EOpenStatus.OPEN.equals(uploadLocal)) {
                        localUrl = localFileService.uploadFile(filedata, fileSort);
                    }
                } catch (Exception e) {
                    log.info("上传文件异常: {}", e.getMessage());
                    e.getStackTrace();
                    return ResultUtil.result(SysConf.ERROR, "文件上传失败，请检查系统配置");
                }

                File file = new File();
                file.setCreateTime(new Date(System.currentTimeMillis()));
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(oldName);
                file.setFileSize(size);
                file.setPicExpandedName(picExpandedName);
                file.setPicName(newFileName);
                file.setPicUrl(localUrl);
                file.setStatus(EStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                file.setQiNiuUrl(qiNiuUrl);
                file.setMinioUrl(minioUrl);
                save(file);
                lists.add(file);
            }
            //保存成功返回数据
            return ResultUtil.result(SysConf.SUCCESS, lists);
        }
        return ResultUtil.result(SysConf.ERROR, "请上传图片");
    }

    @Override
    public R ckeditorUploadFile(HttpServletRequest request) {
        String token = request.getParameter(SysConf.TOKEN);
        // 从Redis中获取七牛云配置文件
//        Map<String, String> qiNiuResultMap = feignUtil.getSystemConfigMap(token);
//        SystemConfig systemConfig = feignUtil.getSystemConfigByMap(qiNiuResultMap);

        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setUploadLocal("1");
        systemConfig.setUploadMinio("0");
        systemConfig.setUploadQiNiu("0");
        systemConfig.setLocalPictureBaseUrl("http://localhost:9999");
        systemConfig.setStatus(1);

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        //引用自己设计的一个工具类
        AboutFileUtil af = new AboutFileUtil();
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file != null) {
                //获取旧名称
                String oldName = file.getOriginalFilename();
                //获取扩展名
                String expandedName = FileUtils.getPicExpandedName(oldName);
                //判断是否是图片
                if (!af.isPic(expandedName)) {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "请上传正确的图片");
                    map.put(SysConf.ERROR, errorMap);
                    return R.success(map);
                }

                //对图片大小进行限制
                if (file.getSize() > (10 * 1024 * 1024)) {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "图片大小不能超过10M");
                    map.put(SysConf.ERROR, errorMap);
                    return R.success(map);
                }

                // 设置图片上传服务必要的信息
                request.setAttribute(SysConf.USER_UID, SysConf.DEFAULT_UID);
                request.setAttribute(SysConf.ADMIN_UID, SysConf.DEFAULT_UID);
                request.setAttribute(SysConf.PROJECT_NAME, SysConf.BLOG);
                request.setAttribute(SysConf.SORT_NAME, SysConf.ADMIN);

                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                // 批量上传图片
                String result = fileService.batchUploadFile(request, fileData, systemConfig);
                Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
                String code = resultMap.get(SysConf.CODE).toString();
                if (SysConf.SUCCESS.equals(code)) {
                    List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) resultMap.get(SysConf.DATA);
                    if (resultList.size() > 0) {
                        Map<String, Object> picture = resultList.get(0);
                        String fileName = picture.get(SysConf.PIC_NAME).toString();
//                        String fileId = picture.get(SysConf.FILE_ID).toString();
                        map.put(SysConf.UPLOADED, 1);
                        map.put(SysConf.FILE_NAME, fileName);
//                        map.put(SysConf.FILE_ID,fileId);
                        // 设置博客详情显示方式
                        if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                            String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
                            String qiNiuUrl = picture.get(SysConf.QI_NIU_URL).toString();
                            map.put(SysConf.URL, qiNiuPictureBaseUrl + qiNiuUrl);
                        } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                            String minioPictureBaseUrl = systemConfig.getMinioPictureBaseUrl();
                            String url = minioPictureBaseUrl + picture.get(SysConf.MINIO_URL).toString();
                            map.put(SysConf.URL, url);
                        } else {
                            String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
                            // 设置图片服务根域名
                            String url = localPictureBaseUrl + picture.get(SysConf.PIC_URL).toString();
                            map.put(SysConf.URL, url);
                        }
                    }
                } else {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "上传失败");
                    map.put(SysConf.ERROR, errorMap);
                }
            }
        }
        return R.success(map);
    }

    @Override
    public Object ckeditorUploadCopyFile() {
        HttpServletRequest request = RequestHolder.getRequest();
        // 从参数中获取token【该方法用于ckeditor复制图片上传，所以会携带token在参数中】
        String token = request.getParameter(SysConf.TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, "未读取到携带token");
        }
        String[] params = token.split("\\?url=");
        // 从Redis中获取系统配置文件
        Map<String, String> qiNiuConfig = new HashMap<>();
        Map<String, String> resultMap = feignUtil.getSystemConfigMap(params[0]);
        SystemConfig systemConfig = feignUtil.getSystemConfigByMap(resultMap);

        String userUid = "uid00000000000000000000000000000000";
        String adminUid = "uid00000000000000000000000000000000";
        String projectName = "blog";
        String sortName = "admin";

        // 需要上传的URL
        String itemUrl = params[1];

        // 判断需要上传的域名和本机图片域名是否一致
        if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getQiNiuPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) && itemUrl.indexOf(systemConfig.getQiNiuPictureBaseUrl()) > -1) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
            // 表示优先显示Minio对象存储
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getMinioPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) && itemUrl.indexOf(systemConfig.getMinioPictureBaseUrl()) > -1) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        } else {
            // 表示优先显示本地服务器
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getLocalPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) && itemUrl.indexOf(systemConfig.getLocalPictureBaseUrl()) > -1) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        }

        //projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }

        // TODO 这里可以检测用户上传，如果不是网站的用户或会员就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtil.result(SysConf.ERROR, "请先注册");
        }

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.SORT_NAME, sortName);
        queryWrapper.eq(SQLConf.PROJECT_NAME, projectName);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort = null;
        if (fileSorts.size() > 0) {
            fileSort = fileSorts.get(0);
        } else {
            return ResultUtil.result(SysConf.ERROR, "文件不被允许上传");
        }

        String sortUrl = fileSort.getUrl();

        //判断url是否为空，如果为空，使用默认
        if (StringUtils.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }

        //获取新文件名(默认为jpg)
        String newFileName = System.currentTimeMillis() + ".jpg";

        //文件url访问地址
        String localUrl = "";
        String qiNiuUrl = "";
        String minioUrl = "";

        // 上传到本地服务器【判断是否能够上传至本地】
        if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
            localUrl = localFileService.uploadPictureByUrl(itemUrl, fileSort);
        }

        // 上传七牛云 【判断是否能够上传七牛云】
        if (EOpenStatus.OPEN.equals(systemConfig.getUploadMinio())) {
//            minioUrl = minioService.uploadPictureByUrl(itemUrl);
        }

        // 上传七牛云 【判断是否能够上传七牛云】
        if (EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu())) {
//            qiNiuUrl = qiniuService.uploadPictureByUrl(itemUrl, systemConfig);
        }

        File file = new File();
        file.setCreateTime(new Date(System.currentTimeMillis()));
        file.setFileSortUid(fileSort.getUid());
        file.setFileOldName(itemUrl);
        file.setFileSize(0L);
        file.setPicExpandedName(Constants.FILE_SUFFIX_JPG);
        file.setPicName(newFileName);
        // 设置本地图片
        file.setPicUrl(systemConfig.getLocalPictureBaseUrl() + localUrl);
        // 设置minio图片
        file.setMinioUrl(systemConfig.getMinioPictureBaseUrl() + minioUrl);
        // 设置七牛云图片
        file.setQiNiuUrl(systemConfig.getQiNiuPictureBaseUrl() + qiNiuUrl);
        file.setStatus(EStatus.ENABLE);
        file.setUserUid(userUid);
        file.setAdminUid(adminUid);
        fileService.save(file);

        Map<String, Object> result = new HashMap<>();
        result.put(SysConf.UPLOADED, 1);
        result.put(SysConf.FILE_NAME, newFileName);
        // 设置显示方式
        if (EFilePriority.QI_NIU.equals(qiNiuConfig.get(SysConf.PICTURE_PRIORITY))) {
            result.put(SysConf.URL, systemConfig.getQiNiuPictureBaseUrl() + qiNiuUrl);
        } else if (EFilePriority.MINIO.equals(qiNiuConfig.get(SysConf.PICTURE_PRIORITY))) {
            result.put(SysConf.URL, systemConfig.getMinioPictureBaseUrl() + localUrl);
        } else {
            result.put(SysConf.URL, systemConfig.getLocalPictureBaseUrl() + localUrl);
        }
        return result;
    }

    @Override
    public Object ckeditorUploadToolFile(HttpServletRequest request) {
        String token = request.getParameter(SysConf.TOKEN);
        // 从Redis中获取系统配置【需要传入token】
        Map<String, String> qiNiuResultMap = feignUtil.getSystemConfigMap(token);
        SystemConfig systemConfig = feignUtil.getSystemConfigByMap(qiNiuResultMap);

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        //引用自己设计的一个工具类
        AboutFileUtil af = new AboutFileUtil();
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file != null) {

                // 获取旧名称
                String oldName = file.getOriginalFilename();
                // 获取扩展名
                String expandedName = FileUtils.getPicExpandedName(oldName);
                // 判断是否安全文件
                if (!af.isSafe(expandedName)) {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "请上传正确格式的文件");
                    map.put(SysConf.ERROR, errorMap);
                    return map;
                }

                //对文件大小进行限制
                if (file.getSize() > (50 * 1024 * 1024)) {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "文件大小不能超过50M");
                    map.put(SysConf.ERROR, errorMap);
                    return map;
                }
                // 设置图片上传服务必要的信息
                request.setAttribute(SysConf.USER_UID, SysConf.DEFAULT_UID);
                request.setAttribute(SysConf.ADMIN_UID, SysConf.DEFAULT_UID);
                request.setAttribute(SysConf.PROJECT_NAME, SysConf.BLOG);
                request.setAttribute(SysConf.SORT_NAME, SysConf.ADMIN);

                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                String result = fileService.batchUploadFile(request, fileData, systemConfig);
                Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
                String code = resultMap.get(SysConf.CODE).toString();
                if (SysConf.SUCCESS.equals(code)) {
                    List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) resultMap.get(SysConf.DATA);
                    if (resultList.size() > 0) {
                        Map<String, Object> picture = resultList.get(0);
                        String fileName = picture.get(SysConf.PIC_NAME).toString();
                        map.put(SysConf.UPLOADED, 1);
                        map.put(SysConf.FILE_NAME, fileName);
                        // 设置显示方式
                        if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                            String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
                            String qiNiuUrl = qiNiuPictureBaseUrl + picture.get(SysConf.QI_NIU_URL).toString();
                            map.put(SysConf.URL, qiNiuUrl);
                        } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                            String minioPictureBaseUrl = systemConfig.getMinioPictureBaseUrl();
                            // 设置图片服务根域名
                            String url = minioPictureBaseUrl + picture.get(SysConf.MINIO_URL).toString();
                            map.put(SysConf.URL, url);
                        } else {
                            String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
                            // 设置图片服务根域名
                            String url = localPictureBaseUrl + picture.get(SysConf.PIC_URL).toString();
                            map.put(SysConf.URL, url);
                        }
                    }
                    return map;
                } else {
                    map.put(SysConf.UPLOADED, 0);
                    errorMap.put(SysConf.MESSAGE, "上传失败");
                    map.put(SysConf.ERROR, errorMap);
                    return map;
                }
            }
        }
        return null;
    }


}
