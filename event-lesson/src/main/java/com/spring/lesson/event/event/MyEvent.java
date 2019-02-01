package com.spring.lesson.event.event;


import org.springframework.context.ApplicationEvent;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
public class MyEvent extends ApplicationEvent {


    private String msg;


    public MyEvent(Object source) {
        super(source);
    }

    public MyEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public void print(){
        System.out.println(this.msg);
    }
}
