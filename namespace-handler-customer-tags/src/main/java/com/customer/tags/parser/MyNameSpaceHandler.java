package com.customer.tags.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/12.
 */
public class MyNameSpaceHandler extends NamespaceHandlerSupport{

    @Override
    public void init() {
        registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
    }
}
