package com.yan.bbs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yanshuang
 * @date 2023/4/25 17:09
 */
@Data
@TableName("d_role_menu")
public class SysRoleMenu {
    /** 角色ID */
    @TableId
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}

