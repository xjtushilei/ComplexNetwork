package com.xjtushilei.complexnetwork.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjtushilei.complexnetwork.API.RelationBean;
import com.xjtushilei.complexnetwork.config.config;
import com.xjtushilei.complexnetwork.dataMining.Relation;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Cypher {
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Cypher");

    public static void main(String[] args) {
        System.out.println(ShortestPathsByBeo4jBellmanFord("林俊杰", "周杰伦").getEdges());
    }

    public static RelationBean getall() {

        RelationBean result = new RelationBean();

        HashSet<Relation> relationSet = new HashSet<Relation>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            ClassLoader classLoader = Cypher.class.getClassLoader();
            File file = new File(classLoader.getResource("relation.json").getFile());
            String json = FileUtils.readFileToString(file);
            relationSet = mapper.readValue(json, new TypeReference<HashSet<Relation>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        String string = "";

        for (Relation relation : relationSet) {

            string = string + "\n" + relation.getName1() + " " + relation.getId1() + " " + relation.getName2() + " "
                    + relation.getId2() + " " + relation.getRelationship();
        }

        try {
            FileUtils.write(new File("D://a.csv"), string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static RelationBean allShortestPathsByBeo4j(String name1, String name2) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {

                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person { name:{name2} }),"
                                + " p = allShortestPaths((ms)-[*]-(cs)) " + "RETURN p;",
                        Values.parameters("name1", name1, "name2", name2));

                logger.info("正在查询：" + name1 + "<->" + name2 + "  的最短路径");

                LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                ArrayList<String> nameList = new ArrayList<>();

                LinkedList<Map<String, Object>> edges = new LinkedList<>();
                while (Statementresult.hasNext()) {

                    Record record = Statementresult.next();
                    /**
                     * 存储节点
                     */

                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        if (!nameList.contains(node.asMap().get("name").toString())) {
                            nameList.add(node.asMap().get("name").toString());
                            oneNode.put("id", node.asMap().get("id").toString());
                            oneNode.put("name", node.asMap().get("name").toString());
                            nodes.add(oneNode);
                        }

                    }

                    /**
                     * 存储边
                     */
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }

                }
                result.setNodes(nodes);
                result.setEdges(edges);
            }
        }
        driver.close();

        return result;
    }

    public static RelationBean ShortestPathsByBeo4jBFS(String name1, String name2) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {

                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person { name:{name2} }),"
                                + " p = allShortestPaths((ms)-[*]-(cs)) " + "RETURN p;",
                        Values.parameters("name1", name1, "name2", name2));

                logger.info("正在查询：" + name1 + "<->" + name2 + "  的最短路径");

                List<Record> records = Statementresult.list();
                LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                ArrayList<String> nameList = new ArrayList<>();

                LinkedList<Map<String, Object>> edges = new LinkedList<>();
                Record record = null;
                Random random = new Random(records.size());
                record = records.get(random.nextInt());
                {

                    /**
                     * 存储节点
                     */

                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        if (!nameList.contains(node.asMap().get("name").toString())) {
                            nameList.add(node.asMap().get("name").toString());
                            oneNode.put("id", node.asMap().get("id").toString());
                            oneNode.put("name", node.asMap().get("name").toString());
                            nodes.add(oneNode);
                        }

                    }

                    /**
                     * 存储边
                     */
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }

                }
                result.setNodes(nodes);
                result.setEdges(edges);
            }
        }
        driver.close();

        return result;
    }

    public static RelationBean ShortestPathsByBeo4jBellmanFord(String name1, String name2) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {

                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person { name:{name2} }),"
                                + " p = allShortestPaths((ms)-[*]-(cs)) " + "RETURN p;",
                        Values.parameters("name1", name1, "name2", name2));

                logger.info("正在查询：" + name1 + "<->" + name2 + "  的最短路径");

                List<Record> records = Statementresult.list();
                LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                ArrayList<String> nameList = new ArrayList<>();

                LinkedList<Map<String, Object>> edges = new LinkedList<>();
                Record record = null;
                if (records.size() >= 3) {
                    record = records.get(2);
                } else {
                    record = records.get(0);
                }
                {

                    /**
                     * 存储节点
                     */

                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        if (!nameList.contains(node.asMap().get("name").toString())) {
                            nameList.add(node.asMap().get("name").toString());
                            oneNode.put("id", node.asMap().get("id").toString());
                            oneNode.put("name", node.asMap().get("name").toString());
                            nodes.add(oneNode);
                        }

                    }

                    /**
                     * 存储边
                     */
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }

                }
                result.setNodes(nodes);
                result.setEdges(edges);
            }
        }
        driver.close();

        return result;
    }

    public static RelationBean ShortestPathsByBeo4j(String name1, String name2, int kind) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {

                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person { name:{name2} }),"
                                + " p = allShortestPaths((ms)-[*]-(cs)) " + "RETURN p;",
                        Values.parameters("name1", name1, "name2", name2));

                logger.info("正在查询：" + name1 + "<->" + name2 + "  的最短路径");

                List<Record> records = Statementresult.list();
                LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                ArrayList<String> nameList = new ArrayList<>();

                LinkedList<Map<String, Object>> edges = new LinkedList<>();
                Record record = null;
                if (records.size() >= kind) {
                    record = records.get(kind - 1);
                } else {
                    record = records.get(0);
                }
                {

                    /**
                     * 存储节点
                     */

                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        if (!nameList.contains(node.asMap().get("name").toString())) {
                            nameList.add(node.asMap().get("name").toString());
                            oneNode.put("id", node.asMap().get("id").toString());
                            oneNode.put("name", node.asMap().get("name").toString());
                            nodes.add(oneNode);
                        }

                    }

                    /**
                     * 存储边
                     */
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }

                }
                result.setNodes(nodes);
                result.setEdges(edges);
            }
        }
        driver.close();

        return result;
    }

    public static RelationBean shortestPathByBeo4j(String name1, String name2) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {
                long time1 = System.currentTimeMillis();
                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person { name:{name2} }),"
                                + " p = shortestPath((ms)-[*]-(cs)) " + "RETURN p;",
                        Values.parameters("name1", name1, "name2", name2));

                logger.info("正在查询：" + name1 + "<->" + name2 + "  的最短路径");
                logger.info("查询用时：" + (System.currentTimeMillis() - time1) + "  ms");
                while (Statementresult.hasNext()) {

                    Record record = Statementresult.next();

                    /**
                     * 存储节点
                     */
                    LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                    ArrayList<String> nameList = new ArrayList<>();
                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        nameList.add(node.asMap().get("name").toString());
                        oneNode.put("id", node.asMap().get("id").toString());
                        oneNode.put("name", node.asMap().get("name").toString());
                        nodes.add(oneNode);
                    }
                    result.setNodes(nodes);

                    /**
                     * 存储边
                     */
                    LinkedList<Map<String, Object>> edges = new LinkedList<>();
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }
                    result.setEdges(edges);
                }

            }
        }
        driver.close();

        return result;
    }

    public static RelationBean closePathByBeo4j(String name1) {

        RelationBean result = new RelationBean();

        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {

            try (Transaction tx = session.beginTransaction()) {
                long time1 = System.currentTimeMillis();
                StatementResult Statementresult = tx.run(
                        "MATCH (ms:Person { name:{name1} }),(cs:Person ), p = (ms)-[*1]-(cs) RETURN p;",
                        Values.parameters("name1", name1));

                logger.info("正在查询：" + name1 + "相邻的路径");
                logger.info("查询用时：" + (System.currentTimeMillis() - time1) + "  ms");
                LinkedList<Map<String, Object>> nodes = new LinkedList<>();
                ArrayList<String> nameList = new ArrayList<>();

                LinkedList<Map<String, Object>> edges = new LinkedList<>();
                while (Statementresult.hasNext()) {

                    Record record = Statementresult.next();
                    /**
                     * 存储节点
                     */

                    for (Node node : record.get("p").asPath().nodes()) {
                        // System.out.println(node.asMap().toString());
                        Map<String, Object> oneNode = new HashMap<>();
                        if (!nameList.contains(node.asMap().get("name").toString())) {
                            nameList.add(node.asMap().get("name").toString());
                            oneNode.put("id", node.asMap().get("id").toString());
                            oneNode.put("name", node.asMap().get("name").toString());
                            nodes.add(oneNode);
                        }

                    }

                    /**
                     * 存储边
                     */
                    for (Relationship relationship : record.get("p").asPath().relationships()) {
                        // System.out.println(relationship.type());
                        String[] names = relationship.asMap().get("description").toString().split("<->");
                        Map<String, Object> oneEdge = new HashMap<>();
                        oneEdge.put("source", nameList.indexOf(names[0]));
                        oneEdge.put("target", nameList.indexOf(names[1]));
                        oneEdge.put("relation", relationship.type());
                        edges.add(oneEdge);
                    }

                }
                result.setNodes(nodes);
                result.setEdges(edges);

            }
        }
        driver.close();

        return result;
    }

    public static RelationBean shortestPathByguozhaotong(String name1, String name2) {

        RelationBean result_final = new RelationBean();
        String result = "";

        HashSet<Relation> relationSet = new HashSet<Relation>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // ClassLoader classLoader = Cypher.class.getClassLoader();
            //// File file = new File(classLoader
            // .getResource("relation.json").getFile());
            // InputStream in =
            // classLoader.getResourceAsStream("relation.json");

            String json = FileUtils.readFileToString(new File("D://relation.json"));
            relationSet = mapper.readValue(json, new TypeReference<HashSet<Relation>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Boolean findPath = false;// 有没有找到name1和name2之间的路径
        /******************************************* 广度优先搜索开始 *********************************************/
        ArrayList<String> relationPre = new ArrayList<>();// 上一层的关系们
        ArrayList<String> personPre = new ArrayList<>();// 上一层的人名
        ArrayList<String> relationNow = new ArrayList<>();// 现在这一层的关系们
        ArrayList<String> personNow = new ArrayList<>();// 现在这一层的人名
        HashSet<String> findPerson = new HashSet<>();// 过的人
        findPerson.add(name1);
        relationPre.add(name1);
        personPre.add(name1);
        while (!findPath) {
            for (int i = 0; i < personPre.size(); i++) {
                for (Relation relation : relationSet) {
                    if (relation.getName1().equals(personPre.get(i))) {
                        if (findPerson.contains(relation.getName2())) {
                            continue;
                        }
                        findPerson.add(relation.getName2());
                        personNow.add(relation.getName2());
                        relationNow.add(relationPre.get(i) + "_" + relation.getName2());
                        if (relation.getName2().equals(name2)) {
                            findPath = true;
                            result = relationPre.get(i) + "_" + relation.getName2();
                            break;
                        }
                    } else if (relation.getName2().equals(personPre.get(i))) {
                        if (findPerson.contains(relation.getName1())) {
                            continue;
                        }
                        findPerson.add(relation.getName1());
                        personNow.add(relation.getName1());
                        relationNow.add(relationPre.get(i) + "_" + relation.getName1());
                        if (relation.getName1().equals(name2)) {
                            findPath = true;
                            result = relationPre.get(i) + "_" + relation.getName1();
                            break;
                        }
                    }
                }
                if (findPath) {
                    break;
                }
            }
            relationPre = relationNow;
            personPre = personNow;
            relationNow = new ArrayList<>();
            personNow = new ArrayList<>();
        }
        /******************************************* 广度优先搜索结束 *********************************************/
        String[] orderPerson = result.split("_");
        String[] orderID = orderPerson.clone();
        String[] orderRelation = orderPerson.clone();
        // 获取人名及其ID及其两个人的关系
        for (Relation relation : relationSet) {
            for (int i = 0; i < orderPerson.length - 1; i++) {
                if (relation.getName1().equals(orderPerson[i]) && relation.getName2().equals(orderPerson[i + 1])) {
                    orderID[i] = relation.getId1();
                    orderID[i + 1] = relation.getId2();
                    orderRelation[i] = relation.getRelationship();
                }
                if (relation.getName2().equals(orderPerson[i]) && relation.getName1().equals(orderPerson[i + 1])) {
                    orderID[i] = relation.getId2();
                    orderID[i + 1] = relation.getId1();
                    orderRelation[i] = relation.getRelationship();
                }
            }
        }
        // 存储点
        LinkedList<Map<String, Object>> nodes = new LinkedList<>();
        for (int i = 0; i < orderPerson.length; i++) {
            Map<String, Object> oneNode = new HashMap<>();
            oneNode.put("id", orderID[i]);
            oneNode.put("name", orderPerson[i]);
            nodes.add(oneNode);
        }
        result_final.setNodes(nodes);
        // 存储边
        LinkedList<Map<String, Object>> edges = new LinkedList<>();
        for (int i = 0; i < orderRelation.length - 1; i++) {
            Map<String, Object> oneEdge = new HashMap<>();
            oneEdge.put("source", i);
            oneEdge.put("target", i + 1);
            oneEdge.put("relation", orderRelation[i]);
            edges.add(oneEdge);
        }
        // System.out.println(edges.size());
        result_final.setEdges(edges);
        System.out.println(result_final.getNodes());
        System.out.println(result_final.getEdges());
        // result_final=null;
        return result_final;
    }

    public static ArrayList<Relation> getRelation(String name, HashSet<Relation> relationSet) {
        ArrayList<Relation> result = new ArrayList<>();
        for (Relation relation : relationSet) {
            if (name.equals(relation.getName2()) || name.equals(relation.getName1())) {
                result.add(relation);
            }
        }
        return result;

    }

}
