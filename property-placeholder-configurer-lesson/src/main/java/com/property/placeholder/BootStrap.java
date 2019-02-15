package com.property.placeholder;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/11.
 */
public class BootStrap {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");

        StudentService studentService = (StudentService) context.getBean("student");
        System.out.println(studentService.getName());

    }
}
