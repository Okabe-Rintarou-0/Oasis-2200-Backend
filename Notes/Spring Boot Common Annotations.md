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

