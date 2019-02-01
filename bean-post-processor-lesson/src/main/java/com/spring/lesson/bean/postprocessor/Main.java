package com.spring.lesson.bean.postprocessor;

import com.spring.lesson.bean.postprocessor.bean.MyJavaBean;
import com.spring.lesson.bean.postprocessor.bean.Person;
import com.spring.lesson.bean.postprocessor.bean.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
public class Main {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");


        Person person = (Person) context.getBean("person");

        System.out.println(person);

        MyJavaBean myJavaBean = (MyJavaBean) context.getBean("myJavaBean");
        System.out.println(myJavaBean);

        User user = (User) context.getBean("user");
        System.out.println(user);

    }
}
