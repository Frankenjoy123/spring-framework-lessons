package com.aop.demo.cglibproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/15.
 */
public class CglibProxyMain {

    public static void main(String[] args) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibProxyMain.class);
        enhancer.setCallback(new MyMethodInterceptor());

        CglibProxyMain main = (CglibProxyMain) enhancer.create();
        main.test();
        System.out.println(main);

    }


    public void test(){
        System.out.println("hello test");
    }

    private static class MyMethodInterceptor implements MethodInterceptor{

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {


            System.out.println("before invoke : " + method);

            Object result = methodProxy.invokeSuper(o,objects);

            System.out.println("after invoke : " + method);

            return result;
        }
    }
}
