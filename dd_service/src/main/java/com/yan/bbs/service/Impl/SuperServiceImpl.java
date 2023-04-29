package com.yan.bbs.service.Impl;

import com.yan.bbs.mapper.SuperMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @author yanshuang
 * @date 2023/4/25 17:00
 */
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

}
