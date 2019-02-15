package com.customer.tags.parser;

import com.customer.tags.bean.User;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/12.
 */
public class UserBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{


    // element对应的类
    @Override
    protected Class<?> getBeanClass(Element element) {
        return User.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String userName = element.getAttribute("userName");
        String email = element.getAttribute("email");

        builder.addPropertyValue("userName",userName);
        builder.addPropertyValue("email",email);
    }
}
