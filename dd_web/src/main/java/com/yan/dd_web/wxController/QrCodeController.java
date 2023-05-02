package com.yan.dd_web.wxController;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yan.dd_common.core.R;
import com.yan.dd_common.utils.CodeLoginUtil;
import com.yan.dd_common.utils.HttpClientUtil;
import com.yan.dd_web.config.WxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author yanshuang
 * @date 2023/4/29 22:56
 */
@RestController
@RequestMapping("wechat")
public class QrCodeController {


    @Autowired
    private WxConfig wxConfig;

    @GetMapping("qrcode")
    public R getQrCode() {
        // 获取token开发者
        String accessToken =getAccessToken();
        String getQrCodeUrl = wxConfig.getQrCodeUrl().replace("TOKEN", accessToken);
        // 这里生成一个带参数的二维码，参数是scene_id
        String sceneId = CodeLoginUtil.getRandomString(8);
        String json="{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\"" +", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneId+"\"}}}";
        String result  = HttpClientUtil.doPostJson(getQrCodeUrl,json);

        HashMap<String,String> jsonObject = JSONObject.parseObject(result, (Type) HashMap.class);

        // qrcode
        HashMap<String, String> map = new HashMap<>(8);
        map.put("url", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + jsonObject.get("ticket"));
        map.put("scene_id",sceneId);
        return R.success(map);
    }


    /**
     *  获取accessToken
     * @return
     */
    public String getAccessToken(){
        String accessToken = null;
        String getTokenUrl = wxConfig.getTokenUrl().replace("APPID", wxConfig.getAppId()).replace("SECRET", wxConfig.getAppSecret());
        String result = HttpClientUtil.doGet(getTokenUrl);
        JSONObject jsonObject = JSONObject.parseObject(result);
        accessToken = jsonObject.getString("access_token");
        return accessToken ;
    }

}
