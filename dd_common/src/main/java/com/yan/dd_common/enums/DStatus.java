package com.yan.dd_common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanshuang
 * @date 2023/4/25 17:07
 */
@Getter
@AllArgsConstructor
public enum DStatus {

    DISABLE("0", "status停用"),
    ENABLE("1", "正常"),
    UNDEL("0","未删除"),
    DELETED("2", "删除");

    private final String code;

    private final String info;


}