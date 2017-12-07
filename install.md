# 环境准备
1. jdk1.8
1. mongodb 3.6及以上
1. Tomcat 8及以上
1. neo4j 社区版（非个人桌面版）安装教程官网就有，注意事项是
    安装此软件需要安装到（Windows service），安装成功后打开 http://localhost:7474/ 然后设置新密码


# 编译项目
1. 下载源码
2. 解压后按需修改`src\main\java\com\xjtushilei\complexnetwork\config`中的neo4j和mongodb数据库口令配置

3. 执行 `gradle build` 编译打包，完成后在 build 目录下的libs将生成 ROOT.war
# 部署
1. 将 war 包部署到 Tomcat 的 webapps 目录下，然后启动服务器，注意查看 logs 目录下的日志情况，是否有报错
2. post访问 http://localhost:8080/api/init 进行数据插入到neo4j数据库中
3. 浏览器访问 http://localhost:8080/（tomcat默认），开始体验

