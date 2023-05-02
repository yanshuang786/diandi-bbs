package com.yan.search.repository;

import com.yan.search.pojo.ESBlogIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yanshuang
 * @date 2022/3/21 9:35 下午
 */
public interface BlogRepository extends ElasticsearchRepository<ESBlogIndex, String> {
}
