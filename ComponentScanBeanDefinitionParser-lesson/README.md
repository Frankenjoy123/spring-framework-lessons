罗列下context:component-scan可填的基础属性
```java
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

    private static final String RESOURCE_PATTERN_ATTRIBUTE = "resource-pattern";

    private static final String USE_DEFAULT_FILTERS_ATTRIBUTE = "use-default-filters";

    private static final String ANNOTATION_CONFIG_ATTRIBUTE = "annotation-config";

    private static final String NAME_GENERATOR_ATTRIBUTE = "name-generator";

    private static final String SCOPE_RESOLVER_ATTRIBUTE = "scope-resolver";

    private static final String SCOPED_PROXY_ATTRIBUTE = "scoped-proxy";

    private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";

    private static final String INCLUDE_FILTER_ELEMENT = "include-filter";

    private static final String FILTER_TYPE_ATTRIBUTE = "type";

    private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";
```

ComponentScanBeanDefinitionParser#parse()-主方法
统一接口parse()方法，看下总体逻辑，代码如下
```java
//@Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        //解析base-package属性值，扫描的包可以,;分隔
        String[] basePackages = StringUtils.tokenizeToStringArray(element.getAttribute(BASE_PACKAGE_ATTRIBUTE),         ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

        // Actually scan for bean definitions and register them.
        ClassPathBeanDefinitionScanner scanner = configureScanner(parserContext, element);
        //通过ClassPathBeanDefinitionScanner扫描类来获取包名下的所有class并将他们注册到spring的bean工厂中
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);
        //注册其他注解组件
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);

        return null;
    }
```

我们关注下ComponentScanBeanDefinitionParser#configureScanner()创建扫描器操作和ClassPathBeanDefinitionScanner#doScan()扫描包方法

ComponentScanBeanDefinitionParser#configureScanner()-创建扫描器
观察下如何创建扫描器，以及相关的初始操作，代码奉上

```java
protected ClassPathBeanDefinitionScanner configureScanner(ParserContext parserContext, Element element) {
        XmlReaderContext readerContext = parserContext.getReaderContext();
        //默认使用spring自带的注解过滤
        boolean useDefaultFilters = true;
        //解析`use-default-filters`，类型为boolean
        if (element.hasAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE)) {
            useDefaultFilters = Boolean.valueOf(element.getAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE));
        }

        // Delegate bean definition registration to scanner class.
        //此处如果`use-default-filters`为true，则添加`@Component`、`@Service`、`@Controller`、`@Repository`、`@ManagedBean`、`@Named`添加到includeFilters的集合过滤
        ClassPathBeanDefinitionScanner scanner = createScanner(readerContext, useDefaultFilters);
        scanner.setResourceLoader(readerContext.getResourceLoader());
        scanner.setEnvironment(parserContext.getDelegate().getEnvironment());
        scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
        scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());
        //设置`resource-pattern`属性，扫描资源的模式匹配，支持正则表达式
        if (element.hasAttribute(RESOURCE_PATTERN_ATTRIBUTE)) {         
                    scanner.setResourcePattern(element.getAttribute(RESOURCE_PATTERN_ATTRIBUTE));
        }

        try {
            //解析name-generator属性 beanName生成器
            parseBeanNameGenerator(element, scanner);
        }
        catch (Exception ex) {
            readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
        }

        try {
            //解析scope-resolver属性和scoped-proxy属性，但两者只可存在其一
            //后者值为targetClass：cglib代理、interfaces：JDK代理、no：不使用代理
            parseScope(element, scanner);
        }
        catch (Exception ex) {
            readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
        }
        //解析子节点`context:include-filter`、`context:exclude-filter`主要用于对扫描class类的过滤
               //例如<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller.RestController" />
        parseTypeFilters(element, scanner, readerContext, parserContext);

        return scanner;
    }
```

此处只简单的罗列了如何创建一个文件扫描器以及相关的初始操作，具体的读者可自行去阅读分析

