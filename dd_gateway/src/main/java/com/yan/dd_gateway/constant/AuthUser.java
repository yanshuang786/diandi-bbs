package com.yan.dd_gateway.constant;

/**
 * @author yanshuang
 * @date 2023/4/24 20:08
 */

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author modebing
 * @since 2022-11-05
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class AuthUser implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 户主姓名
     */

    private String username;

    /**
     * 密码
     */

    private String password;

    /**
     * 账户是否可用
     */
    private int enabled;

    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 更新时间
     */
    protected Date updateTime;


    @JsonIgnore
    private Collection<SimpleGrantedAuthority> permissions;

    public AuthUser() {
    }

    public AuthUser(Long id, String username, String password, int enabled, Date createTime,Date updateTime, List<Permission> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.permissions = new ArrayList<>();
        for (Permission permission : permissions) {
            this.permissions.add(new SimpleGrantedAuthority(permission.getName()));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled == 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    public Collection<SimpleGrantedAuthority> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions.stream().map(Permission::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
