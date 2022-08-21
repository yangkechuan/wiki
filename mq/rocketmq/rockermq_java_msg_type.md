## rocketmq 不同类型消息的生产与消费

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            rocketmq Version: 4.9.3
            rocketmq client Version:4.9.4
---



### 1.顺序消息的生产与消费

例如有以下场景：

用户可以下单，然后才能支付订单。其中，下单和支付流程，需要保证顺序。

需要保证，同一条 `topic` 下的同一个订单号的各种状态的数据，落在同一个队列下。这样消费时，同一个队列的消息，才能保证顺序性。




生产者代码如下：

```java
import lombok.Data;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProudcerOrder {


    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {

        /*
         * 顺序消费的场景：
         *
         * 例如有多个订单数据，操作顺序为：先下单，后支付。对于同一条订单，消费顺序不能变。
         *
         * 主要使用 MessageQueueSelector 来完成
         */

        // 实例化生产者 producer
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        // 配置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 启动 producer 实例
        producer.start();

        List<Order> orderList = buildUserList();

        for (int i = 0 ; i < orderList.size() ; i++){
            String body = orderList.get(i).toString();
            Message msg = new Message("TestOrder", null, "KEY" + i , body.getBytes(StandardCharsets.UTF_8));

            SendResult result = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    /*
                     * 使用订单 id 对 queue size 数进行取模运算，保证同一条订单数据，放到同一个 queue 中，来达到顺序消费的目的。
                     * 这里是对 queue 进行取模，并不是对 orderList 进行取模。
                     */
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
                // arg 具体代表内容，在第二个参数中，这里使用订单 id 来表示
            }, orderList.get(i).getId());

            System.out.printf("status: %s, queueId: %s, body:%s%n",
                    result.getSendStatus(), result.getMessageQueue().getQueueId(), body);
        }


        producer.shutdown();
    }

    private static List<Order> buildUserList(){
        List<Order> userList = new ArrayList<>();

        Order o1 = new Order();
        o1.setId(1);
        o1.setDescription("下单");

        Order o2 = new Order();
        o2.setId(2);
        o2.setDescription("下单");

        Order o3 = new Order();
        o3.setId(1);
        o3.setDescription("支付");

        Order o4 = new Order();
        o4.setId(2);
        o4.setDescription("支付");

        Order o5 = new Order();
        o5.setId(3);
        o5.setDescription("下单");

        Order o6 = new Order();
        o6.setId(3);
        o6.setDescription("支付");

        userList.add(o1);
        userList.add(o2);
        userList.add(o3);
        userList.add(o4);
        userList.add(o5);
        userList.add(o6);
        return userList;
    }
}
@Data
class Order{

    private Integer id;

    private String description;
}



```

执行以上代码，主要日志如下：

```
status: SEND_OK, queueId: 1, body:Order(id=1, description=下单)
status: SEND_OK, queueId: 2, body:Order(id=2, description=下单)
status: SEND_OK, queueId: 1, body:Order(id=1, description=支付)
status: SEND_OK, queueId: 2, body:Order(id=2, description=支付)
status: SEND_OK, queueId: 3, body:Order(id=3, description=下单)
status: SEND_OK, queueId: 3, body:Order(id=3, description=支付)
```

根据日志信息可以看到，对于同一个订单，落在了同一个队列中。



针对以上内容，消费者代码如下：

```java
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ConsumerOrder {


    public static void main(String[] args) throws MQClientException {

        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("default_consumer_group");
        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 订阅 topic ，消费 TopicTest 下的所有 Tag
        consumer.subscribe("TestOrder", "*");

        // 集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 顺序消费监听
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);

                for (MessageExt msg: msgs){
                    System.out.println("consumerThread :" + Thread.currentThread().getName() + ", queueId：" + msg.getQueueId()
                            + ", body：" + new String(msg.getBody()));
                }
                try {
                    // 模拟业务操作
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(300));
                } catch (Exception e) {
                    // 如果有异常，先等一会，再处理消息，而不是直接放到重试队列
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("consumer start");
        
    }
}
```


消费者日志如下：


```
consumer start
consumerThread :ConsumeMessageThread_default_consumer_group_2, queueId：3, body：Order(id=3, description=下单)
consumerThread :ConsumeMessageThread_default_consumer_group_3, queueId：1, body：Order(id=1, description=下单)
consumerThread :ConsumeMessageThread_default_consumer_group_1, queueId：2, body：Order(id=2, description=下单)
consumerThread :ConsumeMessageThread_default_consumer_group_1, queueId：2, body：Order(id=2, description=支付)
consumerThread :ConsumeMessageThread_default_consumer_group_3, queueId：1, body：Order(id=1, description=支付)
consumerThread :ConsumeMessageThread_default_consumer_group_2, queueId：3, body：Order(id=3, description=支付)
```


