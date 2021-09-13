# 整体工作流程(Overall WorkFlow)

### 集群概述

我们的后端服务器一共有两台，采用Nginx+Redis通过Cookies一致性Hash进行负载均衡和房间共享，部署视图如下所示：

![deployment](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/deployment.3mqhw42fryu0.png)

玩家向服务器发起的Http/Websocket请求将会先通过Nginx服务器，Nginx将会根据Cookies中的"roomId"这一个键值进行一致性Hash，分配到集群中的两台服务器上，这两台服务器的内网IP分别为192.168.0.54和192.168.0.51。华为云中集群的网络拓扑图如下所示：

![QQ截图20210912224808](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/QQ截图20210912224808.78005krvcxk0.png)

### 基于JWT的用户信息认证

由于JWT非常适用于集群下的单点用户认证，而且安全性较高，该系统采用了JWT进行用户认证和非法请求拦截。

### 基于Redis的房间系统

我们希望构建出一个独立于服务器的共享房间，即我们希望在客户端的视角，服务器只有一个，也只有一套房间系统。无论创建房间或加入房间的Http请求被发往了哪台服务器，其结果应该就和一台服务器是一致的。

Redis满足了我们的设计需求。通过Redis服务器，两台服务器能够共享一份房间信息（缓存在内存中）。所以，无论请求发往的是哪台服务器，只要两台服务器都对共享的Redis缓存进行相应操作，那么就能实现共享房间的效果。

房间系统的结构如下：

+ 10个房间分区，分别对应Redis中的键:section_0 ~ section_9。分散存储房间，一个分区有50个房间，防止redis的value值过大。
+ Deleted队列，对应Redis中的键deleted。当房间被删除的时候会被加入这个队列。在创建房间的时候会优先选取队头的房间进行创建。
+ Created队列，对应Redis中的键created。当房间被创建的时候会被加入这个队列。在加入房间的时候会选取队头房间进行加入。若该队列为空，则无法加入房间（没有可加入的房间）
+ RoomIdx，上一个创建的房间ID号，是个整数，用于房间创建（若Deleted队列为空，则像Clock Algorithm一样顺时针创建房间）
+ 用户所在房间号，Map<String, Integer>，对应Redis中的键users。

### Redisson分布式锁

由于Redis中的房间系统是两台服务器共享的，所以必然会产生数据冲突的问题（读和写冲突）。所以这时候就需要一把分布式锁，保证一方操作的时候另一方阻塞，直到一方操作结束。

为了保证系统的效率，我们采用了效率较高的Redisson分布式锁，并保证锁的粒度尽可能地小。

Redisson原理参考：

+ [怎样实现redis分布式锁？](https://www.zhihu.com/question/300767410/answer/1749442787)
+ [Redis分布式锁-这一篇全了解(Redission实现分布式锁完美方案)](https://blog.csdn.net/asd051377305/article/details/108384490)

### Nginx Cookies一致性Hash

一致性Hash保证了Hash的均衡性，保证请求能够尽可能均衡地被发送到集群中的各个服务器上。

本系统采用Nginx，通过读取Cookies中的roomId值，进行一致性Hash，保证同一个房间对战的玩家，其请求都被发往同一台服务器。尤其是websocket连接，如果两台客户端的websocket连在了两台服务器进行对战，那还要进行数据的转发。这对于玩家这种需要发送和接受大量数据的应用场景是得不偿失的。所以通过上诉方法，可以保证处于同一房间客户端的websocket连接的是同一台服务器，这样就能较为容易地进行帧同步和状态同步的数据收发、数据广播。

### Nginx配置文件

![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.6v86dwz8eiw0.png)

### 基于STOMP协议的Websocket

该系统采用规范的STOMP协议。该协议可以很方便地实现发布订阅的功能。参与战斗的玩家都会有一个房间号，玩家只需要订阅该房间对应的主题(Topic)即可接受、发送战斗信息。

### 房间系统大致流程

+ 客户端1发送创房请求，创建成功，获取房间ID；此时Redis中更新房间系统；客户端1带上含有房间ID的Cookies发送websocket请求，Nginx通过一致性Hash分配到某一服务器，比如(192.168.0.51)，完成和服务器的websocket连接。

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.1aln8wy54tmo.png)

+ 客户端2发送入房请求，加入成功，获取房间ID；此时Redis中更新房间系统；客户端1带上含有房间ID的Cookies发送websocket请求，Nginx通过一致性Hash分配到某一服务器，比如(192.168.0.51)，完成和服务器的websocket连接，此时两台处于同一房间的客户端均和同一台服务器建立了websocket连接。

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.1y0w3dti0m0w.png)

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.2ns848j2lds0.png)

此时即可开始通过websocket进行战斗数据的转发和广播。

### 基于Redis和Websocket的聊天系统

玩家可以通过发布订阅的方式很轻松的发送和接收聊天信息。由于有两个服务器，我们希望能够实现跨服聊天，故通过redis自带的MQ进行消息的转发。玩家发送到服务器A的聊天信息会被转发到服务器B，然后服务器A和服务器B分别向订阅聊天主题的客户端广播聊天信息。

### 聊天系统大致流程

+ 玩家登陆，获取token。

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.1krmwv7sndy8.png)

+ 客户端1进行websocket连接。
  ![image-20210913093051502](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image-20210913093051502.51rccizpn3k0.png)

+ 客户端2进行类似操作。

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.658klvkwank0.png)

+ 进行跨服聊天

  ![image](https://cdn.jsdelivr.net/gh/Okabe-Rintarou-0/web-images@master/books/image.6sizrhk4gzk0.png)
