package com.frank.threadpool.monitor.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author francis
 * @version 2021-06-01
 */
public class TestAgentMain {

    public static void main(String[] args) throws Exception {


        test1();
        test2();
        test3();
        test4();
    }


    //方法代理和自定义方法逻辑
    private static void test1() throws Exception {
        String r = new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(TestAgentMain.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .sayHelloFoo();
        System.out.println("r:" + r);
        System.out.println("bar:" + Bar.sayHelloBar());
    }

    //方法和字段定义
    private static void test2() throws Exception {
        Class<?> type = new ByteBuddy()
                .subclass(Object.class)
                .name("MyClassName")
                .defineMethod("custom", String.class, Modifier.PUBLIC)
                .intercept(MethodDelegation.to(Bar.class))
                .defineField("x", String.class, Modifier.PUBLIC)
                .make()
                .load(TestAgentMain.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Method m = type.getDeclaredMethod("custom", null);
        System.out.println(m.invoke(type.newInstance()));
        System.out.println(Bar.sayHelloBar());
        System.out.println(type.getDeclaredField("x"));
    }

    //重定义一个已经存在的类
    private static void test3() {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sayHelloFoo"))
                .intercept(FixedValue.value("Hello Foo Redefined"))
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Foo f = new Foo();
        //Hello Foo Redefined
        System.out.println(f.sayHelloFoo());
    }

    private static void test4() {
        Foo1 foo1 = new Foo1();
        new ByteBuddy()
                .redefine(Foo.class)
                .name(Foo1.class.getName())
                .make()
                .load(Foo1.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        System.out.println(foo1.sayHelloFoo());
    }


    public static class Foo {
        public String sayHelloFoo() {
            return "Hello in Foo!";
        }
    }

    public static class Foo1 {
        public String sayHelloFoo() {
            return "####Hello in Foo1111!";
        }
    }

    public static class Bar {
        @BindingPriority(3)
        public static String sayHelloBar() {
            return "Holla in Bar!";
        }

        @BindingPriority(2)
        public static String sayBar() {
            return "bar";
        }
    }


}