可以看到，有三个线程，每个线程处理一个队列中的数据，并且，是按照顺序来消费。



### 2. 延时消息的生产与消费

例如有以下场景：用户下单后，并不会立刻支付，可能由于某个原因，一直没有支付，也没有取消。业务上，可以设定一个时间。
例如半个小时后，如果未支付，自动取消订单。

这种场景，可以在用户下单后，同时发送一个延时消息，当消费延时消息时，检测当前订单是否被支付或者取消，如果依旧是待支付状态，则取消订单。

生产的消息，不会立刻发送给消费者，而是，经过一个延时之后，再发送给消费者。

生产者代码如下：

```java
package example.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

/**
 * @author yangkechuan
 */
public class ScheduleProducer {


    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        // 实例化生产者 producer
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        // 配置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 启动 producer 实例
        producer.start();

        for (int i = 0 ; i < 10 ; i++){
            Message msg = new Message("scheduleTopic", ("hello world" + i).getBytes(StandardCharsets.UTF_8));

            /*
             * 设置延迟等级为2。消息将在 5s 之后发送
             * delayTimeLevel(1 ~ 18 个等级): 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 30m 1h 2h
             */
            msg.setDelayTimeLevel(2);

            producer.send(msg);
        }

        producer.shutdown();
    }
}

```


消费者代码如下：

```java
package example.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class ScheduleConsumer {


    public static void main(String[] args) throws MQClientException {

        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("default_consumer_group");
        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 订阅 topic ，消费 TopicTest 下的所有 Tag
        consumer.subscribe("scheduleTopic", "*");

        // 集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for (MessageExt msg: list){
                        String topic = msg.getTopic();
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        String tags = msg.getTags();
                        System.out.printf("收到消息：topic: %s , body: %s, tags: %s%n", topic, body, tags);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}

```


先启动消费者，然后，再启动生产者，在消费者的日志中，5s 之后，可以看到消费日志：

```
收到消息：topic: scheduleTopic , body: hello world1, tags: null
收到消息：topic: scheduleTopic , body: hello world0, tags: null
收到消息：topic: scheduleTopic , body: hello world2, tags: null
收到消息：topic: scheduleTopic , body: hello world3, tags: null
收到消息：topic: scheduleTopic , body: hello world5, tags: null
收到消息：topic: scheduleTopic , body: hello world9, tags: null
收到消息：topic: scheduleTopic , body: hello world8, tags: null
收到消息：topic: scheduleTopic , body: hello world6, tags: null
收到消息：topic: scheduleTopic , body: hello world4, tags: null
收到消息：topic: scheduleTopic , body: hello world7, tags: null
```

说明延时消费成功。



### 3.批量发送消息的生产与消费


如果想一次性发送多条消息，可以使用批量发送。

生产者代码如下：

```java
package example.rocketmq;


import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BatchProducer {


    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {


        // 实例化生产者 producer
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        // 配置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 启动 producer 实例
        producer.start();

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message("batchTopic", "hello world".getBytes(StandardCharsets.UTF_8)));
        messageList.add(new Message("batchTopic", "hello world".getBytes(StandardCharsets.UTF_8)));
        messageList.add(new Message("batchTopic", "hello world".getBytes(StandardCharsets.UTF_8)));
        messageList.add(new Message("batchTopic", "hello world".getBytes(StandardCharsets.UTF_8)));
        messageList.add(new Message("batchTopic", "hello world".getBytes(StandardCharsets.UTF_8)));

        /*
         * 发送批量消息
         * 一般，单次批量消息大小不能超过 4M ，否则会有性能问题
         * 如果消息过大，需要人为进行拆分
         */
        producer.send(messageList);

        producer.shutdown();
    }
}

```


消费者代码如下：

```java
package example.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class BatchConsumer {


    public static void main(String[] args) throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("default_consumer_group");
        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 订阅 topic ，消费 TopicTest 下的所有 Tag
        consumer.subscribe("batchTopic", "*");

        // 集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for (MessageExt msg: list){
                        String topic = msg.getTopic();
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        String tags = msg.getTags();
                        System.out.printf("收到消息：topic: %s , body: %s, tags: %s%n", topic, body, tags);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}

```


启动生产者和消费者，可以在消费者看到消费日志：

