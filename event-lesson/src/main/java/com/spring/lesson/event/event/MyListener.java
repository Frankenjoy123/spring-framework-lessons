package com.spring.lesson.event.event;

import com.spring.lesson.event.event.MyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
@Component
public class MyListener implements ApplicationListener<MyEvent> {

    public void onApplicationEvent(MyEvent event) {
        event.print();
    }
}
