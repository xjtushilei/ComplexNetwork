package com.xjtushilei.complexnetwork.config;

public class config {
    // neo4j的默认端口，通过http链接
    public static String NEO4J_IP = "bolt://localhost:7687";
    // neo4j用户名（默认）
    public static String NEO4J_USER = "neo4j";
    // neo4j密码，安装过程中会让你修改
    public static String NEO4J_PASSWD = "shilei";
    // 芒果db的ip
    public static String MongoDB_IP = "localhost";
    // 芒果db的端口
    public static int MongoDB_Port = 27017;
    // 芒果db的collection设置
    public static String MongoDB_DataBase_logs = "complexNetwork";
    public static String MongoDB_collection_logs = "Searchlog";

}
