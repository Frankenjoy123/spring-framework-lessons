package com.spring.lesson.bean.postprocessor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 模拟从远程取文件：
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/1.
 */
@Component
public class MyPlaceHolderBeanFactoryPostProcessor extends PropertyPlaceholderConfigurer implements InitializingBean{

    public void afterPropertiesSet() throws Exception {

        List<Properties> list = new ArrayList<Properties>();
        Properties p = PropertiesLoaderUtils.loadAllProperties("config.properties");
        list.add(p);
        //这里是关键，这就设置了我们远程取得的List<Properties>列表
        setPropertiesArray(list.toArray(new Properties[list.size()]));
    }
}
