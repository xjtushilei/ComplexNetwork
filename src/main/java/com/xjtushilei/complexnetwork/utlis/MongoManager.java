package com.xjtushilei.complexnetwork.utlis;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB的驱动
 * 数据库配置文件要改哦
 *
 * @author shilei
 * @date 2017年1月7日20:18:15
 */

public class MongoManager {
    private static final String username = "";
    private static final String passwd = "";
    private static final String whenCreateDatabaseName = "admin";
    private static final Integer soTimeOut = 300000;
    private static final Integer connectionsPerHost = 500;
    private static final Integer threadsAllowedToBlockForConnectionMultiplier = 1000;
    private MongoDatabase mongoDatabase = null;
    private MongoClient mongoClient = null;


    /**
     * 初始化 mongodb
     * 连接数据库，你需要指定数据库名称，如果指定的数据库不存在，mongo会自动创建数据库。
     *
     * @param host
     * @param port
     * @param databaseName
     */
    public MongoManager(String host, int port, String databaseName) {
        mongoClient = new MongoClient(host, port);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
    //	public MongoManager(String host, int port, String databaseName) {
    //
    //		 MongoCredential credential = MongoCredential.createCredential(username, whenCreateDatabaseName, passwd.toCharArray());
    //         List<MongoCredential> credentials = new ArrayList<MongoCredential>();
    //         credentials.add(credential);
    //		mongoClient = new MongoClient(new ServerAddress(host, port),credentials,
    //				new MongoClientOptions.Builder().socketTimeout(soTimeOut).connectionsPerHost(connectionsPerHost)
    //						.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
    //						.socketKeepAlive(true).build());
    //		mongoDatabase = mongoClient.getDatabase(databaseName);
    //	}

    /**
     * 创建集合
     * 我们可以使用 com.mongodb.client.MongoDatabase 类中的createCollection()来创建集合
     *
     * @param CollectionName
     */
    public void createCollection(String CollectionName) {
        mongoDatabase.createCollection(CollectionName);
    }


    /**
     * 插入一个文档
     * <p>
     * 我们可以使用com.mongodb.client.MongoCollection类的 insertMany() 方法来插入一个文档
     *
     * @param CollectionName
     */
    public void insertOneDocument(String CollectionName, Map<String, Object> map) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        Document document = new Document(map);
        collection.insertOne(document);
    }

    /**
     * 插入一个文档
     * <p>
     * 我们可以使用com.mongodb.client.MongoCollection类的 insertMany() 方法来插入一个文档
     *
     * @param CollectionName
     */
    public void insertOneDocument(String CollectionName, String json) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        collection.insertOne(Document.parse(json));
    }


    public long size(String CollectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        return collection.count();

    }


    /**
     * 查询所有的数据
     * 警告：数据量大的时候不要使用！内存和带宽会爆炸！
     *
     * @param CollectionName
     */
    public List<Document> find(String CollectionName) {
        ArrayList<Document> result = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        Bson orderBy = new BasicDBObject("_id", 1);
        FindIterable<Document> findIterable = collection.find().sort(orderBy);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }


    /**
     * @param CollectionName 集合的名字
     * @param pageSize       每一页大小
     * @param page           第几页
     * @return List< org.bson.Document>
     */
    public List<Document> find(String CollectionName, int pageSize, int page) {
        ArrayList<Document> result = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        Bson orderBy = new BasicDBObject("_id", 1);
        FindIterable<Document> findIterable = collection.find().sort(orderBy).limit(pageSize).skip((page - 1) * pageSize);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }

    /**
     * @param CollectionName 集合的名字
     * @param pageSize       每一页大小
     * @param page           第几页
     * @return List<Json String>
     */

    public List<String> findToJsonArray(String CollectionName, int pageSize, int page) {
        ArrayList<String> result = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        Bson orderBy = new BasicDBObject("_id", 1);
        FindIterable<Document> findIterable = collection.find().sort(orderBy).limit(pageSize).skip((page - 1) * pageSize);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next().toJson());
        }
        return result;
    }


    /**
     * 查询前N条数据
     *
     * @param CollectionName
     */
    public List<Document> find(String CollectionName, int n) {
        ArrayList<Document> result = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        Bson orderBy = new BasicDBObject("_id", 1);
        FindIterable<Document> findIterable = collection.find().sort(orderBy).limit(n).skip(0);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }


    /**
     * 插入多个文档
     * <p>
     * 我们可以使用com.mongodb.client.MongoCollection类的 insertMany() 方法来插入一个文档
     *
     * @param CollectionName
     */
    public void insertManyDocument(String CollectionName, List<HashMap<String, Object>> list) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(CollectionName);
        List<Document> documents = new ArrayList<Document>();
        for (HashMap<String, Object> map : list) {
            documents.add(new Document(map));
        }
        collection.insertMany(documents);
    }

    /**
     * 关闭mongoDB
     */
    public void close() {
        mongoClient.close();
    }

}