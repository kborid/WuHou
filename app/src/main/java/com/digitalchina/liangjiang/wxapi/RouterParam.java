package com.digitalchina.liangjiang.wxapi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author kborid
 * @date 2017/2/9 0009
 */
public @interface RouterParam {
    String value() default "";
}
