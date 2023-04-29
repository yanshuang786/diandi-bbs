package com.yan.dd_common.constant;


/**
 * @author yanshuang
 * @date 2023/3/28 15:27
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
//    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi://";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap://";


    /**
     * 博客分类
     */
    public final static String BLOG_SORT_BY_MONTH = "BLOG_SORT_BY_MONTH";

    public static final String USER_LIKE_BLOG_KEY = "user-like-blog-key";


    //异常类型
    public static final String DELIMITER_TO = "@";
    public static final String DELIMITER_COLON = ":";
    public final static String SYMBOL_COLON = ":";

    public final static String DATE_FORMAT_YYYY_MM = "yyyy-MM";

    public final static String SYMBOL_HYPHEN = "-";


    /**
     * 字符串数字 0~11
     */
    public final static String STR_ZERO = "0";
    public final static String STR_ONE = "1";
    public final static String STR_TWO = "2";
    public final static String STR_THREE = "3";
    public final static String STR_FOUR = "4";
    public final static String STR_FIVE = "5";
    public final static String STR_SIX = "6";
    public final static String STR_SEVEN = "7";
    public final static String STR_EIGHT = "8";
    public final static String STR_NINE = "9";
    public final static String STR_TEN = "10";
    public final static String STR_ELEVEN = "11";
    public final static String STR_500 = "500";



    /**
     * 英文符号
     */
    public final static String SYMBOL_COMMA = ",";
    public final static String SYMBOL_POINT = ".";
    public final static String SYMBOL_QUESTION = "?";
    public final static String SYMBOL_STAR = "*";
    public final static String SYMBOL_WELL = "#";
    public final static String SYMBOL_UNDERLINE = "_";
    public final static String SYMBOL_LEFT_BRACKET = "{";
    public final static String SYMBOL_RIGHT_BRACKET = "}";
    public final static String SYMBOL_RIGHT_EQUAL = "=";
    public final static String SYMBOL_LEFT_OBLIQUE_LINE = "/";
}
