package com.yan.dd_common.feign.fallback;

import com.yan.dd_common.enums.RequestHolder;
import com.yan.dd_common.feign.PictureFeignClient;
import com.yan.dd_common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yanshuang
 * @date 2023/4/28 17:14
 */
@Component
@Slf4j
public class PictureFeignFallback implements PictureFeignClient {

    @Override
    public String getPicture(String fileIds, String code) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，服务降级返回，请求路径: {}", requestURL);
        return JsonUtils.objectToJson("获取图片服务降级返回");
    }


}
