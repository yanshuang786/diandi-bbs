package com.yan.dd_common.feign;

import com.yan.dd_common.feign.fallback.SearchFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yanshuang
 * @date 2023/5/1 16:49
 */
@FeignClient(name = "dd-search", fallback = SearchFeignFallback.class)
public interface SearchFeignClient {


    /**
     * 通过博客uid删除ElasticSearch博客索引
     *
     * @param uid
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid);

    /**
     * 通过uids删除ElasticSearch博客索引
     *
     * @param uids
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true) String uids);

    /**
     * 初始化ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/initElasticSearchIndex")
    public String initElasticSearchIndex();

    /**
     * 通过uid来增加ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid);

}
