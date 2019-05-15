package com.custom.mutil;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class SftpUtil {

    private ChannelSftp sftp = null;
    private Session session = null;
    private String userName = null;
    private String password = null;
    private final String privateKey = null;// 私钥
    private String serverIp = null;
    private Integer serverPort = null;

    public SftpUtil(String userName, String password, String serverIp, Integer serverPort) throws Exception {
        this.userName = userName;
        this.password = password;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void login() throws Exception {
        JSch jsch = new JSch();
        if (privateKey != null) {
            jsch.addIdentity(privateKey);// 设置私钥
        }
        session = jsch.getSession(userName, serverIp, serverPort);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect(30000);
        Channel channel = session.openChannel("sftp");
        channel.connect(3000);
        sftp = (ChannelSftp) channel;
    }

    public void logout() throws Exception {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void uploadFile(File file, String remotePath) throws Exception {
        try {
            sftp.cd(remotePath);
        } catch (SftpException e) {
            sftp.mkdir(remotePath);
            sftp.cd(remotePath);
        }
        sftp.put(new FileInputStream(file), file.getName());
    }

    public boolean isConnected() {
        if (sftp != null) {
            return sftp.isConnected();
        }
        return false;
    }
}
