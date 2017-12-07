package com.xjtushilei.complexnetwork.API;


import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.xjtushilei.complexnetwork.config.config;
import com.xjtushilei.complexnetwork.utlis.MongoManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 这是日志的页面的两个功能。
 * 1、显示历史记录
 * 2、显示一些统计数据
 * 3、显示热词。top-n
 *
 * @author shilei
 * @date 2017年1月16日14:26:54
 */

@RestController
@RequestMapping("/log")
public class LogAPI {

    public static void main(String[] args) {
        new LogAPI().getcount();
    }

    @GetMapping("/getHistoryList")
    public HashMap<String, Object> get(
            @RequestParam(value = "pageNumber", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        LinkedList<Map<String, Object>> result = new LinkedList<>();

        // 创建芒果DB的驱动
        MongoManager manager = new MongoManager(config.MongoDB_IP, config.MongoDB_Port, config.MongoDB_DataBase_logs);
        MongoCollection<Document> collection = manager.getMongoDatabase().getCollection(config.MongoDB_collection_logs);

        Long total = collection.count();

        // 按照日期查询历史记录
        Bson orderByDate = new BasicDBObject("time", -1);
        FindIterable<Document> findIterable = collection.find().sort(orderByDate).limit(pageSize)
                .skip((page - 1) * pageSize);

        // 遍历，得到有用的结果
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {

            HashMap<String, Object> map = new HashMap<>();
            Document doc = mongoCursor.next();
            map.put("name1", doc.get("name1"));
            map.put("name2", doc.get("name2"));
            map.put("algorithm", doc.get("algorithm"));
            map.put("usetime", doc.get("usetime"));
            map.put("city", doc.get("city"));
            map.put("ip", doc.get("ip"));
            // 存时间，转换一下
            map.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((long) doc.get("time"))));
            result.add(map);
        }

        HashMap<String, Object> RealReult = new HashMap<>();
        RealReult.put("rows", result);
        RealReult.put("total", total);
        return RealReult;

    }

    @GetMapping("/getCount")
    public HashMap<String, Object> getcount() {

        HashMap<String, Object> RealReult = new HashMap<>();

        // 创建芒果DB的驱动
        MongoManager manager = new MongoManager(config.MongoDB_IP, config.MongoDB_Port, config.MongoDB_DataBase_logs);
        MongoCollection<Document> collection = manager.getMongoDatabase().getCollection(config.MongoDB_collection_logs);

        /**
         * 访问次数
         */
        Long total = collection.count();
        RealReult.put("total", total);

        /**
         * 统计访问人数
         */
        DistinctIterable<String> it = collection.distinct("ip", String.class);
        ArrayList<String> list = new ArrayList<>();
        it.into(list);
        RealReult.put("people", list.size());

        /**
         * 统计平均用时
         */
        List<Bson> pipeline = new ArrayList<Bson>();
        String AvgAgg = "{$group:{_id:'1','avg':{$avg:'$usetime'}}}";
        pipeline.add(Document.parse(AvgAgg));
        AggregateIterable<Document> agg = collection.aggregate(pipeline);
        RealReult.put("avgUsetime", agg.first().get("avg"));

        /**
         * 统计最多搜索的关系
         */
        List<Bson> countpipeline = new ArrayList<Bson>();
        // String countAgg = "{$group:{_id:'$name2','count':{$sum:1}}}";
        String countAgg = "{$group:{_id:{'name2':'$name2','name1':'$name1'},'count':{$sum:1} }}";
        String countSort = "{$sort:{count:-1}}";
        String countLimit = "{$limit:20}"; // 最热的20个词
        countpipeline.add(Document.parse(countAgg));
        countpipeline.add(Document.parse(countSort));
        countpipeline.add(Document.parse(countLimit));
        AggregateIterable<Document> Countagg = collection.aggregate(countpipeline);
        LinkedList<Document> result = new LinkedList<>();
        Countagg.into(result);
        // System.out.println(result.toString());
        RealReult.put("Top", result);

        /**
         * 统计最多搜索的人
         */
        List<Bson> personcountpipeline = new ArrayList<Bson>();
        String personcountagg1 = "{$group:{_id:'$name1','count':{$sum:1}}}";
        String personcountsort = "{$sort:{count:-1}}";
        String personcountlimit = "{$limit:20}"; // 最热的20个词
        personcountpipeline.add(Document.parse(personcountagg1));
        personcountpipeline.add(Document.parse(personcountsort));
        personcountpipeline.add(Document.parse(personcountlimit));
        AggregateIterable<Document> personcountagg = collection.aggregate(personcountpipeline);
        LinkedList<Document> Personresult = new LinkedList<>();
        personcountagg.into(Personresult);
        // System.out.println(Personresult.toString());
        // 第二个人
        List<Bson> personcountpipeline2 = new ArrayList<Bson>();
        String PersoncountAgg2 = "{$group:{_id:'$name2','count':{$sum:1}}}";
        String PersoncountSort2 = "{$sort:{count:-1}}";
        String PersoncountLimit2 = "{$limit:20}"; // 最热的20个词
        personcountpipeline2.add(Document.parse(PersoncountAgg2));
        personcountpipeline2.add(Document.parse(PersoncountSort2));
        personcountpipeline2.add(Document.parse(PersoncountLimit2));
        AggregateIterable<Document> PersonCountagg2 = collection.aggregate(personcountpipeline2);
        LinkedList<Document> Personresult2 = new LinkedList<>();
        PersonCountagg2.into(Personresult2);
        // System.out.println(Personresult2.toString());
        // 加起来
        LinkedHashMap<String, Integer> persontop = new LinkedHashMap<>();
        for (Document document : Personresult) {
            if (persontop.containsKey(document.get("_id").toString())) {
                persontop.put(document.get("_id").toString(),
                        (int) document.get("count") + persontop.get(document.get("_id").toString()));
            } else {
                persontop.put(document.get("_id").toString(), (int) document.get("count"));
            }
        }
        for (Document document : Personresult2) {
            if (persontop.containsKey(document.get("_id").toString())) {
                persontop.put(document.get("_id").toString(),
                        (int) document.get("count") + persontop.get(document.get("_id").toString()));
            } else {
                persontop.put(document.get("_id").toString(), (int) document.get("count"));
            }
        }

        // 排序
        List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(persontop.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                Integer p1 = (Integer) o1.getValue();
                Integer p2 = (Integer) o2.getValue();
                ;
                return p2 - p1;
            }
        });
        /* 转换成新map输出 */
        LinkedHashMap<String, Integer> newMap = new LinkedHashMap<String, Integer>();
        int i = 0;
        for (Map.Entry<String, Integer> entity : infoIds) {
            if (entity.getKey().equals("*")) {
                continue;
            }
            i++;
            if (i > 12) {
                break;   //一共只要12个
            }
            newMap.put(entity.getKey(), entity.getValue());

        }

        RealReult.put("PersonTop", newMap);

        return RealReult;

    }

}
