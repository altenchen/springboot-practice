package com.springboot.practice;

import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootParcticeApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void timeTest() {
		System.out.println(DateUtil.parse("20000101000000", "yyyyMMddHHmmss").getTime());
	}

}
