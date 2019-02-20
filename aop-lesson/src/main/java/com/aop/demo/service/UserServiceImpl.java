package com.aop.demo.service;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/15.
 */
public class UserServiceImpl implements UserService{
    @Override
    public void add() {

        System.out.println("invoke add 1 + 1");

    }
}
