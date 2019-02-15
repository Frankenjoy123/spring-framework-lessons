package com.conponentscan.demo;

import com.conponentscan.demo.beans.Lesson;
import com.conponentscan.demo.beans.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");


//        User user = context.getBean(User.class);
//        user.hello();

        Lesson lesson = context.getBean(Lesson.class);
        lesson.learning();

    }
}
