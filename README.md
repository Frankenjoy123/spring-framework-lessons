
# `BeanFactoryPostProcessor`
* 对于`DefaultListBeanFactroy`来说，在所有的bean示例化之前进行的，可以操作`BeanDefinition`。

* `new ClassPathXmlApplicationContext("classpath:/spring-context.xml")`
    * `AbstractApplicationContext`的`refresh`方法
        

```java
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing. 
			// 加载环境配置
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
			// 获取DefaultListBeanFactory，注册DeanDefinition
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			// 为beanFactory配置ApplicationContextAwareProcessor等
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				// 空实现，需要子类继续，自己实现
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				// 获取所有的BeanFactoryPostProcessor，优先级排序，并调用postProcessBeanFactory方法
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
				// 注册所有的BeanPostProcessor，优先级排序；调用方法，在getBean时触发。
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				initMessageSource();

				// Initialize event multicaster for this context.
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				onRefresh();

				// Check for listener beans and register them.
				registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
				// 非lasy的单例bean，实例化，getBean操作
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				finishRefresh();
			}
```


# `BeanPostProcessor`

* 在实例化bean的时候调用

* 参考`AbstractAutowireCapableBeanFactory` 的`doCreateBean`方法

**过程** 
1. `createBeanInstance`创建bean实例
2. `populateBean`，对bean填充，属性填充，依赖注入
3. `initializeBean`,执行`BeanPostProcessor`的方法，以及`InitializaitonBean`的方法，`init-method`方法

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
		// Instantiate the bean.
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);
		Class<?> beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);

		...

		...

		// Initialize the bean instance.
		Object exposedObject = bean;
		try {
			populateBean(beanName, mbd, instanceWrapper);
			if (exposedObject != null) {
				exposedObject = initializeBean(beanName, exposedObject, mbd);
			}
		}
		catch (Throwable ex) {
			...
		}
		...
		return exposedObject;
	}
```


* 参考 `AbstractAutowireCapableBeanFactory` 的`initializeBean`方法

**过程**
1. applyBeanPostProcessorsBeforeInitialization
2. invokeInitMethods 调用实现了`InitializationBean`的方法，和配置的xml的`init-method`方法
3. applyBeanPostProcessorsAfterInitialization

```java
	protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					invokeAwareMethods(beanName, bean);
					return null;
				}
			}, getAccessControlContext());
		}
		else {
			invokeAwareMethods(beanName, bean);
		}

		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
//			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}

		try {
			invokeInitMethods(beanName, wrappedBean, mbd);
		}
		catch (Throwable ex) {
			...
		}

		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}
		return wrappedBean;
	}
```