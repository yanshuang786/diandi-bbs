package com.yan.sms.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

/**
 * @author yanshuang
 * @date 2023/6/11 01:06
 */
@Data
@Document(indexName = "blog", createIndex = true)
public class ESBlogIndex {
    @Id
    private Integer id;


    //    @Field(index = true,type = FieldType.Text)
    private String title;

    //    @Field(index = true,type = FieldType.Text)
    private String summary;

    //    @Field(index = true,type = FieldType.Text)
    private String content;

    //    @Field(index = true,type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String blogSortName;

    private String author;

    private Integer blogSortUid;

    private String photoUrl;

    private List<Integer> tagUidList;

    private List<String> tagNameList;


}

