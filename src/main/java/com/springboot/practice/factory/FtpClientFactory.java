package com.springboot.practice.factory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description:
 * @create: 2020/4/21
 * @author: altenchen
 */
@Component
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.clientTimeout}")
    private int clientTimeout;
    @Value("${ftp.connectTimeout}")
    private int connectTimeout;
    @Value("${ftp.encoding}")
    private String encoding;
    @Value("${ftp.bufferSize}")
    private int bufferSize;
    @Value("${ftp.passiveMode}")
    private boolean passiveMode;

    private final static Logger log = Logger.getLogger(FtpClientFactory.class.getName());

    /**
     * 创建FtpClient对象
     */
    @Override
    public FTPClient create() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(connectTimeout);
        try {
            ftpClient.connect(host, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                log.warn("FTPServer 连接失败,replyCode: " + replyCode);
                return null;
            }

            if (!ftpClient.login(username, password)) {
                log.warn("ftpClient 登录失败： " + username + password);
                return null;
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//文件类型
            ftpClient.setControlEncoding(encoding);
            ftpClient.setBufferSize(bufferSize);
            if (passiveMode) {
                //这个方法的意思就是每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.setSoTimeout(clientTimeout);
        } catch (IOException e) {
            log.error("FtpClient 创建错误： " + e.toString());
        }
        return ftpClient;
    }

    /**
     * 用PooledObject封装对象放入池中
     */
    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }

        FTPClient ftpClient = ftpPooled.getObject();
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (Exception io) {
            log.error("销毁FtpClient错误..." + io.toString());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                log.error("销毁FtpClient错误..." + io.toString());
            }
        }
    }

    /**
     * 验证FtpClient对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        try {
            FTPClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            log.error("验证FtpClient对象错误: " + e.toString());
        }
        return false;
    }
}
