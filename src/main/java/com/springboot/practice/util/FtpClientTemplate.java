package com.springboot.practice.util;

import com.springboot.practice.pool.FtpClientPool;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @create: 2020/4/21
 * @author: altenchen
 */
@Component
public class FtpClientTemplate {

    private final static Logger log = Logger.getLogger(FtpClientTemplate.class.getName());

    @Autowired
    private FtpClientPool ftpClientPool;

    /***
     * 上传Ftp文件
     *
     * @param localFile 本地文件路径
     * @param remotePath 上传服务器路径 - (/abc/1.txt)
     * @return true or false
     */
    public boolean uploadFile(File localFile, String remotePath) {
        FTPClient ftpClient = null;
        BufferedInputStream inStream = null;
        try {
            //从池中获取对象
            ftpClient = ftpClientPool.borrowObject();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("FTP服务器校验失败, 上传replyCode:{}" + replyCode+"   "+localFile);
                return false;
            }

            //切换到上传目录
//            if (!ftpClient.changeWorkingDirectory(remotePath)) {
//                //如果目录不存在创建目录
//                String[] dirs = remotePath.split("/");
//                String tempPath = "";
//                for (String dir : dirs) {
//                    if (null == dir || "".equals(dir)) {
//                        continue;
//                    }
//                    tempPath += "/" + dir;
//                    if (!ftpClient.changeWorkingDirectory(tempPath)) {
//                        if (!ftpClient.makeDirectory(tempPath)) {
//                            return false;
//                        } else {
//                            ftpClient.changeWorkingDirectory(tempPath);
//                        }
//                    }
//                }
//            }

            //循环遍历创建路径
            //分割路径
            List<String> list = getPathList(remotePath);
            for(int i=0; i<list.size(); i++){
                if(!ftpClient.changeWorkingDirectory(list.get(i))){//若路径未存在则创建路径
                    if(!ftpClient.makeDirectory(list.get(i))){//若路径创建失败则不再继续处理
                        System.out.println("create dir fail --> " + list.get(i));
                        close(ftpClient);
                        return false;
                    }
                }
            }
            System.out.println(ftpClient.isConnected());//查看ftp是否已连接
            System.out.println(ftpClient.changeWorkingDirectory(remotePath));//查看目录是否已存在


            inStream = new BufferedInputStream(new FileInputStream(localFile));
            //设置上传文件的类型为二进制类型
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //尝试上传三次
            for (int j = 0; j < 3; j++) {
                //避免进度回调过于频繁
                final int[] temp = {0};
                //上传进度监控
                ftpClient.setCopyStreamListener(new CopyStreamAdapter() {
                    @Override
                    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                        int percent = (int) (totalBytesTransferred * 100 / localFile.length());
                        if (temp[0] < percent) {
                            temp[0] = percent;
                            log.info("↑↑   上传进度    " + percent + "     " + localFile.getAbsolutePath());
                        }
                    }
                });

                boolean success = ftpClient.storeFile(localFile.getName(), inStream);
                if (success) {
                    log.info("文件上传成功! " + localFile.getName());
                    return true;
                }
                log.info("文件上传失败" + localFile.getName() + "  重试 " + j);
            }
            log.info("文件上传多次仍失败" + localFile.getName());
        } catch (Exception e) {
            log.error("文件上传错误! " + localFile.getName(), e);
        } finally {
            IOUtils.closeQuietly(inStream);
            //将对象放回池中
            ftpClientPool.returnObject(ftpClient);
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param remotePath FTP服务器文件目录
     * @param fileName   需要下载的文件名称
     * @param localPath  下载后的文件路径
     * @return true or false
     */
    public boolean downloadFile(String remotePath, String fileName, String localPath) {
        FTPClient ftpClient = null;
        OutputStream outputStream = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("FTP服务器校验失败, 下载replyCode:{}" + replyCode + "  " + localPath + "/" + fileName);
                return false;
            }

            // 切换FTP目录
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (fileName.equalsIgnoreCase(file.getName())) {
                    //保存至本地路径
                    File localFile = new File(localPath + "/" + file.getName());
                    //创建父级目录
                    if (!localFile.getParentFile().exists()) {
                        localFile.getParentFile().mkdirs();
                    }

                    //尝试下载三次
                    for (int i = 0; i < 3; i++) {
                        //避免进度回调过于频繁
                        final int[] temp = {0};
                        //下载进度监控
                        ftpClient.setCopyStreamListener(new CopyStreamAdapter() {
                            @Override
                            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                                int percent = (int) (totalBytesTransferred * 100 / file.getSize());
                                if (temp[0] < percent) {
                                    temp[0] = percent;
                                    log.info("  ↓↓ 下载进度    " + percent + "     " + localFile.getAbsolutePath());
                                }
                            }
                        });

                        outputStream = new FileOutputStream(localFile);
                        boolean success = ftpClient.retrieveFile(file.getName(), outputStream);
                        outputStream.flush();
                        if (success) {
                            log.info("文件下载成功! " + localFile.getName());
                            return true;
                        }
                        log.info("文件下载失败" + localFile.getName() + "  重试 " + i);
                    }
                    log.info("文件下载多次仍失败" + localFile.getName());
                }
            }
        } catch (Exception e) {
            log.error("文件下载错误! " + remotePath + "/" + fileName, e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            ftpClientPool.returnObject(ftpClient);
        }
        return false;
    }

    //分割路径
    public static List<String> getPathList(String path){
        String[] dirs = path.split("/");
        List<String> list = new ArrayList<>();
        String pathname = "";
        for(String str : dirs){
            if(StringUtils.isEmpty(str)){
                continue;
            }
            pathname = pathname + "/" + str;
            list.add(pathname);
        }
        return list;
    }

    //关闭ftp
    public static void close(FTPClient ftp){
        try {
            if(ftp.isConnected()){
                ftp.logout();
                ftp.disconnect();
            }
            ftp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
