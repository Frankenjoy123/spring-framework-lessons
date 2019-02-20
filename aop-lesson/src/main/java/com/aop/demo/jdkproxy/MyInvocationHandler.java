package com.aop.demo.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/15.
 */
public class MyInvocationHandler implements InvocationHandler{


    private Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("===before===");

        Object result = method.invoke(target,args);

        System.out.println("====after===");

        return result;
    }


    public Object getProxy(){

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
            ,target.getClass().getInterfaces(),this);
    }

}
