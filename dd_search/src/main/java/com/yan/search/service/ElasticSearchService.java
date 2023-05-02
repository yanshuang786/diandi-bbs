package com.yan.search.service;

import com.yan.dd_common.entity.Blog;
import com.yan.search.pojo.ESBlogIndex;

import java.util.Map;

/**
 * @author yanshuang
 * @date 2022/3/20 8:52 下午
 */
public interface ElasticSearchService {
    Map<String, Object> search(String keywords, Integer currentPage, Integer pageSize);

    public ESBlogIndex buidBlog(Blog eblog);

}
