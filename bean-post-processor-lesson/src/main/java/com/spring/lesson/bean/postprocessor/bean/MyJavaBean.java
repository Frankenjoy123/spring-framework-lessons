package com.spring.lesson.bean.postprocessor.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
@Component
public class MyJavaBean implements InitializingBean{

    @Value("my desc")
    private String desc;
    @Value("my remark")
    private String remark;

    public MyJavaBean() {
        System.out.println("MyJavaBean的构造函数被执行啦");
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        System.out.println("调用setDesc方法");
        this.desc = desc;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        System.out.println("调用setRemark方法");
        this.remark = remark;
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("调用afterPropertiesSet方法");
//        this.desc = "在afterPropertiesSet 初始化方法中修改之后的描述信息";
    }

    @Override
    public String toString() {
        return "MyJavaBean{" +
                "desc='" + desc + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
