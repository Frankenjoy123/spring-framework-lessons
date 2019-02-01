package com.spring.lesson.event;

import com.spring.lesson.event.event.MyEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");

        MyEvent myEvent = new MyEvent(context,"hello-world");

        context.publishEvent(myEvent);

    }
}
