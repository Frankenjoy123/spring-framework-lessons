

## ApplicationEventMulticaster

主要实现 `SimpleApplicationEventMulticaster`


* 观察者模式，添加删除监听器，发布事件
```java
public interface ApplicationEventMulticaster {

	void addApplicationListener(ApplicationListener listener);

	void addApplicationListenerBean(String listenerBeanName);

	void removeApplicationListener(ApplicationListener listener);

	void removeApplicationListenerBean(String listenerBeanName);

	void removeAllListeners();

	void multicastEvent(ApplicationEvent event);

}
```

* `AbstractApplicationContext`的`publishEvent`方法
```java
	public void publishEvent(ApplicationEvent event) {
        ...
		getApplicationEventMulticaster().multicastEvent(event);
		if (this.parent != null) {
			this.parent.publishEvent(event);
		}
	}
```