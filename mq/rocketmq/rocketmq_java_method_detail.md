## rocketmq 发送和消费时的一些重要方法和属性

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            rocketmq Version: 4.9.3
            rocketmq client Version:4.9.4
---




### 1. 生产者


代码以及方法说明如下：

```java
package example.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProducerDetails {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {

        /*
         * 消息发送时的重要属性
         */

        // 生产者所属组 (针对事务消息，高可用)
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");

        // 默认主题在每个 Broker 上创建的队列数 (只对新创建时有效)
        producer.setDefaultTopicQueueNums(8);

        // 发送消息超时时间，默认 3s (3000ms)
        producer.setSendMsgTimeout(1000 * 3);

        // 消息体超过该值则启用压缩，默认 4K
        producer.setCompressMsgBodyOverHowmuch(1024 * 4);

        // 同步发送消息默认重试次数，默认 2 次，一共发送 3 次
        producer.setRetryTimesWhenSendFailed(2);

        // 异步发送消息默认重试次数，默认 2 次，一共发送 3 次
        producer.setRetryTimesWhenSendAsyncFailed(2);

        // 消息重试时，选择另外一个 broker (消息没有发送成功时，是否选择另外一个 broker) , 默认为 false
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);

        // 允许发送的消息最大长度，默认 4M
        producer.setMaxMessageSize(1024 * 1024 * 4);

        // 设置 nameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");

        // 启动 producer 实例
        producer.start();

        // 获取该消息下的所有消息队列
        List<MessageQueue> messageQueues = producer.fetchPublishMessageQueues("defaultTopic");

        for (MessageQueue messageQueue : messageQueues) {
            System.out.println(messageQueue.getQueueId());
        }

        for (int i = 0; i < 10; i++) {
            Message msg = new Message("defaultTopic", "TagA", "keys", "hello world".getBytes(StandardCharsets.UTF_8));

            // 1. 发送单项消息
            producer.sendOneway(msg);


            // 1.1 指定队列发送单向消息
            producer.sendOneway(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    return mqs.get(0);
                }
            }, null);

            // 1.2 指定队列发送单向消息
            producer.sendOneway(msg, messageQueues.get(0));


            // 2. 同步发送
            SendResult sendResult = producer.send(msg);

            // 2.1 同步发送，设置发送超时时间 (默认是 3 s)
            SendResult sendResult1 = producer.send(msg, 1000 * 3);

            // 2.2 指定队列发送同步消息
            SendResult sendResult2 = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    return mqs.get(0);
                }
            }, null);

            // 2.3 指定队列发送同步消息
            SendResult sendResult3 = producer.send(msg, messageQueues.get(0));


            // 3. 异步发送
            int index = i;
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK  %s%n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception  %s%n", index, e);
                    e.printStackTrace();
                }
            });



            // 3.1 异步发送消息，设置超时时间
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK  %s%n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception  %s%n", index, e);
                    e.printStackTrace();
                }
            }, 1000 * 3);

            // 3.2 异步发送消息，指定队列
            producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    return mqs.get(0);
                }
            }, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK  %s%n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception  %s%n", index, e);
                    e.printStackTrace();
                }
            });
        }


        // 关闭 producer 实例
        producer.shutdown();
    }
}

```


### 2. 消费者

代码以及方法说明如下：

```java
package example.rocketmq;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class ConsumerDetails {

    public static void main(String[] args) throws MQClientException {

        // 消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("defaultConsumerGroup");

        // 指定 nameServer
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // 消费模式，默认集群模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 消费偏移量 (上次消费偏移量，最大偏移量，最小偏移量，启动时间戳)
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        // 消费者最小线程数，默认 20
        consumer.setConsumeThreadMin(20);

        // 消费者最大线程数，默认 20
        consumer.setConsumeThreadMax(20);

        // 推模式下，任务时间间隔
        consumer.setPullInterval(0);

        // 推模式下，每次拉取任务条数
        consumer.setPullBatchSize(20);

        // 消息重试次数，-1 代表 16 次 (超过重试次数后，成为死信消息)
        consumer.setMaxReconsumeTimes(-1);

        // 消息消费超时时间 (消息可能阻塞正在使用线程的最大时间，以分钟为单位)
        consumer.setConsumeTimeout(15);

        // 获取消费者对主题分配了哪些消息队列
        Set<MessageQueue> messageQueueSet = consumer.fetchSubscribeMessageQueues("TopicTest");

        for (MessageQueue messageQueue : messageQueueSet){
            System.out.println(messageQueue.getQueueId());
        }

        // 方法 -- 订阅

        // 1.1 基于主题订阅，消息过滤使用表达式
        consumer.subscribe("TopicTest", "*");

        // 1.2 基于主题订阅，消息过滤使用表达式
        consumer.subscribe("TopicTest", MessageSelector.bySql("a between 0 and 3"));

        // 1.3 基于主题订阅，消息过滤使用表达式
        consumer.subscribe("TopicTest", MessageSelector.byTag("TagA || TagB"));

        // 取消订阅
        consumer.unsubscribe("TopicTest");

        // 注册并发事件监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                try {
                    for (MessageExt msg : msgs){
                        String topic = msg.getTopic();
                        String body = new String(msg.getBody());
                        String tags = msg.getTags();
                        System.out.printf("topic : %s , body : %s , tags : %s %n", topic, body, tags);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    // 没有成功，到重试队列中
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });


        // 注册顺序事件监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);
                for (MessageExt msg : msgs){
                    System.out.printf("consumerThread : %s, queueId : %s, content: %s",
                            Thread.currentThread().getName(), msg.getQueueId(), new String(msg.getBody()));
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(300));
                } catch (Exception e) {
                    e.printStackTrace();
                    // 消费异常时，先等一会，一会再处理，而不是立刻放到重试队列中。只有这样，才能保证消费的有序性
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();

    }
}

```




---


以上。