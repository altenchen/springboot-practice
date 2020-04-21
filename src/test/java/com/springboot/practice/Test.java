package com.springboot.practice;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @description:
 * @create: 2020/4/18
 * @author: altenchen
 */
public class Test {

    @org.junit.jupiter.api.Test
    void timeTest() {
        //20000101000000
        //22861121014640
        //10000000000000
        System.out.println(DateUtil.parse("19700427014640000", "yyyyMMddHHmmssSSS").getTime());
        System.out.println(DateUtil.format(new Date(10000000000L), "yyyyMMddHHmmss"));
    }
}
