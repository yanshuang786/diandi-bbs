package com.yan.search.config;

import com.yan.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yanshuang
 * @date 2023/5/1 21:30
 */
@Component
@Order(value = 0)
@Slf4j
public class InitIndex implements CommandLineRunner {

    @Autowired
    private ElasticSearchService searchService;

    @Override
    public void run(String... args) throws Exception {
        log.info("创建索引");
//        searchService.initElasticSearchIndex();
    }

    // TODO 初始化时应该刷新缓存
}
