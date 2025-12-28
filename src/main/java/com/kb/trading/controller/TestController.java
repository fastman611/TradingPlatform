package com.kb.trading.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController//告诉Spring这是一个提供REST API的控制器。
@RequestMapping("/test")//这个控制器下所有接口的URL都以 /api/test 开头（因为你在 application.yml 里设置了 context-path: /api）
public class TestController {
    @GetMapping("/hello")//这是一个GET请求接口，访问路径是 /api/test/hello。
    public String hello() {
        return "同城快吧交易平台后端启动成功！" ;
    }

    @GetMapping("/check")
    public String healthCheck() {
        return "{\"status\": \"UP\", \"service\": \"trading-platform-backend\"}";
    }
}

