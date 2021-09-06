package com.game.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/29 18:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Context {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
