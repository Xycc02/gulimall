package com.xuyuchao.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.xuyuchao.gulimall.search.config.GulimallESConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    RestHighLevelClient client;

    @Test
    void initClient() {
        System.out.println(client);
    }

    /**
     * 测试存储数据到es中
     */
    @Test
    void testIndexData() throws IOException {
        //1.创建request请求
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        User user = new User();
        user.setUsername("徐宇超");
        user.setAge(20);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        //2.添加dsl处理操作
        request.source(jsonString,XContentType.JSON);
        //3.发送请求
        IndexResponse response = client.index(request, GulimallESConfig.COMMON_OPTIONS);
        System.out.println(response);
    }

    @Data
    class User{
        private String username;
        private int age;
        private String gender;
    }
}
