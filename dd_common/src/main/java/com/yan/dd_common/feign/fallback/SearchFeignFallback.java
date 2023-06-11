package com.yan.dd_common.feign.fallback;

import com.yan.dd_common.feign.SearchFeignClient;
import com.yan.dd_common.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanshuang
 * @date 2023/5/1 16:50
 */
@Component
@Slf4j
public class SearchFeignFallback implements SearchFeignClient {

    @Override
    public String deleteElasticSearchByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
    }

    @Override
    public String deleteElasticSearchByUids(String uids) {
        log.error("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
    }

    @Override
    public String initElasticSearchIndex() {
        log.error("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
    }

    @Override
    public String addElasticSearchIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
    }
}