```
收到消息：topic: batchTopic , body: hello world, tags: null
收到消息：topic: batchTopic , body: hello world, tags: null
收到消息：topic: batchTopic , body: hello world, tags: null
收到消息：topic: batchTopic , body: hello world, tags: null
收到消息：topic: batchTopic , body: hello world, tags: null
```



### 4.过滤消息的生产与消费


#### 4.1 tag 过滤

同一个 `topic`,如果还需要细分业务，只想处理 `topic` 下的某种信息。可以把类型放到 `body` 中。在代码层面进行过滤。

同时，也可以根据 `topic` 的 `tag` 信息，过滤只想处理的消息。



生产者代码如下，同一个 `topic` , 有 `TagA`, `TagB`, `TagC` 三种不同的消息：

```java
package example.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

public class TagFilterProducer {

    public static void main(String[] args) throws MQClientException {


        // 实例化生产者 producer
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        // 配置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 启动 producer 实例
        producer.start();


        String[] tags = {"TagA", "TagB", "TagC"};
        // 循环发送 3 条，分别使用不同 tag
        for (int i = 0; i < tags.length; i++) {
            Message msg = new Message("TopicTest",
                    tags[i % tags.length],
                    "testKey",
                    "Hello world".getBytes(StandardCharsets.UTF_8));

            try {
                // 发送消息
                SendResult result = producer.send(msg);
                System.out.printf("%s%n", result);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 关闭生产者实例
        producer.shutdown();
    }
}

```



如果我们的消费者，只想处理 `TagA`, `TagB` 的相关消息，可以使用以下方式过滤并处理：

```java
package example.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TagFilterConsumer {


    public static void main(String[] args) throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("default_consumer_group");
        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 订阅 topic ，tag 表达式，支持正则方式
        consumer.subscribe("TopicTest", "TagA || TagB");

        // 集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for (MessageExt msg: list){
                        String topic = msg.getTopic();
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        String tags = msg.getTags();
                        System.out.printf("收到消息：topic: %s , body: %s, tags: %s%n", topic, body, tags);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}

```


启动生产者和消费者，根据消费者日志，正确消费消息：

```
收到消息：topic: TopicTest , body: Hello world, tags: TagB
收到消息：topic: TopicTest , body: Hello world, tags: TagA
```


#### 4.2 sql 过滤

`rocketmq` 也支持一些类 sql 的基础语法，来进行过滤。


生产者代码如下：

```java
package example.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

public class SqlFilterProducer {

    public static void main(String[] args) throws MQClientException {
        // 实例化生产者 producer
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        // 配置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 启动 producer 实例
        producer.start();

        String[] tags = {"TagA", "TagB", "TagC"};

        // 循环发送 10 条，分别使用不同 tag
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("SqlTopicTest",
                    tags[i % tags.length],
                    "testKey",
                    ("Hello world" + i).getBytes(StandardCharsets.UTF_8));

            // 给 msg 设定一些特定的属性，独立于 tag ，方便 consumer 过滤
            msg.putUserProperty("a", String.valueOf(i));

            try {
                // 发送消息
                SendResult result = producer.send(msg);
                System.out.printf("%s%n", result);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 关闭生产者实例
        producer.shutdown();
    }
}

```


消费代码如下：


```java
package example.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class SqlFilterConsumer {

    public static void main(String[] args) throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("default_consumer_group");
        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 订阅 topic ，使用 sql 过滤方式处理
        consumer.subscribe("SqlTopicTest", MessageSelector.bySql(
                "(TAGS is not null and TAGS in ('TagA', 'TagB'))" +
                "and (a is not null and a between 0 and 3)"));

        // 集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for (MessageExt msg: list){
                        String topic = msg.getTopic();
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        String tags = msg.getTags();
                        System.out.printf("收到消息：topic: %s , body: %s, tags: %s%n", topic, body, tags);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}

```


先启动消费者，然后启动生产者，在消费者查看日志：

```
收到消息：topic: SqlTopicTest , body: Hello world0, tags: TagA
收到消息：topic: SqlTopicTest , body: Hello world1, tags: TagB
收到消息：topic: SqlTopicTest , body: Hello world3, tags: TagA
```


**如果在启动消费者时，有以下报错：**

```
    CODE: 1  DESC: The broker does not support consumer to filter message by SQL92
```


那是因为你启动broker的时候，没开启消息过滤。

如何开启呢？
在 `broker.conf` 文件中加入：`enablePropertyFilter = true`
然后启动的时候，还要指定配置文件 `broker.conf`：

        ./mqbroker -n 127.0.0.1:9876 autoCreateTopicEnable=true -c ../conf/broker.conf &







---


以上。