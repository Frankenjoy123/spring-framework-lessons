

## AopNamespaceHandler 注册阶段


```java
		registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
```

```java
class AspectJAutoProxyBeanDefinitionParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
		extendBeanDefinition(element, parserContext);
		return null;
	}
	...
}
```

AopNamespaceUtils.java
```java
	public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(
			ParserContext parserContext, Element sourceElement) {

		BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(
				parserContext.getRegistry(), parserContext.extractSource(sourceElement));
		useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
		registerComponentIfNecessary(beanDefinition, parserContext);
	}
```

## getBean 得到 proxy的流程
```java
ClassPathXmlApplicationContext.ClassPathXmlApplicationContext(java.lang.String)

AbstractApplicationContext.refresh
// Instantiate all remaining (non-lazy-init) singletons.
finishBeanFactoryInitialization(beanFactory);

//Instantiate all remaining (non-lazy-init) singletons.
DefaultListableBeanFactory.preInstantiateSingletons

AbstractBeanFactory.doGetBean

	protected <T> T doGetBean(
			final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly)
			throws BeansException {

...

				// Create bean instance.
				if (mbd.isSingleton()) {
					sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
						public Object getObject() throws BeansException {
							try {
								return createBean(beanName, mbd, args);
							}
							catch (BeansException ex) {
							}
						}
					});
...
		return (T) bean;
	}
	
	
AbstractAutowireCapableBeanFactory.createBean(String, RootBeanDefinition, Object[])	

		try {
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			Object bean = resolveBeforeInstantiation(beanName, mbd);
			if (bean != null) {
				return bean;
			}
		}
		...
		Object beanInstance = doCreateBean(beanName, mbd, args);

		return beanInstance;
		
AbstractAutowireCapableBeanFactory.doCreateBean

		// Initialize the bean instance.
		Object exposedObject = bean;
		try {
			populateBean(beanName, mbd, instanceWrapper);
			if (exposedObject != null) {
				exposedObject = initializeBean(beanName, exposedObject, mbd);
			}
		}
		

AbstractAutowireCapableBeanFactory.initializeBean(String, Object, RootBeanDefinition)
		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}

		try {
			invokeInitMethods(beanName, wrappedBean, mbd);
		}
...
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}
		return wrappedBean;
		
```

## 执行`AnnotationAwareAspectJAutoProxyCreator#postProcessAfterInitialization`

```java
AbstractAutoProxyCreator.postProcessAfterInitialization
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean != null) {
			Object cacheKey = getCacheKey(bean.getClass(), beanName);
			if (!this.earlyProxyReferences.containsKey(cacheKey)) {
				return wrapIfNecessary(bean, beanName, cacheKey);
			}
		}
		return bean;
	}
	
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
...
		// Create proxy if we have advice.
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
		if (specificInterceptors != DO_NOT_PROXY) {
			this.advisedBeans.put(cacheKey, Boolean.TRUE);
			Object proxy = createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			this.proxyTypes.put(cacheKey, proxy.getClass());
			return proxy;
		}

		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}	
```


```java
	protected Object createProxy(
			Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {

		ProxyFactory proxyFactory = new ProxyFactory();
		// Copy our properties (proxyTargetClass etc) inherited from ProxyConfig.
		proxyFactory.copyFrom(this);

		if (!shouldProxyTargetClass(beanClass, beanName)) {
			// Must allow for introductions; can't just set interfaces to
			// the target's interfaces only.
			Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, this.proxyClassLoader);
			for (Class<?> targetInterface : targetInterfaces) {
				proxyFactory.addInterface(targetInterface);
			}
		}

		Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
		for (Advisor advisor : advisors) {
			proxyFactory.addAdvisor(advisor);
		}

		proxyFactory.setTargetSource(targetSource);
		customizeProxyFactory(proxyFactory);

		proxyFactory.setFrozen(this.freezeProxy);
		if (advisorsPreFiltered()) {
			proxyFactory.setPreFiltered(true);
		}

		return proxyFactory.getProxy(this.proxyClassLoader);
	}
```

* `ProxyFactory`
```
	public Object getProxy(ClassLoader classLoader) {
		return createAopProxy().getProxy(classLoader);
	}
	
ProxyCreatorSupport.createAopProxy	
		protected final synchronized AopProxy createAopProxy() {
    		if (!this.active) {
    			activate();
    		}
    		return getAopProxyFactory().createAopProxy(this);
    	}
    	

DefaultAopProxyFactory.createAopProxy
// 进行AopProxy的选择
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface()) {
				return new JdkDynamicAopProxy(config);
			}
			return CglibProxyFactory.createCglibProxy(config);
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}    	
		
```

