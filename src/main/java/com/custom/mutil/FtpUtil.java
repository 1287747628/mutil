package com.custom.mutil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class FtpUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    //
    public final FTPClient ftp;
    public boolean isConnected = false;

    /**
     * FTPClient构造函数,主要进行初始化配置连接FTP服务器。
     */
    public FtpUtil(String host, int port, String userName, String passWord) {
        ftp = new FTPClient();
        try {
            ftp.connect(host, port);
            ftp.login(userName, passWord);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                logger.info("ftp[{}] connect fail", host);
                ftp.disconnect();
            } else {
                logger.info("ftp[{}] connect success", host);
                isConnected = true;
            }
        } catch (SocketException e) {
            isConnected = false;
            logger.error("", e);
        } catch (IOException e) {
            isConnected = false;
            logger.error("", e);
        } catch (Exception e) {
            isConnected = false;
            logger.error("", e);
        }
    }

    /**
     * 上传文件到FTP服务器
     */
    public void upload(String local, String remote) throws IOException {
        File f = new File(local);
        uploadFile(f, remote);
    }

    /**
     * 上传文件到FTP服务器
     */
    public void uploadFile(File localFile, String remoteFile) throws IOException {
        InputStream in = new FileInputStream(localFile);
        ftp.enterLocalPassiveMode();// 设置PassiveMode传输
        ftp.setFileType(FTP.BINARY_FILE_TYPE); // 设置以二进制流的方式传输
        ftp.setBufferSize(1024);
        ftp.setControlEncoding("utf8");
        createDir(remoteFile);
        boolean success = ftp.storeFile(localFile.getName(), in);
        in.close();
        if (success) {
            logger.info("ftp upload file[{}] success", remoteFile + "/" + localFile.getName());
        } else {
            throw new IOException(String.format("ftp upload file[%1$s] fail ,replyCode is[%2$s]", remoteFile + "/" + localFile.getName(),
                    ftp.getReplyCode()));
        }
    }

    /**
     * 递归创建目录
     */
    public void createDir(String remote) throws IOException {
        if (!ftp.changeWorkingDirectory(remote)) {
            if (!ftp.makeDirectory(remote)) {
                if (!"/".equals(remote)) {
                    createDir(remote.substring(0, remote.lastIndexOf("/")));
                }
            }
            if (!ftp.changeWorkingDirectory(remote)) {
                if (ftp.makeDirectory(remote)) {
                    if (!ftp.changeWorkingDirectory(remote)) {
                        throw new IOException("create directory fail");
                    }
                }
            }
        }
    }

    public void close() {
        try {
            ftp.logout();
            ftp.disconnect();
            isConnected = false;
        } catch (IOException e) {
        }
    }
}
