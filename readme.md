# 中国娱乐圈关系挖掘

实现了中国娱乐圈的关系分析和可视化工作，可以快速的查询明星之间的关系。This is a complex network of course assignments. The realization of the relationship analysis and visualization of China's entertainment industry, you can quickly query the relationship between the stars

更多介绍请移步 [other.md](other.md)

# 效果预览

因服务器不是个人的，2年后不知道还能访问不！

**[在线预览](http://aliyun.xjtushilei.com:6088/index.html)**


# 环境准备
1. jdk1.8
1. mongodb 3.6及以上
1. Tomcat 8及以上
1. neo4j 社区版（非个人桌面版）安装教程官网就有，注意事项是
    安装此软件需要安装到（Windows service），安装成功后打开 http://localhost:7474/ 然后设置新密码


# 编译项目
1. 下载源码
2. 解压后按需修改`src\main\java\com\xjtushilei\complexnetwork\config`中的neo4j和mongodb数据库口令配置

3. 执行 `gradlew build`（ 不用单配gradle）编译打包，完成后在 build 目录下的libs将生成 ROOT.war
# 部署
1. 将 war 包部署到 Tomcat 的 webapps 目录下，然后启动服务器，注意查看 logs 目录下的日志情况，是否有报错
2. post访问 http://localhost:8080/api/init 进行数据插入到neo4j数据库中，一次插入，终身体验
3. 浏览器访问 http://localhost:8080/（tomcat默认） ，开始体验




# 系统截图
   
   **部分算法**
   
   ![](/img/net1.png)
   
   **展示所有路径**
   
   ![](/img/net2.png)
   
   **统计功能**
   
   截止2017年5月2日20:11:25已经有这么多独立ip访问！这么多次浏览！
   
   ![](/img/net3.png)





