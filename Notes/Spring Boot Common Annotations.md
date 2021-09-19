# Spring Boot Common Annotations

+ **@Value**

  | 参数  | 含义                             | 格式     |
  | ----- | -------------------------------- | -------- |
  | value | 从配置文件中读取值并注入到变量中 | “${...}” |

  该Annotation可以作用在对象上，以读取配置文件application.properties中的配置值。

  例如下面这个变量使用Value标注，把配置文件中的Redis密码注入到pwd变量中，给Redisson使用。

  ```java
  @Value("${spring.redis.password}")
  ```

+ **@Scope**

    | 参数            | 含义       | 可选值                                             |
    | --------------- | ---------- | -------------------------------------------------- |
    | value/scopeName | Bean作用域 | singleton,prototype,session,global session,request |
    
    该Annotation用于确定Bean的作用域，用以应对不同的应用场景：
    
    + Singleton
    
      即单例，是默认值。全局只维护一个Bean对象。
    
    + Prototype
    
      即多例，每次都会创建新的对象注入。
    
    + Session
    
      每个HttpSession对应一个Bean。如果开两个浏览器去访问标注该作用域的某一controller，则每个浏览器将会各对应一个该controller的bean对象。
    
    + Request
    
      每次发送请求都会创建新对象。

