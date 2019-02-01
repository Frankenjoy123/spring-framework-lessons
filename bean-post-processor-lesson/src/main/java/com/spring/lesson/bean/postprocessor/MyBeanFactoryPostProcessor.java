package com.spring.lesson.bean.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {


        BeanDefinition bd = beanFactory.getBeanDefinition("person");

        MutablePropertyValues values = bd.getPropertyValues();

        PropertyValue propertyValue =  values.getPropertyValue("name");

        if (propertyValue.getValue() instanceof TypedStringValue){

            TypedStringValue value = (TypedStringValue) propertyValue.getValue();

            String oldName = value.getValue();

            String newName = oldName + " : from beanFactoryPostProcessor! ";

            value.setValue(newName);
        }

        System.out.println("jjj");

    }
}
