# 需要
1. java8
2. 下载该分支源码

# 启动步骤
1. 编译工程（过程大约需要2分钟，因为里面的图片太多了，处理小文件太慢），工程根目录运行 `gradlew build`，生成了一个jar文件。在“build/libs/graph-demo.jar”
2. 运行该jar文件即可 `java -jar build\libs\graph-demo.jar`
3. 浏览器浏览 "http://localhost:8087/"

# 其他说明
1. html文件目录：`src\main\resources\static`
2. 索引文件的api文档：http://localhost:8087/swagger-ui.html