package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.interceptor;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.manager.ThreadPoolMonitorAgentManager;
import net.bytebuddy.asm.Advice;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author francis
 * @version 2021-06-07
 */
public class ThreadPoolConstructorInterceptor {

    @Advice.OnMethodExit
    public static void intercept(@Advice.This Object obj, @Advice.AllArguments Object[] allArguments) {
        if (obj instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) obj;
            ThreadPoolMonitorAgentManager.putThreadPoolExecutor(obj.hashCode(), executor);
        }
    }

}
