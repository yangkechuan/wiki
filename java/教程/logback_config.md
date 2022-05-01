## logback 配置

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Apache Maven 3.5.4


### 1.log4j 和 slf4j

java 使用 log，一般有引入 `log4j` 和 `slf4j`，前者可以单独使用。后者只是相当于一个规范接口。需要再导入具体实现类。

现在基本是基于后者使用，所以重点说一下 `slf4j`。

如果想使用 `slf4j`，需要配合实现类一起使用，例如 `logback`。


### 2. logback

`logback` 一般会有三个包：`logback-core`, `logback-classic`, `logback-access`。

- `logback-classic` 模块是log4j 1.x 的显著改进版本，并且实现了原生的SLF4J API。
- `logback-access`模块与Tomcat和Jetty等Servlet容器集成，以提供HTTP访问日志功能
- `logback-core` 为另外两个包的基础包，，当导入另外两个包任意一个时，已经自动导入该基础包。

例如，当导入 `logback-classic` 时，通过依赖分析，会发现该包已经包含了 `logback-core`。
并且会发现 `logback-classic` 已经包含了 `slf4j-api`，如果再次导入，可能会导致版本冲突。

- **所以正常情况下，如无特殊需要，只需要导入 `logback-classic` 即可**
- **springboot 项目中， spring-boot-starter-web 已经集成了 logback-classic**


导入 `logback-classic`的方式：

```xml
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.10</version>
            <scope>compile</scope>
        </dependency>
```

### 3. 配置 logback 文件

logback 配置文件加载顺序：

1. 如果java程序启动时指定了 `logback.configurationFile` 属性，就用该属性指定的配置文件。如 `java -Dlogback.configurationFile=/path/to/mylogback.xml Test` ，这样执行Test类的时候就会加载 `/path/to/mylogback.xml` 配置
2. 在 `classpath` 中查找 `logback.groovy` 文件
3. 在 `classpath` 中查找 `logback-test.xml` 文件
4. 在 `classpath` 中查找 `logback.xml` 文件

一般情况下，放到 `resources` 下，文件名为 `logback.xml`。或者根据不同环境，配置多个 `logback.xml`。

根据以下配置，可以在命令行输出日志，并可以写入日志文件，同时，由于单独创建了一个 `error` 级别的 `appender` ，`error` 日志会单独输出到一个文件中

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true" scan="true" scanPeriod="1 seconds">

    <contextName>logback</contextName>
    <!--定义参数,后面可以通过${app.name}使用-->
    <property name="app.name" value="demo"/>
    <!--ConsoleAppender 用于在屏幕上输出日志-->
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <!--定义了一个过滤器,在LEVEL之下的日志输出不会被打印出来-->
        <!--这里定义了DEBUG，也就是控制台不会输出比ERROR级别小的日志-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>DEBUG</level>
        </filter>
        <!-- encoder 默认配置为PatternLayoutEncoder -->
        <!--定义控制台输出格式-->
        <encoder>
            <pattern>%d{yyyy-mm-dd HH:mm:ss.SSS } [%thread] %-5level %logger{36} [%file : %line] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--定义日志输出的路径-->
        <file>./logs/${app.name}.log</file>
        <!--定义日志滚动的策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--定义文件滚动时的文件名的格式-->
            <fileNamePattern>./logs/${app.name}.%d{yyyy-MM-dd.HH}.log.gz
            </fileNamePattern>
            <!--60天的时间周期，日志量最大20GB-->
            <maxHistory>60</maxHistory>
            <!-- 该属性在 1.1.6版本后 才开始支持-->
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <!--每个日志文件最大100MB-->
            <maxFileSize>100MB</maxFileSize>
        </triggeringPolicy>
        <!--定义输出格式-->
        <encoder>
            <pattern>%d{yyyy-mm-dd HH:mm:ss.SSS } [%thread] %-5level %logger{36} [%file : %line] - %msg%n</pattern>
        </encoder>
    </appender>

    <!--定义一个 error 级别，单独输出到一个日志文件中-->
    <appender name="error" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--定义日志输出的路径-->
        <file>./logs/${app.name}-error.log</file>
        <!--定义日志滚动的策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--定义文件滚动时的文件名的格式-->
            <fileNamePattern>./logs/${app.name}-error.%d{yyyy-MM-dd.HH}.log.gz
            </fileNamePattern>
            <!--60天的时间周期，日志量最大20GB-->
            <maxHistory>60</maxHistory>
            <!-- 该属性在 1.1.6版本后 才开始支持-->
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <!--每个日志文件最大100MB-->
            <maxFileSize>100MB</maxFileSize>
        </triggeringPolicy>
        <!--定义输出格式-->
        <encoder>
            <pattern>%d{yyyy-mm-dd HH:mm:ss.SSS } [%thread] %-5level %logger{36} [%file : %line] - %msg%n</pattern>
        </encoder>
        <!--只处理 error 级别-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
    </appender>

    <!--root是默认的logger 这里设定输出级别是debug-->
    <root level="debug">
        <!--定义了两个appender，日志会通过往这两个appender里面写-->
        <appender-ref ref="stdout"/>
        <appender-ref ref="file"/>
        <appender-ref ref="error"/>
    </root>

    <!--对于类路径以 com.example.logback 开头的Logger,输出级别设置为warn,并且只输出到控制台-->
    <!--这个logger没有指定appender，它会继承root节点中定义的那些appender-->
    <logger name="com.example.logback" level="warn"/>

    <!--通过 LoggerFactory.getLogger("mytest") 可以获取到这个logger-->
    <!--由于这个logger自动继承了root的appender，root中已经有stdout的appender了，自己这边又引入了stdout的appender-->
    <!--如果没有设置 additivity="false" ,就会导致一条日志在控制台输出两次的情况-->
    <!--additivity表示要不要使用rootLogger配置的appender进行输出-->
    <logger name="mytest" level="info" additivity="false">
        <appender-ref ref="stdout"/>
    </logger>

    <!--由于设置了 additivity="false" ，所以输出时不会使用rootLogger的appender-->
    <!--但是这个logger本身又没有配置appender，所以使用这个logger输出日志的话就不会输出到任何地方-->
    <logger name="mytest2" level="info" additivity="false"/>
</configuration>
```


### 4.附录

参考：

- [logback介绍和配置详解](https://www.jianshu.com/p/04065d8cb2a9)


---

以上。