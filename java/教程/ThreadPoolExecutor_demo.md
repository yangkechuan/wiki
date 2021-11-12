## ThreadPoolExecutor 自定义参数

    参考：https://mp.weixin.qq.com/s/OKTW_mZnNJcRBrIFHONR3g

    本文着重分析，当任务不断增加时，核心线程，队列，最大线程三个参数的任务处理顺序。

### 0. 说明

先说结论：

- 创建完 `ThreadPoolExecutor` 后，并没有线程启动，只有任务加入后，才开始启动线程
- 任务加入后，先开启核心线程进行处理
- 核心线程数达到最大值后，新增任务进入队列
- 当队列满后，再新增任务，会继续创建非核心线程，直到最大线程数
- 当队列已满，并且已达到最大线程数，再次进入的任务，执行拒绝策略


![ThreadPoolExecutor_demo_1](../images/ThreadPoolExecutor_demo_1.gif)

### 1. ThreadPoolExecutor 配置参数

当自己通过 `ThreadPoolExecutor` 实现线程池时，主要有几个参数，构造方法如下：

```java
    public ThreadPoolExecutor(int corePoolSize,
                            int maximumPoolSize,
                            long keepAliveTime,
                            TimeUnit unit,
                            BlockingQueue<Runnable> workQueue,
                            ThreadFactory threadFactory,
                            RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.acc = System.getSecurityManager() == null ? null :AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
        }
```

根据构造方法，自己配置参数如下：

核心线程数是 `1` ,最大线程数 `5` ，队列长度 `2`。


```java
ThreadPoolExecutor pool=new ThreadPoolExecutor(1,
        5,
        0,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(2),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy());
```


完整代码：

```java
package example;

import lombok.SneakyThrows;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Test1 implements Runnable {

    private static AtomicInteger i = new AtomicInteger(0);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ThreadPoolExecutor pool = new ThreadPoolExecutor(1,
                5,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        
        Test1 test1 = new Test1();
        for (int i = 0; i < 1; i++) {
            pool.submit(test1);
        }

        pool.shutdown();
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "-----" + i.getAndIncrement());
        TimeUnit.SECONDS.sleep(1);
    }
}
```

### 2. 代码执行

#### 2.1 一个任务

执行以上代码，看到打印结果：

```log
pool-1-thread-1-----0
```

说明只有一个任务时，会有一个线程去处理。

#### 2.1 两个任务

增加 `for` 循环中的任务数到 `2`。然后再执行代码，看到打印结果：

```log
pool-1-thread-1-----0
pool-1-thread-1-----1
```

两个任务时，只会有一个线程去处理。说明只启动了一个核心线程，当第二个任务加入时，并没有再次启动线程，而是新任务加入到队列中。


#### 2.1 四个任务

三个任务与两个任务一致，只会有一个线程去处理，这里直接到四个任务

增加 `for` 循环中的任务数到 `4`。然后再执行代码，看到打印结果：

```log
pool-1-thread-1-----0
pool-1-thread-2-----1
pool-1-thread-1-----2
pool-1-thread-2-----3
```
这里会看到，出现 `thread-2` 的标志，说明4个任务时，`核心线程 + 队列数` < `当前任务数`。就会再次创建非核心线程

#### 2.1 五个任务

增加 `for` 循环中的任务数到 `5`。然后再执行代码，看到打印结果：

```log
pool-1-thread-1-----0
pool-1-thread-2-----1
pool-1-thread-3-----2
pool-1-thread-2-----3
pool-1-thread-1-----4
```

会看到 `thread-3` ，说明队列满后，每次新增任务，会不断创建非核心线程，直到线程数达到最大线程 `5`


#### 2.1 七个任务

验证以上猜想，增加 `for` 循环中的任务数到 `7`。然后再执行代码，看到打印结果：

```log
pool-1-thread-1-----0
pool-1-thread-3-----2
pool-1-thread-2-----1
pool-1-thread-4-----3
pool-1-thread-5-----4
pool-1-thread-2-----5
pool-1-thread-3-----6
```

根据日志可知，已达到 `5` 个最大线程数。

#### 2.1 八个任务

当达到最大线程数时，再次增加任务到 `8`。然后再执行代码，看到打印结果：


```log
pool-1-thread-1-----0
pool-1-thread-3-----2
pool-1-thread-2-----1
pool-1-thread-4-----3
pool-1-thread-5-----4
Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@28c97a5 rejected from java.util.concurrent.ThreadPoolExecutor@6659c656[Running, pool size = 5, active threads = 5, queued tasks = 2, completed tasks = 0]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:112)
	at example.Test1.main(Test1.java:24)
pool-1-thread-2-----5
pool-1-thread-5-----6
```

这时会看到，触发拒绝策略，并且需要注意，这时线程池的 `shutdown` 并没有起作用，线程池没有被关闭。


### 3.注意点

### 3.1

**当队列已满，创建非核心线程时，是新任务直接创建新线程。而不是取队列的旧任务创建线程，再把新任务添加到队列**

这样可以省去添加和获取队列任务的时间。

### 3.2

如果使用默认的拒绝策略 `AbortPolicy()` ，在抛出 `RejectedExecutionException` 异常之后，添加新的任务时，无法再执行新任务。并且不能被 `shutdown`

可以通过捕获异常的方式解决，例如上面代码，创建 `10` 个线程，然后捕获异常：

```java
Test1 test1 = new Test1();
for (int i = 0; i < 10; i++) {
    try {
        pool.submit(test1);
    }catch (RejectedExecutionException e){
        System.out.println(e);
    }
}
pool.shutdown();
```

执行并查看日志：

```log
pool-1-thread-1-----0
pool-1-thread-3-----2
pool-1-thread-2-----1
pool-1-thread-4-----3
pool-1-thread-5-----4
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@28c97a5 rejected from java.util.concurrent.ThreadPoolExecutor@6659c656[Running, pool size = 5, active threads = 5, queued tasks = 2, completed tasks = 0]
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@6d5380c2 rejected from java.util.concurrent.ThreadPoolExecutor@6659c656[Running, pool size = 5, active threads = 5, queued tasks = 2, completed tasks = 0]
java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@45ff54e6 rejected from java.util.concurrent.ThreadPoolExecutor@6659c656[Running, pool size = 5, active threads = 5, queued tasks = 2, completed tasks = 0]
pool-1-thread-5-----5
pool-1-thread-4-----6

Process finished with exit code 0
```

可以看到程序正常结束。

异常线程会被取消，并创建一个新的线程来承接新的任务，并不会因为异常，就减少一个核心或者非核心线程。


### 4.附录：四种拒绝策略


#### 3.1 AbortPolicy()

`AbortPolicy` 是默认拒绝策略，执行方式是抛出异常。

```java
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    throw new RejectedExecutionException("Task " + r.toString() +
        " rejected from " +
        e.toString());
}
```

#### 3.2 CallerRunsPolicy()

`CallerRunsPolicy` 是把当前任务在主线程中执行，直接调用 `run()` 方法


```java
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
        r.run();
    }
}
```

#### 3.3 DiscardPolicy()

`DiscardPolicy` 是直接丢弃当前任务，不执行，也不抛出异常

```java
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
}
```

#### 3.4 DiscardOldestPolicy()

`DiscardOldestPolicy` 是丢弃队列中最早的一个任务，然后把当前任务添加进去

```java
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
        e.getQueue().poll();
        e.execute(r);
    }
}
```



---
以上。