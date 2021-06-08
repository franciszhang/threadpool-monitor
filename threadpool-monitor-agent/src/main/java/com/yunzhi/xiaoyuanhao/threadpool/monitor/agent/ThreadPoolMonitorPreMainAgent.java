package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent;


import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.interceptor.EndpointDiscovererInterceptor;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.interceptor.ThreadPoolConstructorInterceptor;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.interceptor.ThreadPoolRejectInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.jar.JarFile;

/**
 * @author francis
 * @version 2021-05-28
 */
public class ThreadPoolMonitorPreMainAgent {
    private static final String THREAD_POOL_EXECUTOR_NAME = "java.util.concurrent.ThreadPoolExecutor";
    private static final String WEB_ENDPOINT_DISCOVERER_NAME = "org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer";


    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            inst.appendToBootstrapClassLoaderSearch(
                    new JarFile(findAgentPath() + "/threadpool-monitor-agent-manager-1.0.0.jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuddy with = new ByteBuddy().with(TypeValidation.of(true));
        ElementMatcher.Junction<TypeDefinition> elementMatcher = ElementMatchers
                .is(ThreadPoolExecutor.class)
                .or(ElementMatchers.named(WEB_ENDPOINT_DISCOVERER_NAME));

        new AgentBuilder
                .Default(with)
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .type(elementMatcher)
                .transform(getTransformerThreadPool())
                .transform(getTransformerWebDiscoverer())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new RedefinitionListener())
                .with(new Listener())
                .with(new InstallListener())
                .installOn(inst);
    }


    private static AgentBuilder.Transformer getTransformerThreadPool() {
        return (builder, typeDescription, classLoader, module) -> {
            if (THREAD_POOL_EXECUTOR_NAME.equals(typeDescription.getName())) {
                return builder
                        .visit(Advice
                                .to(ThreadPoolConstructorInterceptor.class)
                                .on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(7))
                                ))
                        .visit(Advice
                                .to(ThreadPoolRejectInterceptor.class)
                                .on(ElementMatchers.named("reject")));
            }
            return builder;
        };
    }

    private static AgentBuilder.Transformer getTransformerWebDiscoverer() {
        return (builder, typeDescription, classLoader, module) -> {
            if (WEB_ENDPOINT_DISCOVERER_NAME.equals(typeDescription.getName())) {
                return builder
                        .visit(Advice
                                .to(EndpointDiscovererInterceptor.class)
                                .on(ElementMatchers.isConstructor()));
            }
            return builder;
        };
    }


    private static class Listener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }

        @Override
        public void onTransformation(final TypeDescription typeDescription, final ClassLoader classLoader,
                                     final JavaModule module, final boolean loaded, final DynamicType dynamicType) {
        }

        @Override
        public void onIgnored(final TypeDescription typeDescription, final ClassLoader classLoader,
                              final JavaModule module, final boolean loaded) {
        }

        @Override
        public void onError(final String typeName, final ClassLoader classLoader, final JavaModule module,
                            final boolean loaded, final Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }

    private static class RedefinitionListener implements AgentBuilder.RedefinitionStrategy.Listener {

        @Override
        public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
            /* do nothing */
        }

        @Override
        public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
            throwable.printStackTrace();
            return Collections.emptyList();
        }

        @Override
        public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
            /* do nothing */
        }
    }

    private static class InstallListener implements AgentBuilder.InstallationListener {

        @Override
        public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {

        }

        @Override
        public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
        }

        @Override
        public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
            throwable.printStackTrace();
            return throwable;
        }

        @Override
        public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {

        }
    }

    private static String findAgentPath() {
        String classResourcePath = ThreadPoolMonitorPreMainAgent.class.getName().replaceAll("\\.", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();
            String jarPath = urlString.substring(urlString.indexOf("file:"), urlString.indexOf("!"));
            File agentJarFile;
            try {
                agentJarFile = new File(new URL(jarPath).toURI());
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException("agent jar path can`t find", e);
            }

            return agentJarFile.getParent();
        }
        throw new RuntimeException("agent jar path can`t find");
    }
}
