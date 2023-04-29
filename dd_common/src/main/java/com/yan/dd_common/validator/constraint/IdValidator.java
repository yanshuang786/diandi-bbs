package com.yan.dd_common.validator.constraint;


import com.yan.dd_common.constant.Constants;
import com.yan.dd_common.utils.StringUtils;
import com.yan.dd_common.validator.annotion.IdValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ID校验器，主要判断是否为空，并且长度是否为32
 *
 * @author 陌溪
 * @date 2019年12月4日22:48:43
 */
public class IdValidator implements ConstraintValidator<IdValid, String> {


    @Override
    public void initialize(IdValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || StringUtils.isEmpty(value.trim()) || value.length() != 32) {
            return false;
        }
        return true;
    }
}
