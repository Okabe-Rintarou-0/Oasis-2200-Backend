package com.game.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/29 19:05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Properties {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
