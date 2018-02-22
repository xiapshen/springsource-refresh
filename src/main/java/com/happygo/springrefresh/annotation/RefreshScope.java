package com.happygo.springrefresh.annotation;

import java.lang.annotation.*;

/**
 * ClassName: RefreshScope <br/>
 * Description: 刷新范围注解类 <br/>
 * Date: 2017/11/28 12:22 <br/>
 *
 * @author sxp(sxp 1378127237 qq.com)<br>
 * @version 1.0 <br/>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshScope {

    /**
     * Path string.
     *
     * @return the string
     */
    String path() default "";

    /**
     * File string.
     *
     * @return the string
     */
    String file();
}
