package com.xjtushilei.complexnetwork.dataMining;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjtushilei.complexnetwork.config.config;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.*;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.neo4j.driver.v1.Values.parameters;


public class DataPretreatment {
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("DataPretreatment");

    public static void main(String[] args) throws IOException {
//        deleteAll();
        inset();
    }

    public static void init() throws IOException {
        deleteAll();
        inset();
    }

    private static HashSet<Person> getPerson() throws IOException {
        HashSet<Person> persons = new HashSet<>();
        ArrayList<Relation> relationList = deDuplication();
        for (Relation relation : relationList) {
            persons.add(new Person(relation.getName1(), relation.getId1()));
            persons.add(new Person(relation.getName2(), relation.getId2()));
        }
        logger.info("人总数： " + persons.size());
        return persons;

    }

    private static ArrayList<Relation> deDuplication() throws IOException {
        File relationFile = ResourceUtils.getFile("classpath:data/relation.json");
        String json = FileUtils.readFileToString(relationFile, "utf-8");
        ArrayList<Relation> relationList;
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType1 = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Relation.class);
        relationList = objectMapper.readValue(json, javaType1);
        logger.info("关系总数： " + relationList.size());
        return relationList;
    }

    private static void deleteAll() {
        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));

        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run("MATCH (n) DETACH DELETE n");
                tx.success();
            }
        }
        driver.close();
        logger.info("全部删除！");
    }

    private static void inset() throws IOException {
        Driver driver = GraphDatabase.driver(config.NEO4J_IP, AuthTokens.basic(config.NEO4J_USER, config.NEO4J_PASSWD));



        /*
         * 插入人
         */
        HashSet<Person> persons = getPerson();

//        persons.parallelStream().forEach(person -> {
//            try (Session session = driver.session()) {
//                try (Transaction tx = session.beginTransaction()) {
//                    tx.run("CREATE (a:Person {name: {name}, id: {id}})",
//                            parameters("name", person.getName(),
//                                    "id", person.getId()
//                            ));
//                    tx.success();
//                }
//            }
//        });
        logger.info("插入节点结束！");
        /*
         * 插入关系
         */
        ArrayList<Relation> relationSet = deDuplication();

        relationSet.parallelStream().forEach(relation ->
        {
            try (Session session = driver.session()) {
                try (Transaction tx = session.beginTransaction()) {
                    String relationShip = relation.getRelationship().replaceAll("[\\pP\\p{Punct}]", "");
                    relationShip = relationShip.replace(" ", "");
                    if (relationShip.charAt(0) >= '0' && relationShip.charAt(0) <= 'z') {
                        relationShip = "关系是" + relationShip;
                    }
                    tx.run("MATCH (a:Person),(b:Person) "
                                    + "WHERE a.id = {id1} AND b.id ={id2}"
                                    + " CREATE (a)-[r:" + relation.getRelationship().replaceAll("[\\pP\\p{Punct}]", "") + " { description: a.name + '<->' + b.name }]->(b)",
                            parameters("id1", relation.getId1(),
                                    "id2", relation.getId2()
                            ));
                    tx.success();
                }
            }
        });
        driver.close();
        logger.info("插入关系结束！");

    }

}
