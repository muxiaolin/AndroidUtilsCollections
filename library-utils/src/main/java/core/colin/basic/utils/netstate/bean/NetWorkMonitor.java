package core.colin.basic.utils.netstate.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author : 彭林
 * date   : 2020/6/28
 * desc   :
 */
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Target(ElementType.METHOD)//标记在方法上
public @interface NetWorkMonitor {

    //监听的网络状态变化 默认全部监听并提示
    NetWorkState[] monitorFilter() default {NetWorkState.ETH, NetWorkState.GPRS, NetWorkState.WIFI, NetWorkState.NONE};
}
