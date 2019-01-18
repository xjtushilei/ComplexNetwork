package com.xjtu.graphdemo.entity;


import java.util.LinkedList;
import java.util.Map;

public class RelationBean {
    private LinkedList<Map<String, Object>> nodes;
    private LinkedList<Map<String, Object>> edges;

    public RelationBean(LinkedList<Map<String, Object>> nodes, LinkedList<Map<String, Object>> edges) {
        super();
        this.nodes = nodes;
        this.edges = edges;
    }


    public RelationBean() {
        super();
    }

    public LinkedList<Map<String, Object>> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedList<Map<String, Object>> nodes) {
        this.nodes = nodes;
    }

    public LinkedList<Map<String, Object>> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Map<String, Object>> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "RelationBean{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                '}';
    }
}