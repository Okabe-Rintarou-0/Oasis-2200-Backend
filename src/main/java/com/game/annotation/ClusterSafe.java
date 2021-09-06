package com.game.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: 标注了这个的话就说明这个方法可以不上分布式锁直接用
 * @date 2021/8/26 13:20
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ClusterSafe {

}
