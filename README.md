# [网页访问人数统计脚本](https://meethigher.top/blog/2021/count-for-page/)

更新记录

* 2021-09-21：中秋快乐！优化接口调用速度，耗时操作交给后台执行，不阻塞主线程

* 2021-08-05：删除referer。新增通过Java模板引擎freeMarker渲染统计页面

* 2021-07-23：新增通知功能、自动获取ip地址。添加获取referer

* 2021-07-19：初始化count-for-page，仅包含统计功能

[meethigher/count-for-page: 类似于“不蒜子”的统计功能，根据ip来统计页面访问人数](https://github.com/meethigher/count-for-page)

之所以要实现这个脚本，还是受[不蒜子](http://busuanzi.ibruce.info/)启发。

我从2019年，就开始使用不蒜子了，但是2020年末，我发现不蒜子有一个问题。就是在IOS端跟PC端，数据总是不变，查看接口返回内容，就是一个一成不变的数据。

在旧版的安卓Chrome浏览器中，数据是正确的，换成新版之后，又出问题了。

我怀疑是不蒜子后台的逻辑可能出了问题，因为网上也查不到相关资料，所以就打算自己实现一个。

正好今天公司停电，不上班，所以就花时间完成了这个脚本。

环境

1. Java
2. SQLite
   * 一开始我是想用记事本，主要是直接持久化到硬盘，不会浪费太多内存。想了很久，想实现类似于外键这种功能，还真不好整
   * SQLite，一款自给自足、无服务器、无配置的数据库，不就是一个记事本嘛。解决了占用内存过大的问题。

使用

0. 创建SQLite数据库，路径在application-dev.properties下面修改

1. 启动java项目之后，浏览器访问[http://localhost:9090/](http://localhost:9090/)，出现跳转页面，说明启动成功
2. 用Postman发送post请求到[http://localhost:9090/count](http://localhost:9090/count)，请求体内容是要**统计的url**，后台会**根据ip**进行计数统计。后台记录该ip第一次请求的设备、时间、来源

页面访问时，前端页面在所有资源加载完毕之后，携带当前网页url，开始执行ajax请求，获取访问总人数。

![](https://meethigher.top/blog/2021/count-for-page/1.png)

后台的数据如下，两张表通过vId来进行关联。

![](https://meethigher.top/blog/2021/count-for-page/2.png)

遇到的难题

1. ajax访问同站不同端口跨域，添加配置类允许跨域即可
2. https发送ajax时，目标必须为https
   * 这个地方，我一开始是通过nginx启动443端口配置https，反向代理apache9090端口和tomcat80端口，但是有点麻烦。
   * 目前使用的方法是，apache配置443端口配置https，tomcat配置9090端口配置https，占用内存会稍微小点。
3. 记录访问的来源，也就是referer时，比较麻烦。比如客户端A访问网页B，B通过ajax获取C上的数据，这就导致C直接获取referer是获取到`B->C`的。为了解决这个问题，B通过`document.referrer`获取来源，添加请求头，发送个C获取
4. 接口访问过慢。慢的原因就是在于多次进行io，同时又调用了第三方api，就如下面的这三步。通过分析，发现耗时主要在第二步，调第三方api时，会比较耗时，为了解决这个问题，采用了jdk8提供的CompletableFuture。这样就解决了不少问题。
   * 查询：根据访问url查询是否有访问量，有则自增，无则记录
   * 获取ip信息：通过request获取到ip，再调用第三方api获取ip的地理位置
   * 入库：将以上所有获取到的数据入库

![通过CompletableFuture实现后台运行](https://meethigher.top/blog/2021/count-for-page/3.png)

至于java模板引擎，我也是第一次接触，spring推荐Thymeleaf，但我个人感觉freeMarker语法更简单一点。直接放上上手的例子。

pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```

application.properties

```properties
spring.freemarker.template-loader-path=classpath:/static
# 关闭缓存，及时刷新，上线生产环境需要修改为true
spring.freemarker.cache=true
spring.freemarker.charset=UTF-8
spring.freemarker.check-template-location=true
spring.freemarker.content-type=text/html
spring.freemarker.expose-request-attributes=true
spring.freemarker.expose-session-attributes=true
spring.freemarker.request-context-attribute=request
spring.freemarker.suffix=.ftl
```

控制器中，ModelMap其实是存储的SessionScope，这个参照SpringMVC博客中的@ModelAttribute注解

> 控制器如果使用@RestController，就会将返回内容原封不动的打印到接口里。
>
> 如果使用@Controller，就会模板引擎返回到指定的返回字符串的名称的模板、JSP、HTML里

```java
@Controller
public class HtmlController {
    @Autowired
    CountService countService;
    @GetMapping(value="/today")
    public String today(ModelMap map){
        List<TopResponse> top = countService.getTop();
        if(!ObjectUtils.isEmpty(top)){
            String time = new SimpleDateFormat("MM月dd日").format(new Date());
            map.put("title",time+"统计"+top.size()+"条");
            map.put("today",top);
        }
        return "/index";
    }
}
```

index.ftl模板。接口返回的内容可能会有null值，使用freemarker提供的判空语法。

title不存在时，默认值为null

```
${title!"null"}
```

if判断

```
<#if today??>
//TODO: today存在时
<#else >
//TODO: today不存在时
</#if>
```

完整的index.ftl模板

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>${title!"null"}</title>
    <link rel="stylesheet" href="layui/css/layui.css">
</head>
<body>
<#--https://www.cnblogs.com/panchanggui/p/9342246.html-->
<#if today??>
    <table class="layui-table">
        <colgroup>
            <col width="150">
            <col width="150">
            <col width="150">
            <col>
            <col>
            <col>
        </colgroup>
        <thead>
        <tr>
            <th>ip</th>
            <th>位置</th>
            <th>时间</th>
            <th>设备</th>
            <th>访问</th>
            <th>来源</th>
        </tr>
        </thead>
        <tbody>
        <#list today as item>
            <tr>
                <td>${item.ip!"null"}</td>
                <td>${item.location!"null"}</td>
                <td>${item.firstVisitTime!"null"}</td>
                <td>${item.userAgent!"null"}</td>
                <td>${item.url!"null"}</td>
                <td>${item.originReferer!"null"}</td>
            </tr>
        </#list>
        </tbody>
    </table>
<#else >
    <h1 align="center">nobody</h1>
</#if>
<script src="layui/layui.js"></script>
</body>
</html>
```

参考文章

* [springboot（服务端接口）获取URL请求参数的几种方法](https://www.cnblogs.com/zhanglijun/p/9403483.html)

* [SpringBoot整合Sqlite数据库流程](https://www.cnblogs.com/zblwyj/p/10668803.html)

* [sqlite配置下载安装及使用教程](https://blog.csdn.net/qq_16093323/article/details/88226397)

* [Spring Data JPA 使用 SQLite](https://blog.csdn.net/rocshaw/article/details/102495924)

* [JPA注解添加唯一约束_](https://blog.csdn.net/qq_38705025/article/details/86636818)

* [java获取请求的url地址 ](https://www.cnblogs.com/pxblog/p/10523013.html)

* [用Java来获取访问者真实的IP地址](https://blog.csdn.net/qq_36411874/article/details/79938439)

* [原生JS实现ajax 发送post请求 ](https://www.cnblogs.com/e0yu/p/7055347.html)

* [nginx配置ssl证书实现https ](https://www.cnblogs.com/zhoudawei/p/9257276.html)

* [Apache设置反向代理](https://blog.csdn.net/zhangrouzhu/article/details/80986082)

* [SpringBoot配置SSL证书HTTPS详细流程](https://blog.csdn.net/qq_20051535/article/details/108249482)

* [SpringBoot整合freemarker_haozz的博客](https://blog.csdn.net/hz_940611/article/details/80706772)

* [springboot整合freemarker ](https://www.cnblogs.com/lixianguo/p/12519012.html)

* [jquery ajax 设置header的方式_会飞的程序猿-CSDN博客](https://blog.csdn.net/zzk220106/article/details/81316092?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0.no_search_link&spm=1001.2101.3001.4242)
* [XMLHttpRequest.setRequestHeader() - Web API 接口参考 | MDN](https://developer.mozilla.org/zh-CN/docs/Web/API/XMLHttpRequest/setRequestHeader)
* [JavaScript中document.referrer的用法详解_基础知识_脚本之家](https://www.jb51.net/article/117739.htm)
* [: Failed to execute 'setRequestHeader' on 'XMLHttpRequest': The object's state must be OPENED_liukai6的博客-CSDN博客](https://blog.csdn.net/liukai6/article/details/84887091)
* [springboot集成freemarker属性配置（不知道是针对于某个版本，2.0后有变动） - 小破孩楼主 - 博客园](https://www.cnblogs.com/zouhong/p/12015523.html)
* [freemarker中对null值问题的处理 - panchanggui - 博客园](https://www.cnblogs.com/panchanggui/p/9342246.html)