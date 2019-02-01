package com.spring.lesson.bean.postprocessor;

import com.spring.lesson.bean.postprocessor.bean.MyJavaBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor{

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor，对象" + beanName + "调用postProcessBeforeInitialization的数据： " + bean.toString());


        if (bean instanceof MyJavaBean){
            MyJavaBean myJavaBean = (MyJavaBean) bean;

            myJavaBean.setDesc(myJavaBean.getDesc() + " from beanPostProcessor");
        }

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor，对象" + beanName + "调用postProcessAfterInitialization的数据：" + bean.toString());
        return bean;
    }
}
