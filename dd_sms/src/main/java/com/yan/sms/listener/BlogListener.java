package com.yan.sms.listener;

import com.alibaba.fastjson.JSONObject;
import com.yan.bbs.mapper.BlogMapper;
import com.yan.bbs.service.SysConfigService;
import com.yan.dd_common.constant.RedisConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.feign.SearchFeignClient;
import com.yan.dd_common.global.Constants;
import com.yan.dd_common.redis.RedisUtil;
import com.yan.dd_common.utils.JsonUtils;
import com.yan.sms.config.RabbitMqConfig;
import com.yan.sms.pojo.ESBlogIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于更新博客的Redis和ElasticSearch
 *
 * @author yanshuang
 * @date 2022/3/22 11:06 上午
 */
@Component
@Slf4j
public class BlogListener {

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public SysConfigService sysConfigService;

    @Autowired
    public ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public BlogMapper blogMapper;


    /**
     * 通过@RabbitListener 注解指定该方法监听的队列，该注解的参数就是消息队列的名字
     * @param map 消息
     */
    @RabbitListener(queues = RabbitMqConfig.DD_BLOG)
    public void update(HashMap<String, String> map) {
        log.info("更新Redis和ES");

        //从Redis清空对应的数据
        redisUtil.delete(RedisConf.HOT_BLOG);
        redisUtil.delete(RedisConf.NEW_BLOG);

        if(map != null) {
            // 当前是删除，添加，修改，
            String comment = map.get(SysConf.COMMAND);
            String id = map.get(SysConf.BLOG_ID);

            //从Redis清空对应的数据
            redisUtil.delete(RedisConf.HOT_BLOG);
            redisUtil.delete(RedisConf.NEW_BLOG);

            // 获取检索的模式
            String searchModel = sysConfigService.selectConfigByKey("sys_search_mode");
            try {
                switch (comment) {

                    case SysConf.ADD: {
                        log.info("增加博客");
                        // 新增ES
                        Blog blog = blogMapper.selectById(Integer.valueOf(id));
                        ESBlogIndex esBlogIndex = new ESBlogIndex();
                        BeanUtils.copyProperties(blog, esBlogIndex);
                        elasticsearchOperations.save(esBlogIndex);
                    }
                    break;

                    case SysConf.UPDATE: {
                        log.info("更新博客");
                        Blog blog = blogMapper.selectById(Integer.valueOf(id));
                        ESBlogIndex esBlogIndex = new ESBlogIndex();
                        esBlogIndex.setId(blog.getId());
                        elasticsearchOperations.delete(esBlogIndex);
                        BeanUtils.copyProperties(blog, esBlogIndex);
                        elasticsearchOperations.save(esBlogIndex);
                    }
                    break;

                    case SysConf.DELETE: {
                        log.info("删除博客: id:" + id);
                        ESBlogIndex esBlogIndex = new ESBlogIndex();
                        esBlogIndex.setId(Integer.valueOf(id));
                        elasticsearchOperations.delete(esBlogIndex);
                    }
                    break;
                    default: {
                        log.info("没有对博客做任何处理哟！");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("处理博客异常，searchModel: " + searchModel);
            }
        }
    }


    private void updateSearch(Map<String, String> map) {
        try {
            String level = map.get(SysConf.LEVEL);
            String createTime = map.get(SysConf.CREATE_TIME);
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(createTime))));
            String[] list = sd.split(Constants.SYMBOL_HYPHEN);
            String year = list[0];
            String month = list[1];
            String key = year + "年" + month + "月";
            redisUtil.delete(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON + key);
            String jsonResult = (String) redisUtil.get(RedisConf.MONTH_SET);
            ArrayList<String> monthSet = (ArrayList<String>) JsonUtils.jsonArrayToArrayList(jsonResult);
            Boolean haveMonth = false;
            if (monthSet != null) {
                for (String item : monthSet) {
                    if (item.equals(key)) {
                        haveMonth = true;
                        break;
                    }
                }
                if (!haveMonth) {
                    monthSet.add(key);
                    redisUtil.set(RedisConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
                }
            }

        } catch (Exception e) {
            log.error("更新Redis失败");
        }
    }
}
