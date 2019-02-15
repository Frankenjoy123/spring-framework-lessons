package com.customer.tags;

import com.customer.tags.bean.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/12.
 */
public class BootStrap {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:/spring-context.xml");
        User user = (User) context.getBean("user");

        System.out.println(user);
    }
}
