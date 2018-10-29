## java task

---

java 实现定时任务有几种方法，本文介绍其中几种



- Timer
- ScheduledExecutor
- quartz
- springboot + quartz


### 1. Timer

通过java自带的java.util.Timer 实现：

```java

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author yangkechuan
 */
public class TaskTimer extends TimerTask {

    private String jobName = null;

    private TaskTimer(String name){
        super();
        this.jobName = name;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();

        long delay = 1000;
        long period = 2000;

        //延迟1s执行，之后每隔2s执行一次
        timer.schedule(new TaskTimer("task"), delay, period);

        //延迟1s执行，只执行一次，执行后，进程并不会终止
        timer.schedule(new TaskTimer("task2"), delay);
    }

    @Override
    public void run() {
        System.out.println(jobName);
    }
}
```

>Timer 的设计核心是一个 TaskList 和一个 TaskThread。Timer 将接收到的任务丢到自己的 TaskList 中，TaskList 按照 Task 的最初执行时间进行排序。TimerThread 在创建 Timer 时会启动成为一个守护线程。这个线程会轮询所有任务，找到一个最近要执行的任务，然后休眠，当到达最近要执行任务的开始时间点，TimerThread 被唤醒并执行该任务。之后 TimerThread 更新最近一个要执行的任务，继续休眠。
Timer 的优点在于简单易用，但由于所有任务都是由同一个线程来调度，因此所有任务都是串行执行的，同一时间只能有一个任务在执行，前一个任务的延迟或异常都将会影响到之后的任务。


### 2.ScheduledExecutor

通过并发包的线程池来做：


```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Task implements Runnable{

    private String jobName = null;

    private Task(String name){
        super();
        this.jobName = name;
    }

    public static void main(String[] args) {

        Task task = new Task("job1");
        Task task2 = new Task("job2");

        ScheduledExecutorService service = Executors.
                newScheduledThreadPool(10);

        //延迟1秒执行，每隔1秒执行一次
        service.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);

        //延迟2秒执行，上次任务结束后推迟2秒再执行
        service.scheduleWithFixedDelay(task2, 2, 2, TimeUnit.SECONDS);


    }
    @Override
    public void run() {
        System.out.println(this.jobName);
    }
}
```

*只有当任务的执行时间到来时，ScheduedExecutor 才会真正启动一个线程，其余时间 ScheduledExecutor 都是在轮询任务的状态*



### 3.quartz


新建一个`maven`项目:


`pom.xml`依赖如下：


```xml
    <dependencies>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <version>2.2.1</version>
        </dependency>
    </dependencies>
```


Task.java:

两种执行方式：

```java
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Task {

    public static void main(String[] args) throws SchedulerException {

        //调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();


        //触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(1)
                .repeatForever())
                .build();

        //表达式触发器
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("cronTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/3 * * * * ?"))
                .build();

        //任务数据
        JobDetail job  = JobBuilder.newJob(QuartzJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("name", "quartz")
                .build();


        JobDetail cronJob = JobBuilder.newJob(CronJob.class)
                .withIdentity("job2", "group1")
                .usingJobData("name", "cronJob")
                .build();


        scheduler.scheduleJob(job, trigger);
        scheduler.scheduleJob(cronJob, cronTrigger);
        scheduler.start();


    }
}

```

QuartzJob.java:


```java
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class QuartzJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //job执行逻辑
        JobDetail detail = jobExecutionContext.getJobDetail();
        String name = detail.getJobDataMap().getString("name");
        System.out.println(name);
    }
}
```

CronJob.java:


```java
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CronJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //job执行逻辑
        JobDetail detail = context.getJobDetail();
        String name = detail.getJobDataMap().getString("name");
        System.out.println(name);
    }
}

```



### 4.springboot + quartz



新建一个springboot项目：

`pom.xml`配置：


```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>


        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
        </dependency>
```


配置quartz

QuartzConfig.java:


```java

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(Task.class)
                .withIdentity("task", "group")
                .storeDurably()
                .usingJobData("name", "quartz")
                .build();
    }

    @Bean
    public Trigger trigger(){
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("*/2 * * * * ?");

        return TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail())
                .withIdentity("task", "group")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
```


任务详情：

Task.java:

```java

@Slf4j
@DisallowConcurrentExecution
public class Task extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
            JobDetail detail = jobExecutionContext.getJobDetail();
            String name = detail.getJobDataMap().getString("name");
            log.debug(name);
    }
}
```



查看日志：

```log
2018-10-29 11:36:56.002 DEBUG 2300 --- [eduler_Worker-2] com.example.demo.quartz.Task             : quartz
2018-10-29 11:36:58.000 DEBUG 2300 --- [eduler_Worker-3] com.example.demo.quartz.Task             : quartz
2018-10-29 11:37:00.002 DEBUG 2300 --- [eduler_Worker-4] com.example.demo.quartz.Task             : quartz
2018-10-29 11:37:02.000 DEBUG 2300 --- [eduler_Worker-5] com.example.demo.quartz.Task             : quartz
2018-10-29 11:37:04.000 DEBUG 2300 --- [eduler_Worker-6] com.example.demo.quartz.Task             : quartz

```

---


参考：

- [https://www.ibm.com/developerworks/cn/java/j-lo-taskschedule/](https://www.ibm.com/developerworks/cn/java/j-lo-taskschedule/)
- [https://www.cnblogs.com/drift-ice/p/3817269.html](https://www.cnblogs.com/drift-ice/p/3817269.html)

---

以上。