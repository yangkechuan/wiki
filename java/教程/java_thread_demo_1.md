## java wait/notify 和park/unpark


    线程之间通信有多种方式，这里只想着重记录一下,这两种方式在使用的时候，有哪些不同点，特别是在加锁不加锁时，执行顺序问题。

###1. wait / notify(notifyall)

#### 1) 这种方式在使用的时候必须加锁，不管是 `wait` 还是 `notify`，都必须锁定对象，都是针对于对象的操作。

不加锁会报 `IllegalMonitorStateException` 错误
样例代码：

```java
public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args){

        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            try {
                o1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1 end");
        });

        t1.start();

        System.out.println("main is over");
    }
}
```
打印结果：

```log
main is over
t1 start
Exception in thread "Thread-0" java.lang.IllegalMonitorStateException
	at java.lang.Object.wait(Native Method)
	at java.lang.Object.wait(Object.java:502)
	at Test.lambda$main$0(Test.java:12)
	at java.lang.Thread.run(Thread.java:748)

Process finished with exit code 0
```

#### 2) `wait / notify` 使用时，最重要一点是，必须先执行 `wait`，再执行 `notify`，才能保证代码正常执行。如果 `wait` 在之后执行，则 `wait` 之后的代码则不会执行。

样例代码：
```java
import java.util.concurrent.TimeUnit;

public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            synchronized (o1){
                System.out.println("t1 lock o1");
                try {
                    o1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t1 end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");
            synchronized (o1){
                System.out.println("t2 notify o1");
                o1.notify();
            }
            System.out.println("t2 end");
        });

        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t1.start();

        t2.join();
        t1.join();

        System.out.println("main is over");
    }
}

```

打印结果：
```log
t2 start
t2 notify o1
t2 end
t1 start
t1 lock o1

```
程序并没有结束，t1在wait之后阻塞，由于没有其他线程打断，导致不能正常结束。


#### 3) wait某个对象时，会解锁当前对象，而且只会解锁当前对象

样例代码：

```java
import java.util.concurrent.TimeUnit;

public class Test {

    private static volatile Object o1 = new Object();
    private static volatile Object o2 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            synchronized (o1){
                System.out.println("t1 lock o1");
                synchronized (o2){
                    try {
                        System.out.println("t1 o1 wait");
                        o1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("t1 end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");
            synchronized (o1){
                System.out.println("t2 lock o1");
                synchronized (o2){
                    System.out.println("t2 o1 notify");
                    o1.notify();
                }
            }

            System.out.println("t2 end");
        });
        
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        t1.join();
        t2.join();
        System.out.println("main is over");
    }
}

```

以上代码执行日志：
```log
t1 start
t1 lock o1
t1 o1 wait
t2 start
t2 lock o1
```

**分析：** 线程1先执行，然后执行线程2，线程1在执行到 `wait` 方法后,解锁`o1`对象,但是`o2`对象未解锁，导致线程2不能获取到`o2`的锁，程序阻塞，不能继续执行。


#### 4） wait / notify 执行顺序

先看程序：

```java
import java.util.concurrent.TimeUnit;

public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            synchronized (o1){
                System.out.println("t1 lock o1");
                try {
                    System.out.println("t1 wait start");
                    o1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 wait end");
            }
            System.out.println("t1 end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");
            synchronized (o1){
                System.out.println("t2 notify o1");
                o1.notify();
                System.out.println("t2 notify end");
            }
            System.out.println("t2 end");
        });

        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();

        t1.join();
        t2.join();

        System.out.println("main is over");
    }
}
```

打印结果：

```log
t1 start
t1 lock o1
t1 wait start
t2 start
t2 notify o1
t2 notify end
t2 end
t1 wait end
t1 end
main is over
```

根据日志结果，t2执行完之后，t1继续从wait之后执行，但是，本人疑惑点：

    - t2 notify 之后，什么时候开始并发执行
    - 以上日志打印结果是否每次都一致

于是验证以上疑惑

代码稍加修改：

```java
        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");
            synchronized (o1){
                System.out.println("t2 notify o1");
                o1.notify();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 notify end");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 end");
        });
```

只给出了修改部分，在t2的锁和锁外分别加延迟，然后执行，打印结果：

```log
t1 start
t1 lock o1
t1 wait start
t2 start
t2 notify o1
t2 notify end
t1 wait end
t1 end
t2 end
main is over
```

注意看 `t2 notify end` 之后才是 `t1 wait end`,而 `t2 end`再、在线程最后。

**说明：`notify`之后，并不会立刻让 `wait` 线程执行，而是等到当前 `notify` 锁部分结束之后，才通知到 `wait` 线程，之后，开始并发执行。**


###2. park / unpark

个人而言，该方式有两大优势：

    - 不强制依赖于锁
    - 不强依赖于先后执行顺序
    
    
先看程序：

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 park");
            LockSupport.park();
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 unpark");
            LockSupport.unpark(t1);
        });

        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();

        t1.join();
        t2.join();

        System.out.println("main is over");
    }
}
```

查看打印结果：

```log
t1 park
t2 unpark
main is over
```

线程1和线程2并没有用到锁，同时 `park` 和 `unpark` 均为静态方法,程序正常执行

再看以下代码：

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 park");
            LockSupport.park();
        });


        LockSupport.unpark(t1);
        TimeUnit.SECONDS.sleep(1);
        t1.start();
        t1.interrupt();

        System.out.println("main is over");
    }
}
```

执行：

```log
main is over
t1 park

Process finished with exit code 0
```

先执行 `unpark` 再执行 `park`，线程也可以正常执行


#### pack /  unpark 并发问题


执行以下代码：

```java
mport java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Test {

    private static volatile Object o1 = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            LockSupport.park();
            System.out.println("t1 park end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");

            LockSupport.unpark(t1);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 unpark");
        });


        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        t1.join();
        t2.join();

        System.out.println("main is over");
    }
}
```

打印：

```log
t1 start
t2 start
t1 park end
t2 unpark
main is over
```

说明在执行完 `unpark` 之后，并发执行。

但是如果加锁呢？

修改代码：

```java
        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            synchronized (o1){
                System.out.println("t1 park");
                LockSupport.park();
                System.out.println("t1 park end");
            }
            System.out.println("t1 end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");

            synchronized (o1){
                System.out.println("t2 unpark");
                LockSupport.unpark(t1);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 unpark end");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 unpark");
        });
```

打印日志：

```log
t1 start
t1 park
t2 start
```

程序被阻塞，说明 `o1` 被线程1占用

看来，`park` 并不会释放锁。然后去掉线程1的锁，保留线程2的锁：


```java
 Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            LockSupport.park();
            System.out.println("t1 end");
        });


        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");

            synchronized (o1) {
                System.out.println("t2 unpark");
                LockSupport.unpark(t1);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 unpark end");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 unpark");
        });
```


执行结果：

```log
t1 start
t2 start
t2 unpark
t1 end
t2 unpark end
t2 unpark
main is over
```

`t2 unpark end` 是在 `t1 end` 之后执行，说明此时，线程2锁是无效的.


---

以上。


