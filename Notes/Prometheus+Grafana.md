# Prometheus+Grafana

### 基本框架

![1174516-fa12369d3edc1c28](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/1174516-fa12369d3edc1c28.310hunqz0pa0.webp)

### JVM相关知识

+ Heap区

  JVM的Heap区中存储的对象会被分为新生代和老生代。为什么需要分成新生代和老生代？因为很多对象都是朝生夕死，一被实例化出来就被要被回收。如果不分区的话，每次GC（Garbage Collection）都要扫描整个内存区域，这会耗费大量时间。

  + **Eden区**

    Eden区，即伊甸园，听名字就能大概知晓其含义。刚创建的对象会被放到Eden区，下一个GC周期，Eden区中未被回收的对象会进入survivor区。这一过程被称作“**Minor GC**”。

  + **Survivor区**

    Survivor区分为From Survivor和To Survivor。Eden区里幸存的对象（在GC中未能被回收的对象）会被放入To Survivor区，而From Survivor区中幸存的对象又会被放入To Survivor区，然后两者交换身份(上一个周期的To Survivor在这一周期变成From Survivor)这样就能始终保证一个survivor区是空的。这么做的好处是可以很方便地进行内存复制，而且不会产生碎片。

  + **Old Gen区**

    最后是Old Gen区，即老生代。每次GC幸存的对象都会增加年龄，到达一定阈值之后就会进入老生代当中。Survivor区满了的话也会把对象转移到Old Gen区，Eden区中未被回收的**大型对象**也会被放入老生代，以保证Survivor空间足够。当老生代也满了的时候，会进行**Major GC/Full GC**，扫描整个heap进行GC。

  heap区会根据实际情况动态调整大小，具体参数可配置。

  在Grafana可视化界面中对应的分区：

![QQ截图20210912223448](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210912223448.7iz8li7et2o0.png)

