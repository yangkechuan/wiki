## springboot + activemq

---

### 1.1.安装启动Activemq

- activemq下载地址：[http://activemq.apache.org/download.html](http://activemq.apache.org/download.html)
- 下载解压后，根据自己电脑是32还是64位进入相应的bin目录，然后找到`activemq.bat`，双击运行.
- **如果本地已运行`rabbmitmq`，启动可能提示`端口占用`，先从服务中停止`rabbmitmq`，再运行.**

成功运行后，可以打开admin地址：

    http://127.0.0.1:8161/admin/
    账户：admin 密码：admin

### 2.springBoot配置

开启`两个项目`，分别作为`生产者`和`消费者`

`pom.xml`文件：

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

生产者和消费者只是启动端口不同

`生产者`配置文件：

```properties
# activemq
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.in-memory=true
spring.activemq.pool.enabled=false
spring.activemq.user=admin
spring.activemq.password=admin
# log
logging.level.com.example.demo=debug
```


`消费者`配置文件：


```properties
# log
logging.level.com.example.demo=debug

server.port=8081

spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.in-memory=true
spring.activemq.pool.enabled=false
spring.activemq.user=admin
spring.activemq.password=admin
```


`生产者`代码：

Producer.java:

```java
@RestController
public class Producer {

    private static final String QUEUE = "activemq";

    @Autowired
    private JmsMessagingTemplate template;

    @GetMapping("/producer")
    public void producer(@RequestParam String msg){
        template.convertAndSend(QUEUE, msg);
    }
}
```


`消费者`代码：

Consumer.java:

```java
@Slf4j
@Component
public class Consumer {

    private static final String QUEUE = "activemq";

    @JmsListener(destination = QUEUE)
    public void reveiceQueue(String msg){
        log.debug(msg);
    }
}
```

启动两个项目，然后发送请求
    
    http://localhost:8080/producer?msg=activemq


在消费者查看日志：

```log
2018-10-26 11:05:54.648 DEBUG 10156 --- [enerContainer-1] com.example.demo.Consumer: activemq
```


说明可以接收到消息

---

以上。