ClassPathBeanDefinitionScanner#doScan()-扫描操作
真实扫描base-package指定的目录并返回注册的所有beanDefinition，具体的扫描简析如下

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //表明base-package属性是需要被指定的
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
        for (String basePackage : basePackages) {
            //对每个基础包都进行扫描寻找并且对基础包下的所有class都注册为BeanDefinition
                        /**
                        **
                        **并对得到的candidates集合进行过滤，此处便用到include-filters和exclude-filters
                        */
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                //解析一个bean的scope属性，代表作用范围
                //prototype->每次请求都创建新的对象 singleton->单例模式，处理多请求
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                //使用beanName生成器生成
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                /**
                **对注册的bean进行另外的赋值处理，比如默认属性的配置
                *返回的candidate类型为ScannedGenericBeanDefinition，下面两者
                *条件满足
                */
                if (candidate instanceof AbstractBeanDefinition) {
                    //设置lazy-init/autowire-code默认属性，从spring配置的<beans>节点属性读取
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    //读取bean上的注解，比如`@Lazy`、`@Dependson`的值设置相应的属性
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                //查看是否已注册
                if (checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    //默认采取cglib来做代理
                    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    beanDefinitions.add(definitionHolder);
                    //注册bean信息到工厂中
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        return beanDefinitions;
    }
```

在这里我们只简单的看下其父类ClassPathScanningCandidateComponentProvider#findCandidateComponents获取包下的所有class资源文件并实例化为BeanDefinition对象

ClassPathScanningCandidateComponentProvider#findCandidateComponents()-找寻符合条件的资源文件
扫描包下的所有class文件并对其进行过滤，过滤的条件为includeFilters和excludeFilters集合。代码简析如下

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        try {
            //值类似为classpath*:com/question/sky/**/*.class
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +resolveBasePackage(basePackage) + "/" + this.resourcePattern;
            //通过PathMatchingResourcePatternResolver来找寻资源
            //常用的Resource为FileSystemResource
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            boolean traceEnabled = logger.isTraceEnabled();
            boolean debugEnabled = logger.isDebugEnabled();
            for (Resource resource : resources) {

                if (resource.isReadable()) {
                    try {
                        //生成MetadataReader对象->SimpleMetadataReader，内部包含AnnotationMetadataReadingVisitor注解访问处理类
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                        //判断class是否不属于excludeFilters集合内但至少符合一个includeFilters集合
                        if (isCandidateComponent(metadataReader)) {
                            //包装为ScannedGenericBeanDefinition对象
                            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                            //保存文件资源
                            sbd.setResource(resource);
                            sbd.setSource(resource);
                            //判断class文件是否不为接口或者抽象类并且是独立的
                            if (isCandidateComponent(sbd)) {
                                //完成验证加入集合中
                                candidates.add(sbd);
                            }
                        }
                    }
                    catch (Throwable ex) {
                        throw new BeanDefinitionStoreException(
                                "Failed to read candidate component class: " + resource, ex);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return candidates;
    }
```

对上面的代码解释作下补充，主要是验证beanDefinition的两个方法

ClassPathScanningCandidateComponentProvider#isCandidateComponent(MetadataReader metadataReader)
对class类进行filter集合过滤
```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            //满足excludeFilter集合中的一个便返回false，表示不对对应的beanDefinition注册
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            //首先满足其中includeFilter集合中的一个
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                //判断对应的beanDifinition不存在@Conditional注解或者满足@Conditional中指定的条件，则返回true
                //@Conditional注解的使用可自行查看相关资料
                return isConditionMatch(metadataReader);
            }
        }
        return false;
    }
```

ClassPathScanningCandidateComponentProvider#isCandidateComponent(AnnotatedBeanDefinition beanDefinition)
验证beanDefinition class类是否为具体类
```java
protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        //非抽象类、接口类并且有独立特性[它是一个顶级类还是一个嵌套类(静态内部类)，可以独立于封闭类构造。]
        return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
    }
```

ComponentScanBeanDefinitionParser#registerComponents-注册其他组件
在扫描包内的class文件注册为beanDefinition之后，ComponentScanBeanDefinitionParser还需要注册其他的组件，具体是什么可简单看下相关的源码

```java
protected void registerComponents(
            XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element) {

        Object source = readerContext.extractSource(element);
        //包装为CompositeComponentDefinition对象，内置多ComponentDefinition对象
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
        //将已注册的所有beanDefinitionHolder对象放到上述对象中
        for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
        }

        // Register annotation config processors, if necessary.
        boolean annotationConfig = true;
        //获取annotation-config的属性值，默认为true
        if (element.hasAttribute(ANNOTATION_CONFIG_ATTRIBUTE)) {
            annotationConfig = Boolean.valueOf(element.getAttribute(ANNOTATION_CONFIG_ATTRIBUTE));
        }
        if (annotationConfig) {
            //注册多个BeanPostProcessor接口，具体什么可自行查看，返回的是包含BeanPostProcessor接口的beanDefinitionHolder对象集合
            Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
            //继续装入CompositeComponentDefinition对象
            for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
                compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
            }
        }
        //此处为空
        readerContext.fireComponentRegistered(compositeDef);
    }
```

此处的目的主要是注册多个BeanPostProcessor接口实现类【供后续spring调用统一接口进行解析，比如>>>Spring源码情操陶冶-AbstractApplicationContext#invokeBeanFactoryPostProcessors可执行下述的@Configuration解析】具体的有

ConfigurationClassPostProcessor解析@Configuration注解类
AutowiredAnnotationBeanPostProcessor解析@Autowired/@Value注解
RequiredAnnotationBeanPostProcessor解析@Required注解
CommonAnnotationBeanPostProcessor解析@Resource注解
PersistenceAnnotationBeanPostProcessor解析JPA注解，持久层


## 区别

<context:annotation-config/>
    在基于主机方式配置Spring时,Spring配置文件applicationContext.xml,你可能会见<context:annotation-config/>这样一条配置，它的作用是隐式的向Spring容器注册
                           AutowiredAnnotationBeanPostProcessor,
                           CommonAnnotationBeanPostProcessor,
                           PersistenceAnnotationBeanPostProcessor,
                           RequiredAnnotationBeanPostProcessor 
 这4个BeanPostProcessor.注册这4个bean处理器主要的作用是为了你的系统能够识别相应的注解。                        
 例如：
 如果想使用@Autowired注解，需要在Spring容器中声明AutowiredAnnotationBeanPostProcessor Bean。传统的声明方式：<bean class="org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor"/>
如果想使用@PersistenceContext注解，需要在Spring容器中声明PersistenceAnnotationBeanPostProcessor Bean。传统的声明：<bean class="org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor"/>
如果想使用@Required注解，需要在Spring容器中声明RequiredAnnotationBeanPostProcessor Bean。传统声明方式：<bean class="org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor"/>
如果想使用@Resource、@ PostConstruct、@ PreDestroy等注解就必须声明CommonAnnotationBeanPostProcessor。传统申明方式： <bean class="org.springframework.beans.factory.annotation.CommonAnnotationBeanPostProcessor"/>
所以，如果按照传统声明一条一条去声明注解Bean，就会显得十分繁琐。
因此如果在Spring的配置文件中事先加上<context:annotation-config/>这样一条配置的话，那么所有注解的传统声明就可以被 忽略，即不用在写传统的声明，Spring会自动完成声明。

<context:component-scan base-package="com.xx.xx" /> 
   <context:component-scan/>的作用是让Bean定义注解工作起来,也就是上述传统声明方式。 它的base-package属性指定了需要扫描的类包，类包及其递归子包中所有的类都会被处理。

     值得注意的是<context:component-scan/>不但启用了对类包进行扫描以实施注释驱动 Bean 定义的功能，同时还启用了注释驱动自动注入的功能（即还隐式地在内部注册了 AutowiredAnnotationBeanPostProcessor 和  CommonAnnotationBeanPostProcessor），因此当使用 <context:component-scan/> 后，就可以将 <context:annotation-config/> 移除了。
 
 @Autowired可以对成员变量、方法和构造函数进行标注，来完成自动装配的工作。@Autowired的标注位置不同，它们都会在Spring在初始化这个bean时，自动装配这个属性。注解之后就不需要set/get方法了。
注意：如果有多个配置文件，在最顶层的配置文件（启动类所在的配置文件）中加入<context:component-scan base-package="com.xx.xx" /> 。（如controller层的配置文件中）
<mvc:annotation-driven />
它会自动注册DefaultAnnotationHandlerMapping 与AnnotationMethodHandlerAdapter

结论：在spring-servlet.xml中只需要扫描所有带@Controller注解的类，在applicationContext中可以扫描所有其他带有注解的类（也可以过滤掉带@Controller注解的类）。