# tomcat 网络模型简易解析

### 环境

base on spring-boot-starter-web:2.6.7 embedded tomcat

### 调试入口

启动两个 `server.port` 相同的springboot应用会报端口占用的错误

![tomcat#1](resources/2022-06-19_11-08-26.png)

我们在这个异常类的构造方法上打一个断点，这就是我们的调试入口

![tomcat#2](resources/2022-06-19_11-11-36.png)

debug模式启动后（后面启动报错的程序），通过堆栈日志我们可以看到这个异常是由 `org.springframework.boot.web.embedded.tomcat.TomcatWebServer` 抛出的

![tomcat#3](resources/2022-06-19_11-17-25.png)

### `TomcatWebServer`

前面我们讲到了端口占用的异常是由 `TomcatWebServer` 抛出的，我们可以从前面的堆栈图看到 `TomcatWebServer` 的 `start` 方法。

但是光看start方法好像什么都看不出来，也是无从下手，除非每一个方法debug进去看一下。其实这里还是得回到 `PortInUseException` ，我们看一下它的 `stackTrace` ：

![tomcat#4](resources/2022-06-19_11-49-01.png)

它的 `stackTrace` 栈最顶部帧是 `org.apache.catalina.core.StandardService.addConnector`

我们看一下这个方法：

![tomcat#5](resources/2022-06-19_11-54-32.png)

很明显我们应该从 `connector.start()` 入手

![tomcat#6](resources/2022-06-19_11-57-30.png)

继续看一下 `startInternal()` 方法：

![tomcat#7](resources/2022-06-19_11-59-39.png)

沿着图片红圈的路径一直走：

![tomcat#8](resources/2022-06-19_12-02-37.png)

![tomcat#9](resources/2022-06-19_12-04-25.png)

![tomcat#10](resources/2022-06-19_12-04-50.png)

我们终于看到与网络模型有关的 `bind()` 了

上面的路径如果还是觉得无从入手，也是可以通过查看异常 `stackTrace` 解决的，比如在 `org.apache.catalina.core.StandardService.addConnector` 时：

![tomcat#11](resources/2022-06-19_12-12-25.png)

### 监听

因为tomcat默认使用的是 `org.apache.tomcat.util.net.NioEndpoint` ，我们来看一下它的 `bind`  方法

![tomcat#12](resources/2022-06-20_15-11-35.png)

第一行很明显是初始化 `ServerSocket` ，我们进去看看：

![tomcat#13](resources/2022-06-20_15-13-43.png)

果然发现了 `ServerSocket` 的绑定，但是注意这里还有个 `getAcceptCount()` 方法，通过查看源码可以得知，该方法设置的是**backlog，并且它在tomcat中的默认值为100**（backlog是指一个决定内核为服务端socket维护的队列的容量的参数，连接队列是指操作系统为服务端socket维护的用于保存等待被服务端socket调用取走的队列）

![tomcat#14](resources/2022-06-20_15-19-58.png)

回到 `org.apache.tomcat.util.net.AbstractEndpoint.start()`方法 ，我们看一下 `org.apache.tomcat.util.net.NioEndpoint.startInternal` 方法的执行：

![tomcat#15](resources/2022-06-20_15-33-29.png)

![tomcat#16](resources/2022-06-20_15-35-17.png)

前面设置一些属性就不仔细看了， `Poller` 这部分后面再讲，我们来看 `org.apache.tomcat.util.net.AbstractEndpoint.startAcceptorThread` 方法：

![tomcat#17](resources/2022-06-20_15-38-20.png)

这里我们可以看到启动了**一个监听线程** ，我们继续看一下 `Acceptor` 的实现，因为它是一个 `Runnable` ，我们主要来看它的 `run` 方法

![tomcat#18](resources/2022-06-20_15-43-26.png)

首先正常情况下，监听线程会无线循环执行里面的逻辑

其次达到最大连接数时，线程会等待

![tomcat#19](resources/2022-06-20_15-53-10.png)

![tomcat#20](resources/2022-06-20_15-54-19.png)

并且可以得知**最大连接数是8192**

![tomcat#21](resources/2022-06-20_15-55-13.png)

然后线程会调用 `ServerSocket` 的accept

![tomcat#22](resources/2022-06-20_16-00-43.png)

![tomcat#23](resources/2022-06-20_16-04-18.png)

**这里的accept是直接阻塞的，不是像其它的一些模型一样是等探测到 `OP_ACCEPT` 事件后才accept**

我们这时触发客户端连接，可以发现**在`Acceptor` 线程的 `run` 方法的这一步与 `Poller` 线程产生了交互**

![tomcat#34](resources/2022-06-20_21-50-02.png)

![tomcat#35](resources/2022-06-20_21-52-16.png)

`Acceptor` 线程向 `Poller` 线程注册了这个socket，并产生了读事件存放到事件队列中

![tomcat#36](resources/2022-06-20_21-59-04.png)

![tomcat#37](resources/2022-06-20_22-00-13.png)

### IO监听

现在目光回到 `org.apache.tomcat.util.net.NioEndpoint.startInternal`

![tomcat#24](resources/2022-06-20_16-22-23.png)

上图红圈处创建了**一个IO监听线程**， `Poller` 也是一个 `Runnable` ，同样我们也是从它的 `run` 方法入手

![tomcat#25](resources/2022-06-20_20-56-41.png)

上面红圈中的 `events` 方法会判断是否有读事件发生

![tomcat#26](resources/2022-06-20_20-59-12.png)

上面红圈中的 `events` 是一个存放事件的队列，而 `events` 方法会对队列中的事件进行遍历，如果是读事件就把 `Channel` 注册到 `Selector` 中。我们回到 `Poller` 的 `run` 方法

![tomcat#27](resources/2022-06-20_21-06-23.png)

上图是在计算有几个socket是满足条件的，然后 `Poller` 线程会对满足条件的socket进行遍历处理

![tomcat#28](resources/2022-06-20_21-09-26.png)

![tomcat#29](resources/2022-06-20_21-14-53.png)

![tomcat#30](resources/2022-06-20_21-24-17.png)

在处理socket时， `Poller` 线程首先会把当前socket从 `Selector` 中取消注册，如果是可读的话，会把当前socket抛给业务线程池 `Executor` 进行处理

### IO读

还是回到 `org.apache.tomcat.util.net.NioEndpoint.startInternal` 方法

![tomcat#31](resources/2022-06-20_21-31-08.png)

其实在创建 `Poller` 线程前，业务线程池也被创建了，这里有几个线程池的参数挺重要的

![tomcat#32](resources/2022-06-20_21-34-12.png)

**核心线程数 `corePoolSize` 默认值为10，最大线程数 `maximumPoolSize` 默认值为200**

![tomcat#33](resources/2022-06-20_21-39-12.png)

前面我们提到过 `Poller` 线程会把当前socket抛给业务线程池，我们来看一下：

![tomcat#38](resources/2022-06-20_22-08-10.png)

这里可以看到 `Poller` 线程把 `org.apache.tomcat.util.net.SocketProcessorBase` 提交给了线程池，查看 `SocketProcessorBase` 的源码可以知道它是一个 `Runnable` ，所以业务线程池的执行我们从它的 `run` 方法入手

![tomcat#39](resources/2022-06-20_22-11-20.png)

![tomcat#40](resources/2022-06-21_21-19-21.png)

![tomcat#41](resources/2022-06-21_21-20-32.png)

其实到这里之后，就已经挺难判断哪里才是我们应该看的地方了，但是我们可以通过 `SocketWrapperBase` 这个类入手，因为它被作为参数传递给了 `org.apache.coyote.AbstractProtocol.ConnectionHandler.process` 。这里 `SocketWrapperBase` 的实际类型很容易可以得知是 `org.apache.tomcat.util.net.NioChannel` 类型的（通过查看是谁调用 `SocketWrapperBase` 的构造方法得知）

![tomcat#42](resources/2022-06-21_21-29-41.png)

所以我们不妨在 `NioChannel` 的 `read` 方法上打一个断点

![tomcat#43](resources/2022-06-21_21-40-20.png)

这表明**tomcat nio 网络模型的IO读是在业务线程池完成的**

### IO写

业务逻辑的执行自然是在业务线程池，IO写我们同样可以在 `NioChannel` 的 `write` 方法打一个断点

![tomcat#44](resources/2022-06-21_21-51-24.png)

这证明了**tomcat nio 网络模型的IO写是在业务线程池完成的，并且与IO读、业务逻辑处理是同一个线程**

### http长连接

我们知道对于http长连接，一次长连接可能包含多个http请求，而 `Poller` 线程会监听存放事件的队列里的读事件，所以我们可以在存放事件的队列的地方打一个断点（ `org.apache.tomcat.util.net.NioEndpoint.Poller.addEvent` ）

![tomcat#37](resources/2022-06-20_22-00-13.png)

发送请求后可以发现有两个线程调用此处：

![tomcat#46](resources/2022-06-22_21-45-30.png)

![tomcat#47](resources/2022-06-22_21-46-17.png)

