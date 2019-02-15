package com.aop.demo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/14.
 */
@Aspect
public class AspectJTest {

    @Pointcut("execution(* *.test(..))")
    public void test(){

    }

    @Before("test()")
    public void beforeTest(){
        System.out.println("before test");
    }

    @After("test()")
    public void afterTest(){
        System.out.println("after test");
    }

    @Around("test()")
    public Object aroudTest(ProceedingJoinPoint joinPoint){

        System.out.println("around before");

        Object object =null;
        try {
            object = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.out.println("around after");

        return object;
    }


}
