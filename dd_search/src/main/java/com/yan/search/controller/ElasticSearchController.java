package com.yan.search.controller;

import com.yan.dd_common.constant.MessageConf;
import com.yan.dd_common.constant.SysConf;
import com.yan.dd_common.core.R;
import com.yan.dd_common.entity.Blog;
import com.yan.dd_common.feign.WebFeignClient;
import com.yan.dd_common.utils.JsonUtils;
import com.yan.dd_common.utils.ResultUtil;
import com.yan.dd_common.utils.StringUtils;
import com.yan.search.pojo.ESBlogIndex;
import com.yan.search.repository.BlogRepository;
import com.yan.search.service.ElasticSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanshuang
 * @date 2022/3/20 7:38 下午
 */
@RequestMapping("/search")
@Api(value = "ElasticSearch相关接口", tags = {"ElasticSearch相关接口"})
@RestController
public class ElasticSearchController {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ElasticSearchService searchService;

    @Resource
    private WebFeignClient webFeignClient;

    @ApiOperation(value = "通过ElasticSearch搜索博客", notes = "通过ElasticSearch搜索博客", response = String.class)
    @GetMapping("/elasticSearchBlog")
    public R searchBlog(HttpServletRequest request,
                        @RequestParam(required = false) String keywords,
                        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer
                                     currentPage,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer
                                     pageSize) throws IOException {

        if (StringUtils.isEmpty(keywords)) {
            return R.success(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        return R.success(SysConf.SUCCESS, searchService.search(keywords, currentPage, pageSize));
    }

    @ApiOperation(value = "ElasticSearch初始化索引", notes = "ElasticSearch初始化索引", response = String.class)
    @PostMapping("/initElasticSearchIndex")
    public R initElasticSearchIndex() throws ParseException {
        elasticsearchRestTemplate.deleteIndex(ESBlogIndex.class);
        elasticsearchRestTemplate.createIndex(ESBlogIndex.class);
        elasticsearchRestTemplate.putMapping(ESBlogIndex.class);

        Long page = 1L;
        Long row = 10L;
        Integer size = 0;

        do {
            // 查询blog信息
            String result = webFeignClient.getBlogBySearch(page, row);

            //构建blog
            List<Blog> blogList = getList(result, Blog.class);
            size = blogList.size();

            List<ESBlogIndex> esBlogIndexList = blogList.stream()
                    .map(searchService::buidBlog).collect(Collectors.toList());

            //存入索引库
            if (esBlogIndexList != null) {
                blogRepository.saveAll(esBlogIndexList);
            }
            // 翻页
            page++;
        } while (size == 15);

        return R.success(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    /**
     * 获取结果集的内容，返回的是 List<POJO>，带分页的情况
     *
     * @param result
     * @return
     */
    public static <T> List<T> getList(String result, Class<T> beanType) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        Map<String, Object> dataMap = (Map<String, Object>) JsonUtils.jsonToObject(result, Map.class);
        if ("success".equals(dataMap.get("code"))) {

            Map<String, Object> data = (Map<String, Object>) dataMap.get("data");

            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("records");
            List<T> resultList = new ArrayList<>();
            list.forEach(item -> {
                resultList.add(JsonUtils.mapToPojo(item, beanType));
            });
            return resultList;
        }
        return null;
    }


    @ApiOperation(value = "通过uids删除ElasticSearch博客索引", notes = "通过uids删除ElasticSearch博客索引", response = String.class)
    @PostMapping("/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true, value = "uid") String uids) {

        List<String> uidList = StringUtils.changeStringToString(uids, SysConf.FILE_SEGMENTATION);

        for (String uid : uidList) {
            blogRepository.deleteById(uid);
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "通过博客uid删除ElasticSearch博客索引", notes = "通过uid删除博客", response = String.class)
    @PostMapping("/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid) {
        blogRepository.deleteById(uid);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "ElasticSearch通过博客Uid添加索引", notes = "添加博客", response = String.class)
    @PostMapping("/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid) {

        String result = webFeignClient.getBlogByUid(uid);

        Blog eblog = getData(result, Blog.class);
        if (eblog == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INSERT_FAIL);
        }
        ESBlogIndex blog = searchService.buidBlog(eblog);
        blogRepository.save(blog);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    public static <T> T getData(String result, Class<T> beanType) {

        Map<String, Object> dataMap = (Map<String, Object>) JsonUtils.jsonToObject(result, Map.class);
        if ("success".equals(dataMap.get("code"))) {
            Map<String, Object> data = (Map<String, Object>) dataMap.get("data");
            T t = JsonUtils.mapToPojo(data, beanType);
            return t;
        }
        return null;
    }

}
