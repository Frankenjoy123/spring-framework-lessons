package com.aop.demo.jdkproxy;

import com.aop.demo.service.UserService;
import com.aop.demo.service.UserServiceImpl;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/15.
 */
public class JdkProxyMain {

    public static void main(String[] args) {

        UserService userService = new UserServiceImpl();

        MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);

        UserService proxy = (UserService) invocationHandler.getProxy();
        proxy.add();
    }
}
