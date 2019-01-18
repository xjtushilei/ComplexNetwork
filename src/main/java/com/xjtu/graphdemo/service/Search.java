package com.xjtu.graphdemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjtu.graphdemo.entity.Relation;
import com.xjtu.graphdemo.entity.RelationBean;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Search {

    private static HashSet<Relation> relationSet = null;

    public static void main(String[] args) {
        shortestPathByGuoZhaoTong("周星驰", "周杰伦");
    }

    public static RelationBean shortestPathByGuoZhaoTong(String name1, String name2) {
        if (relationSet == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ClassPathResource resource = new ClassPathResource("classpath:data/relation.json");
                InputStream inputStream = resource.getInputStream();
                String json = IOUtils.toString(inputStream, "utf-8");
                relationSet = mapper.readValue(json, new TypeReference<HashSet<Relation>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("find: " + name1 + " " + name2);
        RelationBean result_final = new RelationBean();
        String result = "";

        boolean findPath = false;// 有没有找到name1和name2之间的路径
        /*
         * ****************************************** 广度优先搜索开始 ********************************************
         * */
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
        /*
         * ****************************************** 广度优先搜索结束 ********************************************
         * */
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
            oneEdge.put("Relation", orderRelation[i]);
            edges.add(oneEdge);
        }
        System.out.println("边数: " + edges.size());
        result_final.setEdges(edges);
        System.out.println("节点: " + result_final.getNodes());
        System.out.println("边: " + result_final.getEdges());

        return result_final;
    }

}