## `CglibAopProxy.getProxy(java.lang.ClassLoader)`

```
	public Object getProxy(ClassLoader classLoader) {

		try {
			Class<?> rootClass = this.advised.getTargetClass();
			Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

			Class<?> proxySuperClass = rootClass;
			if (ClassUtils.isCglibProxyClass(rootClass)) {
				proxySuperClass = rootClass.getSuperclass();
				Class<?>[] additionalInterfaces = rootClass.getInterfaces();
				for (Class<?> additionalInterface : additionalInterfaces) {
					this.advised.addInterface(additionalInterface);
				}
			}

			// Validate the class, writing log messages as necessary.
			validateClassIfNecessary(proxySuperClass);

			// Configure CGLIB Enhancer...
			Enhancer enhancer = createEnhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);
				if (classLoader instanceof SmartClassLoader &&
						((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
					enhancer.setUseCache(false);
				}
			}
			enhancer.setSuperclass(proxySuperClass);
			enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
			enhancer.setStrategy(new MemorySafeUndeclaredThrowableStrategy(UndeclaredThrowableException.class));
			enhancer.setInterceptDuringConstruction(false);

			Callback[] callbacks = getCallbacks(rootClass);
			Class<?>[] types = new Class<?>[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			enhancer.setCallbackFilter(new ProxyCallbackFilter(
					this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
			enhancer.setCallbackTypes(types);
			enhancer.setCallbacks(callbacks);

			// Generate the proxy class and create a proxy instance.
			Object proxy;
			if (this.constructorArgs != null) {
				proxy = enhancer.create(this.constructorArgTypes, this.constructorArgs);
			}
			else {
				proxy = enhancer.create();
			}

			return proxy;
		}
...
	}
```

## AOP相关接口

```
Advised  -> Advisor

MethodInteceptor

```

## CglibAopProxy类
```
org.springframework.cglib.proxy.MethodInterceptor接口
    DynamicAdvisedInterceptor

org.springframework.aop.ProxyMethodInvocation 接口
    CglibMethodInvocation extends ReflectiveMethodInvocation


```

## execution 表达式

```
execution(* com.loongshawn.method.ces..*.*(..))
```

| 标识符                    | 含义                                          |      |
| ------------------------- | --------------------------------------------- | ---- |
| execution()               | 表达式的主体                                  |      |
| 第一个“*”符号             | 表示返回值的类型任意                          |      |
| com.loongshawn.method.ces | AOP所切的服务的包名，即，需要进行横切的业务类 |      |
| 包名后面的“..”            | 表示当前包及子包                              |      |
| 第二个“*”表示类名         | 即所有类                                      |      |
| *(..)	表示任何方法名   | 括号表示参数，两个点表示任何参数类型          |      |

```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) 
throws-pattern?)
```

上述表达式结构是Spring官方文档说明，翻译为中文如下，其中除了返回类型模式、方法名模式和参数模式外，其它项都是可选的。

```
execution(<修饰符模式>?<返回类型模式><方法名模式>(<参数模式>)<异常模式>?)
```

```
修饰符模式  public 可选
返回值 必须  * 
类  class 可选
方法 必须 *
参数 必须 ..
异常模式 可选
```

> **execution**：用于匹配执行方法的连接点，是Spring AOP最主要的切入点指示符，execution表达式的格式如下：

```
execution(modifies-pattern?  ret-type-pattern  declaring-type-parttern?  name--pattern(parm-pattern)  throws-pattern?)
```

以上打了问号的都可以省略。

> 上面格式中的execution是不变的，用于作为execution表达式的开头，整个表示式各个部分的解释为：

> **modifies-pattern**：指定方法的修饰符，支持通配符，该部分可以省略。

> **ret-type-pattern**：指定方法的返回值类型，支持通配符，可以使用“*”通配符来匹配所有返回值类型。

> **declaring-type-parttern**：指定方法所属的类，支持通配符，该部分可以省略。

> **name--pattern**：指定匹配指定方法名，支持通配符，可以使用“*”通配符来匹配所有方法。

> **parm-pattern**：指定方法声明中的形参列表，支持两个通配符：“*”、“..”，其中*表示一个任意类型的参数，而“..”表示零个或多个任意类型的参数。

> **throws-pattern**：指定方法声明抛出的异常，支持通配符，该部分可以省略。

> 例如下面几个execution表达式：

```
//匹配任意public方法的执行。
execution(public * *(..))
//匹配任意方法名以set开始的方法。
execution(* set*(..))
//匹配AccountService里定义的任意方法的执行。
execution(* org.hb.AccountService.*(..))
//匹配Service包中任意类的任意方法的执行。
execution(* org.hb.service.*.*(..))
```