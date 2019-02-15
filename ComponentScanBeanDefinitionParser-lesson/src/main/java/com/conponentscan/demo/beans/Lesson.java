package com.conponentscan.demo.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/12.
 */
@Component
public class Lesson {

    @Autowired
    private User user;

    public void learning(){
        user.hello();
    }

}
