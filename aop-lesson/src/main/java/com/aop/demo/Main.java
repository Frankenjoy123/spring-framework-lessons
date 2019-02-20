package com.aop.demo;

import com.aop.demo.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");


        TestBean testBean = (TestBean) context.getBean("testBean");
        testBean.test();

        UserService userService = (UserService) context.getBean("userService");
        userService.add();

    }
}
