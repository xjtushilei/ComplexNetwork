package com.xjtushilei.complexnetwork.API;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjtushilei.complexnetwork.config.config;
import com.xjtushilei.complexnetwork.dataMining.DataPretreatment;
import com.xjtushilei.complexnetwork.search.Cypher;
import com.xjtushilei.complexnetwork.utlis.MongoManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RequestMapping("/api")
@RestController
public class API {

    public static void main(String[] args) {
        System.out.println(new API().get("鹿晗", "张国立", "default", "0.0.0.0", "西安"));

    }

    @PostMapping("/init")
    public String init() throws IOException {
        long time1 = System.currentTimeMillis();
        DataPretreatment.init();
        long time2 = System.currentTimeMillis();
        return "执行时间：" + (time2 - time1);
    }

    @GetMapping("/getRelation")
    public RelationBean get(
            @RequestParam(value = "name1", defaultValue = "鹿晗") String name1,
            @RequestParam(value = "name2", defaultValue = "张国立") String name2,
            @RequestParam(value = "algorithm", defaultValue = "default") String algorithm,
            @RequestParam(value = "ip", defaultValue = "0.0.0.0") String ip,
            @RequestParam(value = "city", defaultValue = "西安") String city) {

        RelationBean result = null;
        long time1 = System.currentTimeMillis();
        /**
         * 开始搜索
         */
        switch (algorithm) {
            case "个人关系网":
                result = Cypher.closePathByBeo4j(name1);
                break;
            case "随机最短路":
                result = Cypher.ShortestPathsByBeo4jBFS(name1, name2);
                break;
            case "所有最短路":
                result = Cypher.allShortestPathsByBeo4j(name1, name2);
                break;

            default:
                result = Cypher.ShortestPathsByBeo4jBFS(name1, name2);
                break;
        }


        /**
         * 开始处理日志！
         */
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("time", new Date());
        logMap.put("name1", name1);
        logMap.put("name2", name2);
        logMap.put("algorithm", algorithm);
        logMap.put("ip", ip);
        logMap.put("city", city);
        logMap.put("usetime", (System.currentTimeMillis() - time1));
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(logMap);
            // 创建芒果DB的驱动
            MongoManager manager = new MongoManager(config.MongoDB_IP, config.MongoDB_Port,
                    config.MongoDB_DataBase_logs);
            manager.insertOneDocument("Searchlog", json);
            manager.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;

    }

}