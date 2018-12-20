package cn.gz.rd.datacollection.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

import com.jcraft.jsch.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 * SFTP 客户端工具类
 *
 * @author Peng Xiaodong
 */
public class SFTPClient {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SFTPClient.class);

    private ChannelSftp sftp;

    private Session session;

    private String username;

    private String password;

    private String privateKey;

    private String host;

    private int port = 22;

    public SFTPClient(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public SFTPClient(String privateKey, String host, int port) {
        this.privateKey = privateKey;
        this.host = host;
        this.port = port;
    }

    /**
     * 连接sftp服务器
     */
    public void login() {
        try {
            JSch jsch = new JSch();
            if (StringUtils.isNotBlank(privateKey)) {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
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


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param directory    上传目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     */
    public void upload(String directory, String sftpFileName, InputStream input) {
        if (!exist(directory)) {
            mkdirs(directory);
        }
        try {
            sftp.cd(directory);
            sftp.put(input, sftpFileName);
        } catch (SftpException e) {
            String errMsg = "文件上传失败，上传目录：" + directory + ", 文件名称：" + sftpFileName;
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    public boolean exist(String dirPath) {
        SftpATTRS attrs = null;
        try {
            attrs = sftp.stat(dirPath);
        } catch (Exception e) {
            //do nothing
        }
        if (attrs == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile)
            throws SftpException, FileNotFoundException {
        if (StringUtils.isNotEmpty(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param fileName 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String fileName)
            throws SftpException, IOException {
        if (StringUtils.isNotEmpty(directory) && exist(directory)) {
            sftp.cd(directory);
        }
        byte[] fileData;
        if (exist(directory + fileName)) {
            InputStream is = sftp.get(fileName);
            fileData = IOUtils.toByteArray(is);
        } else {
            fileData = null;
        }
        return fileData;
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) {
        String filePath = directory + File.separatorChar + deleteFile;
        try {
            if (!exist(directory)) {
                return;//文件都不存在则不用删除
            }
            sftp.cd(directory);
            if (exist(filePath)) {
                sftp.rm(deleteFile);
            } else {
                logger.warn("要删除的文件不存在，文件路径=" + filePath);
            }
        } catch (SftpException e) {
            String errMsg = "文件删除失败，删除的文件路径：" + filePath;
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath  要删除文件路径
     */
    public void delete(String filePath) {
        String[] split = filePath.split(String.valueOf(File.separatorChar));
        String deleteFile = split[split.length - 1];
        String directory = filePath.substring(0,filePath.length()-deleteFile.length());
        try {
            if (!exist(directory)) {
                return;//文件都不存在则不用删除
            }
            sftp.cd(directory);
            if (exist(filePath)) {
                sftp.rm(deleteFile);
            } else {
                logger.warn("要删除的文件不存在，文件路径=" + filePath);
            }
        } catch (SftpException e) {
            String errMsg = "文件删除失败，删除的文件路径：" + filePath;
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * 在指定目录下删除包含containsStr字符的文件
     * @param directoryPath 文件夹
     * @param containsStr 包含的文件名
     */
    public void deleteByContains(String directoryPath, String containsStr) {
        try {
            if (!exist(directoryPath)) {
                return;//文件都不存在则不用删除
            }

            sftp.cd(directoryPath);
            Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(directoryPath);
            for (ChannelSftp.LsEntry lsEntry : lsEntries) {
                String filename = lsEntry.getFilename();
                if (StringUtils.equals(filename, ".") ||
                        StringUtils.equals(filename, "..")) {
                    continue;
                }
                if (StringUtils.contains(filename, containsStr)) {
                    sftp.rm(filename);
                    logger.info("根据文件名称删除文件：" + directoryPath + File.separatorChar + filename);
                }
            }
        } catch (SftpException e) {
            String errMsg = "根据文件名称删除文件失败，文件夹路径："
                    + directoryPath + ", 包含的字符：" + containsStr;
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     */
    public Vector<ChannelSftp.LsEntry> listFiles(String directory) {
        Vector<ChannelSftp.LsEntry> lsEntries = new Vector<>();
        try {
            lsEntries = sftp.ls(directory);
        } catch (SftpException e) {
            logger.error("SFTP列举文件失败，目录：" + directory, e);
            throw new RuntimeException("SFTP列举文件失败，目录：" + directory, e);

        }
        return lsEntries;
    }

    public void mkdirs(String dir) {
        logger.info("批量新建SFTP目录：" + dir);
        if (StringUtils.endsWith(dir, "/")) {
            dir = StringUtils.removeEnd(dir, "/");
        }
        Stack<String> stack = new Stack<>();
        String parentDir = dir;
        while(!exist(parentDir)) {
            stack.push(parentDir);
            parentDir = FilenameUtils.getFullPathNoEndSeparator(parentDir);
        }

        while (!stack.empty()) {
            String path = stack.pop();
            try {
                if (!exist(path)) {
                    logger.info("新建SFTP子目录：" + path);
                    sftp.mkdir(path);
                } else {
                    logger.debug("目录已经存在：" + path);
                }
            } catch (SftpException e) {
                logger.error("新建SFTP目录异常，目录=" + path, e);
                throw new RuntimeException("新建SFTP目录异常，目录=" + path);
            }
        }
    }

}