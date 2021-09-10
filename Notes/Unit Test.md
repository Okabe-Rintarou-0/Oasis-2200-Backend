# 单元测试

### 基本信息

一个好的软件开发流程离不开软件测试这一关，软件测试是软件开发中非常重要的一环，一个好的程序员必须掌握的一项技能。

单元测试是软件测试中的一个分支。单元测试通过对软件程序的各个单元进行白盒/黑盒测试，检验软件是否达到实现的功能。“单元测试（unit testing），是指对软件中的最小可测试单元进行检查和验证。对于单元测试中单元的含义，一般来说，要根据实际情况去判定其具体含义，如C语言中单元指一个函数，Java里单元指一个类，图形化的软件中可以指一个窗口或一个菜单等。总的来说，单元就是人为规定的最小的被测功能模块。单元测试是在软件开发过程中要进行的最低级别的测试活动，软件的独立单元将在与程序的其他部分相隔离的情况下进行测试”。

基本上每个主流语言都有一套较为成熟的单元测试框架，比如Java的主流单元测试框架JUnit。

IDEA和Spring Boot很好地集成了JUnit，可以很方便地进行软件单元测试，并且获取测试用例的覆盖率。一个优秀的测试程序应尽可能达到100%的语句覆盖率。

### Mockito

一个优秀的软件系统总是会将各个功能模块解耦，分成很多个层。比如Web开发通常分成：

Controller=>Service=>Dao=>Repository

计算机界有一句名言：没有什么是加一层解决不了的问题，如果有，那就再加一层。上面这个是最基础的分层，真正的系统的层数会比这多得多。比如Dao层上面还可能加一层Dto层。

如此多的分层对测试也会带来一定的难度，所以Mockito由此而生。Mockito通过Mock（模拟）对象的行为（用户可自定义），以屏蔽待测试的层下面所有的层。即：如果我测试的是Service层，那我可以利用Mockito模拟Dao层的行为。Dao层内部，乃至Dao以下的所有层的实现方式我全都不关心，因为测试的时候并不会真正执行这些行为，而是会按照用户给定的行为执行。这实际上有点类似黑匣子，将较低层的实现进行了屏蔽。这给测试带来了极大便利，程序员可以很容易地写出相应的测试代码。

而且在逻辑上，这种测试方式也不存在问题。如果最外层和最低层都不采用Mockito进行测试，真正应用Spring Boot环境进行测试，并且通过了测试。而这些中间层又通过Mockito测试，那么就可以将这些测试串联起来，证明整套系统的可行性、正确性。

### Mock的好处

- **提前创建测试，TDD（测试驱动开发）**

> 这是个最大的好处吧。如果你创建了一个Mock那么你就可以在service接口创建之前写Service Tests了，这样你就能在开发过程中把测试添加到你的自动化测试环境中了。换句话说，**模拟使你能够使用测试驱动开发。**

- **团队可以并行工作**

> 这类似于上面的那点；为不存在的代码创建测试。但前面讲的是开发人员编写测试程序，这里说的是测试团队来创建。当还没有任何东西要测的时候测试团队如何来创建测试呢？**模拟并针对模拟测试！这意味着当service借口需要测试时，实际上QA团队已经有了一套完整的测试组件**；没有出现一个团队等待另一个团队完成的情况。这使得模拟的效益型尤为突出了。

- **你可以创建一个验证或者演示程序。**

> **由于Mocks非常高效，Mocks可以用来创建一个概念证明，作为一个示意图，或者作为一个你正考虑构建项目的演示程序。**这为你决定项目接下来是否要进行提供了有力的基础，但最重要的还是提供了实际的设计决策。

- **为无法访问的资源编写测试**

> 这个好处不属于实际效益的一种，而是作为一个必要时的“救生圈”。有没有遇到这样的情况？当你想要测试一个service接口，但service需要经过防火墙访问，防火墙不能为你打开或者你需要认证才能访问。遇到这样情况时，**你可以在你能访问的地方使用MockService替代，这就是一个“救生圈”功能。**

- **Mock 可以交给用户**

> 在有些情况下，某种原因你需要允许一些外部来源访问你的测试系统，像合作伙伴或者客户。这些原因导致别人也可以访问你的敏感信息，而你或许只是想允许访问部分测试环境。在这种情况下，如何向合作伙伴或者客户提供一个测试系统来开发或者做测试呢？最简单的就是提供一个mock，无论是来自于你的网络或者客户的网络。soapUI mock非常容易配置，他可以运行在soapUI或者作为一个war包发布到你的java服务器里面。

- **隔离系统**

