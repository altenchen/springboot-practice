package com.springboot.practice;

import com.springboot.practice.util.FtpClientTemplate;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @create: 2020/4/21
 * @author: altenchen
 */
@RunWith(SpringJUnit4ClassRunner.class)//用于在JUnit环境下提供Spring Test框架的功能。
@SpringBootTest
public class FtpTest {

    @Autowired
    private FtpClientTemplate ftpClientTemplate;

    @Test
    public void download() {
        ftpClientTemplate.downloadFile("/aaa", "gaofeng.tgz", "E:\\aaa");
    }

    @Test
    public void upload() {
        File file = new File("/Users/chenchen/Desktop/ftpfile/t.zip");
        ftpClientTemplate.uploadFile(file, "/ftp/test");
    }


}
