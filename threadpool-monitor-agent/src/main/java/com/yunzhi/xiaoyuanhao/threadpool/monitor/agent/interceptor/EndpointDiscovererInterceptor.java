package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.interceptor;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint.ThreadPoolAgentEndpoint;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;

/**
 * @author francis
 * @version 2021-06-07
 */
public class EndpointDiscovererInterceptor {

    @Advice.OnMethodExit
    public static void intercept(@Advice.This Object obj, @Advice.AllArguments Object[] allArguments) {
        if (allArguments == null || allArguments.length == 0) {
            return;
        }

        if (!(allArguments[0] instanceof ApplicationContext)) {
            return;
        }

        ApplicationContext applicationContext = (ApplicationContext) allArguments[0];

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ThreadPoolAgentEndpoint.class);
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition("threadPoolAgentEndpoint", beanDefinition);

    }
}
