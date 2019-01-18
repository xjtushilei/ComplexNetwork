package com.xjtu.graphdemo.controller;

import com.xjtu.graphdemo.entity.RelationBean;
import com.xjtu.graphdemo.service.Search;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class API {

    @GetMapping("search")
    public RelationBean test(String name1, String name2) {
        return Search.shortestPathByGuoZhaoTong(name1, name2);
    }
}
