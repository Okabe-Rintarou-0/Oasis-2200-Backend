# 压力测试

### Jmeter基本使用方法

##### 引用变量

${variable_name} variable_name代表变量名，比如常用的变量：${__threadNum}代表当前线程号。

通常可以通过如下方法快速修改服务器和端口。

![image](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/image.7hnkyld422o0.png)

对于每个Http请求，用${host}和${post}，而不是使用绝对网址，这样在需要更换测试网址的时候就不用一个个修改。

##### JSON提取

使用JSON提取器可以提取Response中的数据并保存到指定变量中，比如下面的JSON提取器将提取登录之后服务器返还的token(JWT)，并保存到变量token中。可以通过${token}获取变量token的值。

![image](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/image.22y3jokl4tu.png)

##### BeanShell处理器

在Beanshell前置/后置处理器中可以进行各种处理工作。

