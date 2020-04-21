package com.springboot.practice.pool;

import com.springboot.practice.factory.FtpClientFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @description:
 * @create: 2020/4/21
 * @author: altenchen
 */
@Component
public class FtpClientPool {

    @Value("${ftpPool.maxTotal}")
    private int maxTotal;
    @Value("${ftpPool.minIdle}")
    private int minIdle;
    @Value("${ftpPool.maxIdle}")
    private int maxIdle;
    @Value("${ftpPool.maxWait}")
    private long maxWait;
    @Value("${ftpPool.blockWhenExhausted}")
    private boolean blockWhenExhausted;
    @Value("${ftpPool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${ftpPool.testOnReturn}")
    private boolean testOnReturn;
    @Value("${ftpPool.testOnCreate}")
    private boolean testOnCreate;
    @Value("${ftpPool.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${ftpPool.lifo}")
    private boolean lifo;

    //连接池
    private GenericObjectPool<FTPClient> ftpClientPool;

    @Autowired
    private FtpClientFactory ftpClientFactory;

    /**
     * 初始化连接池
     */
    @PostConstruct //加上该注解表明该方法会在bean初始化后调用
    public void init() {
        // 初始化对象池配置
        GenericObjectPoolConfig<FTPClient> poolConfig = new GenericObjectPoolConfig<FTPClient>();
        poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setTestOnCreate(testOnCreate);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setLifo(lifo);

        // 初始化对象池
        ftpClientPool = new GenericObjectPool<FTPClient>(ftpClientFactory, poolConfig);
    }

    public FTPClient borrowObject() throws Exception {
        return ftpClientPool.borrowObject();
    }

    public void returnObject(FTPClient ftpClient) {
        ftpClientPool.returnObject(ftpClient);
    }
}
