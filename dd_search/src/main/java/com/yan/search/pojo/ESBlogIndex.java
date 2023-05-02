package com.yan.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

/**
 * ESBlogIndex
 */
@Data
@Document(indexName = "blog", type = "docs", shards = 1, replicas = 0)
public class ESBlogIndex {
    @Id
    private Integer id;

    private Long uid;

    private String type;

//    @Field(index = true,type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;

//    @Field(index = true,type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String summary;

//    @Field(index = true,type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

//    @Field(index = true,type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String blogSortName;

    private Integer blogSortId;

    private String isPublish;

    private Date createTime;

    private String author;

    private String photoUrl;

    private List<Integer> tagUidList;

    private List<String> tagNameList;


}
