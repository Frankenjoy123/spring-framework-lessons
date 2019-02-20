package com.aop.demo;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/16.
 */
@Aspect
public class UserServiceAspect {

    @Pointcut("execution(* com.aop.demo.service..*.*(..))")
    public void cutPoint(){

    }

    @Before("cutPoint()")
    public void beforeCut(){
        System.out.println("before point cut in UserService");
    }

    @After("cutPoint()")
    public void afterCut(){
        System.out.println("after pointCut in UserService");
    }

}
