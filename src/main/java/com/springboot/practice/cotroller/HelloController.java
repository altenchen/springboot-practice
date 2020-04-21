package com.springboot.practice.cotroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @create: 2020/4/18
 * @author: altenchen
 */
@RestController
public class HelloController {


    @RequestMapping("/hello")
    public String hello() {
        return "Hello Spring Boot!";
    }



}
