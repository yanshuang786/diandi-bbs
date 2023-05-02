package com.yan.search.service.impl;

import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.entity.Tag;
import com.yan.dd_common.utils.StringUtils;
import com.yan.search.pojo.ESBlogIndex;
import com.yan.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanshuang
 * @date 2022/3/20 8:52 下午
 */
@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;


//    @Resource
//    HighlightResultHelper highlightResultHelper;


    @Override
    public Map<String, Object> search(String keywords, Integer currentPage, Integer pageSize) {

        List<HighlightBuilder.Field> highlightFields = new ArrayList<>();

        HighlightBuilder.Field titleField = new HighlightBuilder.Field("title").preTags("<span style='color:red'>").postTags("</span>");
        HighlightBuilder.Field summaryField = new HighlightBuilder.Field("summary").preTags("<span style='color:red'>").postTags("</span>");
        highlightFields.add(titleField);
        highlightFields.add(summaryField);

        HighlightBuilder.Field[] highlightFieldsAry = highlightFields.toArray(new HighlightBuilder
                .Field[highlightFields.size()]);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().add(QueryBuilders.matchQuery("title",keywords));
        boolQueryBuilder.should().add(QueryBuilders.matchQuery("summary",keywords));
        boolQueryBuilder.should().add(QueryBuilders.matchQuery("content",keywords));


        // 创建查询构造器
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightFields(highlightFieldsAry)
                .build();

        // 查询
        SearchHits<ESBlogIndex> search = elasticsearchOperations.search(build, ESBlogIndex.class);

        ArrayList<ESBlogIndex> list = new ArrayList<>();
        for(SearchHit<ESBlogIndex> searchHit : search.getSearchHits()) {
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setSummary(highLightFields.get("summary") == null ? searchHit.getContent().getSummary() : highLightFields.get("summary").get(0));
            searchHit.getContent().setContent(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // 放到实体类中
            list.add(searchHit.getContent());
        }
        long total = search.getTotalHits();
        List<ESBlogIndex> blogList = list;
        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.TOTAL, total);
        map.put(SysConf.PAGE_SIZE, pageSize);
        map.put(SysConf.CURRENT_PAGE, currentPage + 1);
        map.put(SysConf.BLOG_LIST, blogList);
        return map;
    }

    @Override
    public ESBlogIndex buidBlog(Blog eblog) {

        //构建blog对象
        ESBlogIndex blog = new ESBlogIndex();
        blog.setId(eblog.getId());
        blog.setUid(eblog.getUserId());
        blog.setTitle(eblog.getTitle());
        blog.setSummary(eblog.getSummary());
        blog.setContent(eblog.getContent());

        if (eblog.getBlogSort() != null) {
            blog.setBlogSortName(eblog.getBlogSort().getSortName());
            blog.setBlogSortId(eblog.getBlogSortId());
        }

        if (eblog.getTagList() != null) {
            List<Tag> tagList = eblog.getTagList();
            List<Integer> tagUidList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            tagList.forEach(item -> {
                if (item != null) {
                    tagUidList.add(item.getId());
                    tagNameList.add(item.getContent());
                }
            });
            blog.setTagNameList(tagNameList);
            blog.setTagUidList(tagUidList);
        }

        blog.setIsPublish(eblog.getIsPublish());
        blog.setAuthor(eblog.getAuthor());
        blog.setCreateTime(eblog.getCreateTime());
        if (eblog.getPhotoUrl() != null ) {
            blog.setPhotoUrl(eblog.getPhotoUrl());
        } else {
            blog.setPhotoUrl("");
        }
        return blog;
    }

}
