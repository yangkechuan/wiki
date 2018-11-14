## springboot + websocket

---

>*主要实现聊天室功能*

---
### 1.新建项目



`pox.xml` 文件如下：

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

新建websocket配置类文件 `WebSocketConfig.java`

```java
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

主要业务逻辑代码 `WebSocket.java`：

```java
@Data
@ServerEndpoint(value = "/websocket")
@Component
public class WebSocket {

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为 : " + getOnlineCount());
        try {
            sendMessage("您已成功连接！");
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为 : " + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("来自客户端的消息:" + message);

        // 群发消息
        for (WebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }

}
```

增加一个controller,可以跳转到html页面

```java

@Controller
public class WebSocketController {
    
    @GetMapping("/")
    public String socket(){
        return "socket.html";
    }
}

```


前端业务代码 `socket.html`：


```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My WebSocket Test</title>
</head>
<body>

<input id="text" type="text"/>
<button onclick="send()">Send</button>
<button onclick="closeWebSocket()">Close</button>
<div id="message">
</div>

</body>

<script type="text/javascript">

    var websocket = null;

    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8080/websocket");
    }
    else {
        alert('Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("error");
    };

    //连接成功建立的回调方法
    websocket.onopen = function (event) {
        setMessageInnerHTML("open");
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data);
    };

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("close");
    };

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        websocket.close();
    };

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭连接
    function closeWebSocket() {
        websocket.close();
    }

    //发送消息
    function send() {
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
</script>
</html>

```


### 2.运行项目


运行项目，打开浏览器
    
    http://localhost:8080/

打开多个窗口，输入内容，并在各窗口查看信息，信息正常，查看日志：


```log
有新连接加入！当前在线人数为 : 1
来自客户端的消息:1
来自客户端的消息:2
有一连接关闭！当前在线人数为 : 0
有新连接加入！当前在线人数为 : 1
有一连接关闭！当前在线人数为 : 0
有新连接加入！当前在线人数为 : 1
来自客户端的消息:11
有新连接加入！当前在线人数为 : 2
来自客户端的消息:11
来自客户端的消息:22

```


---

以上