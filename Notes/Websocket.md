# Websocket	

### 基础信息

​		Websocket（以下简称ws）和Http同属于应用层，只不过ws只需要一次握手即可建立连接，而Http协议则需要三次握手。ws是基于TCP的，TCP的握手和ws的握手是不同层次的。TCP的握手用来保证链接的建立，ws的握手是在TCP链接建立后告诉服务器这是个ws链接，服务器你要按ws的协议来处理这个TCP链接。

​		不同于Http协议的无状态(Stateless)连接，ws是有状态(Stateful)连接。一旦服务器和客户端建立起ws连接，（并且通信过程中没有出现中断）两者就能持续不断地进行信息交互。换句话说，不借助额外辅助方式的话（Session/Token），服务器无法记住发送http请求的那一方是谁；而对于ws，一旦建立连接，服务器就能够清晰地知道和自己建立连接的客户端是谁。而且，两者也存在着一个巨大的差异，那就是如果使用http请求获取信息，只能是客户端主动向客户端发起，而使用ws，服务器就可以比较容易地向客户端主动推送信息和数据。一个比较常见的例子就是B站视频播放UI上显示的在线人数，它会每隔一段时间进行刷新。这实际上是服务器端通过ws向前端主动传输数据，前端再解析数据显示出来；其实播放视频也用到了ws，服务器通过ws把图片流主动推送到客户端，实现缓存。

​		两者在速度上，ws显然要快一点。但是要注意，由于http请求是无状态连接，除了session（如果采用此方式的话）会占用一部分内存空间，在请求结束之后，就会释放资源。而ws如果不断开的话，连接信息就不会被释放，也就是说它会占用一定的资源，因而服务器能够承载的ws连接数量是有限的。

### 具体实现

​		Spring为我们很好地集成了ws，具体实现可以参考[Spring ws实现](https://www.cnblogs.com/kiwifly/p/11729304.html)，写的非常详尽。

​		本项目采用了比较规范的STOMP协议。STOMP协议和Http协议类似，也有header和body。STOMP包含多种指令：

| 指令名      | 描述     | 可指定的参数                                 |
| ----------- | -------- | -------------------------------------------- |
| CONNECT     | 连接ws   | accept-version(1.1), heart-beat(10000,10000) |
| DISCONNECT  | 断开ws   | /                                            |
| SUBSCRIBE   | 订阅主题 | destination(主题路径), id                    |
| UNSUBSCRIBE | 取消订阅 | /                                            |
| SEND        | 发送信息 | destination(主题路径)                        |

​		通过STOMP协议，可以实现简易的发布订阅。StompClient通过订阅主题，就可以接受到来自服务器在对应主题下的广播。比如我们游戏里存在着房间系统，玩家创建或者加入房间都会主动订阅路径为"/topics/room/{roomId}"的主题（其中roomId是房间号，非常量），然后服务器可以把战斗信息广播到订阅这个主题的两个客户端。

​	前端代码：（使用的是C#）

```c#
StompMessageSerializer serializer = new StompMessageSerializer();   
public StompCommandSender Subscribe(string url, string clientId)
{
    var sub = new StompMessage("SUBSCRIBE");
    sub["destination"] = url;
    sub["id"] = $"sub-{clientId}";
    ws.Send(serializer.Serialize(sub));
    return this;
}
public StompCommandSender Send(string url, string body = "")
{
    var send = new StompMessage("SEND", body);
    send["destination"] = url;
    ws.Send(serializer.Serialize(send));
    return this;
}
```

​		后端广播信息可以在相应的方法上面使用@SendTo("...")这一annotation，annotation的value即为订阅路径，方法的返回值会被加入Message的body返回给客户端。当然也可以使用Spring为我们提供的模板SimpMessageTemplate，通过@Autowired自动注入，使用convertAndSend函数进行广播。

​		上面所讲的都是多点通信，STOMP其实也可以配合Spring Security进行用户拦截验证+实现单点通信（因为单点通信不同于广播，需要清楚地知道要通信的那个人是谁）。其实单点通信相较于多点广播就多了三个不同点。第一点是客户端必须订阅以/user为前缀的路径。比如说，多点订阅的是"/topics/room/0"，那么单点就必须订阅"/user/topics/room/0"，也就是除了那个前缀后面都和多点一致。第二点是需要把@SendTo换成@SendToUser，convertAndSend换成convertAndSendToUser。第三点就是需要使用Spring Security的拦截器，并自定义继承Principal类的用户认证类。

​		详情可以参考[springboot websocket convertAndSendToUser这个方法中的user到底是从哪里来的？](https://segmentfault.com/q/1010000015140531)

​		ws除了发布订阅，信息传输中最常用的还有心跳检测机制。客户端和服务器每隔一段时间都给对方发心跳包，证明自己还“活着”。如果服务器隔了一定时间没收到心跳包，则可以进行相应的处理，比如回收ws资源等。这一过程叫做“pingpong”。

​		华为云的心跳包长这样：

```json
{"publisher_id":"heartbeat","message":1630288750503}	
```

​		message里面应该是时间戳。该实例可以为心跳包数据结构设计提供参考。