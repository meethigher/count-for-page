# [网页访问人数统计脚本](https://meethigher.top/blog/2021/graduation/)

之所以要实现这个脚本，还是受[不蒜子](http://busuanzi.ibruce.info/)启发。

我从2019年，就开始使用不蒜子了，但是2020年末，我发现不蒜子有一个问题。就是在IOS端跟PC端，数据总是不变，查看接口返回内容，就是一个一成不变的数据。

我怀疑是不蒜子后台的逻辑可能出了问题，因为网上也查不到相关资料，所以就打算自己实现一个。

正好今天公司停电，不上班，所以就花时间完成了这个脚本。

环境

1. Java
2. SQLite

使用

0. 创建SQLite数据库，路径在application-dev.properties下面修改

1. 启动java项目之后，浏览器访问http://localhost:9090/，出现跳转页面，说明启动成功
2. 用Postman发送post请求到http://localhost:9090/count，请求体内容是要**统计的url**，后台会**根据ip**进行计数统计。后台记录该ip第一次请求的设备、时间

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