> **有时，你希望在没有系统其他部分的影响下测试系统单独的一部分。由于其他系统部分会给测试数据造成干扰，影响根据数据收集得到的测试结论。**使用mock你可以移除掉除了需要测试部分的系统依赖的模拟。当隔离这些mocks后，mocks就变得非常简单可靠，快速可预见。这为你提供了一个移除了随机行为，有重复模式并且可以监控特殊系统的测试环境。

转载自[Mockito浅谈](https://www.jianshu.com/p/77db26b4fb54)

## 常用Annotations

+ **@Test**

  该Annotation可以直接作用于测试方法上。被标注的方法可以像类中的main函数一样直接运行（IDEA会在方法左边显示一个绿色小三角，点击即可运行）

  ![QQ截图20210910104806](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210910104806.2hjxa4vtk3w0.png)

+ **@Display**

  | 参数  | 含义           |
  | ----- | -------------- |
  | value | 测试显示的标题 |

  该Annotation可以直接作用于测试方法上，被标注的方法会在测试的时候显示如下小标题，主要是辅助测试，增加测试的可读性，易用性，帮助其他程序员更好理解测试内容。

  ![QQ截图20210910105216](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210910105216.13iix35ipqf4.png)

+ **@BeforeEach**

  该Annotation可以直接作用于方法上，可以用于做一些初始化工作。例如如下代码，该setUp函数用于初始化Mockito。

  ```java
      @BeforeEach
      public void setUp() {
          MockitoAnnotations.initMocks(this);
          Mockito.when(roomContext.addRoom(-1)).thenReturn(-1);
          Mockito.when(roomContext.addRoom(0)).thenReturn(0);
  
          Mockito.when(roomDao.getRoomInfo(0))
              .thenReturn(new RoomDto(0, 0, "lzh", new HashSet<>()));
  
          Mockito.when(roomContext.addRandomRoomMember(0)).thenReturn(true);
          Mockito.when(roomContext.addRandomRoomMember(-1)).thenReturn(false);
      }
  ```

+ **@Mock**

  Mockito中最常用的Annotation之一，官方解释如下：

  > Mark a field as a mock.
  > + Allows shorthand mock creation.
  > + Minimizes repetitive mock creation code.
  > + Makes the test class more readable.
  > + Makes the verification error easier to read because the field name is used to identify the mock.

  个人理解，由于采用Mockito并不会用到Spring Boot环境，而是会运行在一个虚拟的环境中。所以Mock之于Mockito就像Bean之于Spring Boot。只需要把测试单元中用到的、自动注入的(@Autowired)对象全部换成@Mock注解即可完成Mock的工作，非常简单。

  下文将会提到如何自定义被Mock的对象的行为。

+ **@InjectMocks**

  很好理解，就是将被@Mock标注的对象注入到@InjectMocks标注的对象当中。

  一个简单的例子就是：

  在Spring Boot下：

  ```java
  @Service
  public class UserServiceImpl implements UserService{
  	@Autowired
  	private UserDao userDao;    
  	
      ......
  }
  ```

  如果我们需要用Mockito测试UserServiceImpl，即可这样：

  ```java
  @Mock
  private UserDao userDao;
  
  @InjectMocks
  private UserService userService;
  ```

+ **@MockBean**

  MockBean和Mock类似，但是有区别，官方解释如下：

  > Annotation that can be used to add mocks to a Spring ApplicationContext. Can be used as a class level annotation or on fields in either @Configuration classes, or test classes that are @RunWith the SpringRunner.
  >
  > Mocks can be registered by type or by bean name. Any existing single bean of the same type defined in the context will be replaced by the mock, if no existing bean is defined a new one will be added.
  >
  > When @MockBean is used on a field, as well as being registered in the application context, the mock will also be injected into the field.

+ **@SpringBootTest**

  | 参数           | 含义         |
  | -------------- | ------------ |
  | webEnvironment | 指定测试环境 |

  常用：webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT，这样测试就会**开启随机端口进行测试。**

  该Annotation可应用于测试类。标注了该Annotation的类在测试时会运行在Spring Boot环境下。每次运行都会部署一遍Spring Boot环境，因而速度会比Mockito慢很多。但是一般会配合TestRestTemplate发送真正的Http请求进行测试，并且会真正地执行程序的相应语句，而不是基于Mock模拟的行为。一般用在最外层（Controller层）的单元测试。

+ **@WebMvcTest**

  > 1 这个注解仅用于Controller层的单元测试。默认情况下会仅实例化所有的Controller，可以通过指定单个Controller的方式实现对单个Controller的测试。
  >  2 同时，如果被测试的Controller依赖Service的话，需要对该Service进行mock,如使用@MockBean
  >  3 该注解的定义中还包括了@AutoConfigureMockMvc注解，因此，可以直接使用MockMvc对被测controller发起http请求。当然这过程中是不会产生真实的网络流量的。

  摘自[@SpringBootTest和@WebMvcTest并用？](https://www.jianshu.com/p/4a8326d89991)

  该Annotation可以应用于Controller的单元测试类上，并且可以对Http请求进行Mock。

  一个例子：

  ```java
  @WebMvcTest(CombatController.class)
  public class CombatControllerUnitTest {
      @MockBean
      private CombatService combatService;
  
      @MockBean
      private CombatCacheService combatCacheService;
  
      @Autowired
      private MockMvc mockMvc;
  
      @Test
      @DisplayName("测试接受战斗")
      public void testAcceptCombat() throws Exception {
          //if not accepted;
          given(combatService.acceptCombat(-1)).willReturn(false);
          //可以通过如下方式指定Session内容，用于处理装配了Http Session登录拦截的情况
          MvcResult result = mockMvc.perform(
                  get("/accept")
                          .sessionAttr("userId", 0)
                          .sessionAttr("userIdentity", 0)
                          .sessionAttr("username", "lzh")
          )
                  .andReturn();
          String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
          System.out.println(response);
          JSONObject jsonObject = JSON.parseObject(response);
          Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_INVALID);
  
          given(combatService.acceptCombat(-1)).willReturn(true);
          result = mockMvc.perform(
                  get("/accept")
                          .sessionAttr("userId", 0)
                          .sessionAttr("userIdentity", 0)
                          .sessionAttr("username", "lzh")
          )
                  .andReturn();
          response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
          System.out.println(response);
          jsonObject = JSON.parseObject(response);
          Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
      }
  }
  ```

+ **@AutoConfigureTestDatabase**

  | 参数    | 含义                                |
  | ------- | ----------------------------------- |
  | replace | What the test database can replace. |

  | Enum Constant and Description                                |
  | :----------------------------------------------------------- |
  | `ANY`  Replace the DataSource bean whether it was auto-configured or manually defined. |
  | `AUTO_CONFIGURED`  Only replace the DataSource if it was auto-configured. |
  | `NONE`  Don't replace the application default DataSource.    |

一般：

```java
// 下面这句annotation的意思是使用真正的数据库源而不是使用虚拟数据库。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
```

+ **@Rollback**

  | 参数  | 含义                       |
  | ----- | -------------------------- |
  | value | 是否回滚（默认为**true**） |

+ **@DataMongoTest**

  MongoDB测试。

+ **@DataJpaTest**

  JPA测试。

### 断言

单元测试中常常会使用断言（Assertion）来对测试结果进行判定。常用的Assertion方式有两个：

+ Assertions

  ```java
  import org.junit.jupiter.api.Assertions
  ```

  这个模块使用起来非常简单：

  + Assertions.assertEquals
  + Assertions.assertNull
  + Assertions.assertNotNull
  + Assertions.assertFalse
  + Assertions.assertTrue
  + ......

  参数有两个，分别是Expected和Actual，很好理解。

+ MatcherAssert

  ```java
  import org.hamcrest.MatcherAssert;
  ```

  ​	该模块比Assertions要更灵活一点，可以自定义Matcher。

  + MatcherAssert.assertThat(T actual, Matcher<? super T> matcher)

### 模拟(Mock)

有两种常用方式：

+ Mockito.when(...).thenReturn(...)

  ```java
  import org.mockito.Mockito;
  ```
  
  如果选择这种方式，必须初始化：MockitoAnnotations.initMocks(this);

  举例：

  + ```java
    Mockito.when(userDao.getUserAuthority(1))
                    .thenReturn(testAuthority);
    ```

  + ```java
    Mockito.when(userDao.saveUserAuthority(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
            .thenReturn(new UserAuthority(1, "123", "123", "123", 1));
    ```

+ given(...).willReturn(...)

  ```java
  import static org.mockito.BDDMockito.*
  ```

  举例：

  ```java
  given(roomCacheService.createRoom(0)).willReturn(MessageUtil.createMessage(
  	MessageUtil.STAT_OK, "创建成功")
  );
  ```

### IDEA单元测试使用方法

+ 由此可以测试覆盖率。

![QQ截图20210910140251](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210910140251.5slgzglgq8g0.png)

+ 把controller单元测试类都放在test/java根目录下，可以测试所有代码的覆盖率。

+ 由此可以执行目录下所有测试代码（标有@Test的方法）

![QQ截图20210910140547](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210910140547.2arfs4d07fms.png)

+ 在这里可以设置很多单元测试相关的东西，比如代码覆盖率。

![QQ截图20210910140737](https://raw.githubusercontent.com/Okabe-Rintarou-0/web-images/master/books/QQ截图20210910140737.77ue3uamuh40.png)
