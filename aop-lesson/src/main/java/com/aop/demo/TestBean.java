package com.aop.demo;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/14.
 */
public class TestBean {

    private String testStr = "hello world";

    public String getTestStr() {
        return testStr;
    }

    public void setTestStr(String testStr) {
        this.testStr = testStr;
    }

    public void test(){
        System.out.println(testStr);
    }

}
