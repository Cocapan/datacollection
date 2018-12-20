package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.JkwwPrjImgInfoMapper;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传服务
 * @author Peng Xiaodong
 */
@Service
public class SFTPService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    private String privateKey;

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    /**
     * 上传文件的保存目录前缀
     */
    @Value("${uploadfile.new.save.root.dir}")
    private String uploadNewFileSaveDir;

    /**
     * 上传文件的历史保存目录前缀
     */
    @Value("${uploadfile.history.save.root.dir}")
    private String uploadHisFileSaveDir;

    @Autowired
    private JkwwPrjImgInfoMapper prjImgInfoMapper;

    public SFTPClient createClientAndLgoin() {
        SFTPClient sftpClient = new SFTPClient(username, password, host, port);
        sftpClient.login();
        return sftpClient;
    }

    public void close(SFTPClient sftpClient) {
        if (sftpClient != null) {
            sftpClient.logout();
        }
    }

    /**
     * 上传文件到SFTP，存在最新目录中。
     * @param saveDir 文件存储目录
     * @param saveFileName 存储的文件名称
     * @param inputStream 上传的文件流
     * @throws SftpException
     * @return 上传到最新目录中的文件路径
     */
    public String uploadNewFile(
            SFTPClient sftpClient, String saveDir,
            String saveFileName, InputStream inputStream) {
        String ftpNewSaveDir;
        if (StringUtils.isNotEmpty(saveDir)) {
            ftpNewSaveDir = uploadNewFileSaveDir + saveDir + File.separatorChar;
        } else {
            ftpNewSaveDir = uploadNewFileSaveDir + File.separatorChar;
        }
        logger.info("上传文件到SFTP文件：" + ftpNewSaveDir + saveFileName);
        sftpClient.upload(ftpNewSaveDir, saveFileName, inputStream);
        return ftpNewSaveDir + saveFileName;
    }

    public String uploadNewFileByJkww(
            SFTPClient sftpClient, String saveDir,
            String fileName, InputStream inputStream,
            String subjectName, String statPeriod, String subjectCode) {
        String saveFileNamePrefix = subjectName + "_" + statPeriod;
        String saveFileNameSuffix = FilenameUtils.getExtension(fileName);
        String saveFileName = saveFileNamePrefix + "." + saveFileNameSuffix;
        String saveDirPathBySFTP = uploadNewFileSaveDir + saveDir;

        //先删除原有文件，再上传
        if (!StringUtils.contains(subjectName, "项目进展照片")) {
            sftpClient.deleteByContains(saveDirPathBySFTP, saveFileNamePrefix);

        } else {//图片类型的文件需要特殊处理
            int imgProjectID = prjImgInfoMapper.selectImgIdBySubjectCode(subjectCode);
            saveFileName = saveFileNamePrefix + "_" + imgProjectID + "_" + System.currentTimeMillis() + "." + saveFileNameSuffix;
        }
        sftpClient.upload(saveDirPathBySFTP, saveFileName, inputStream);
        String saveFilePath = saveDirPathBySFTP + File.separatorChar + saveFileName;
        return saveFilePath;
    }

    /**
     * 上传文件到SFTP，存在历史目录中。
     * @param saveDir 文件存储目录
     * @param saveFileName 存储的文件名称
     * @param file 上传的文件
     * @throws IOException
     * @throws SftpException
     * @return 上传到历史目录中的文件路径
     */
    public String uploadHisFile(
            SFTPClient sftpClient, String saveDir,
            String saveFileName, MultipartFile file)
            throws IOException {
        return uploadHisFile(sftpClient, saveDir, saveFileName, file.getInputStream());
    }

    /**
     * 上传文件
     * @param sftpClient sftp客户端
     * @param saveDir 文件保存目录，必须有/或者\开头，结尾必须有分隔符。
     * @param saveFileName 文件保存名称
     * @param inputStream 文件流
     * @return 上传的文件存储路径
     * @throws SftpException
     */
    public String uploadHisFile(
            SFTPClient sftpClient, String saveDir,
            String saveFileName, InputStream inputStream) {
        String ftpHisSaveDir;

        if (StringUtils.isBlank(saveDir)) {
            throw new RuntimeException("文件保存目录不能为空！");
        }

        if (StringUtils.isBlank(saveFileName)) {
            throw new RuntimeException("文件名称不能为空！");
        }

        //必须已分隔符开头
        if (!StringUtils.startsWith(saveDir, String.valueOf(File.separatorChar))) {
            throw new RuntimeException("文件存放路径必须已/或\\开头！");
        }

        //必须已分隔符结尾
        if (!StringUtils.endsWith(saveDir, String.valueOf(File.separatorChar))) {
            throw new RuntimeException("文件存放路径必须已/或\\结尾！");
        }

        if (StringUtils.isNotEmpty(saveDir)) {
            ftpHisSaveDir = uploadHisFileSaveDir + saveDir;
        } else {
            ftpHisSaveDir = uploadHisFileSaveDir + File.separatorChar;
        }
        logger.info("上传文件到SFTP文件：" + ftpHisSaveDir + saveFileName);
        sftpClient.upload(ftpHisSaveDir, saveFileName, inputStream);
        return ftpHisSaveDir + saveFileName;
    }

    public void deleteNewFile(SFTPClient sftpClient, String saveDir, String saveFileName) {
        String ftpNewSaveDir = null;
        if (StringUtils.isNotEmpty(saveDir)) {
            ftpNewSaveDir = uploadNewFileSaveDir + saveDir;
        } else {
            ftpNewSaveDir = uploadNewFileSaveDir + File.separatorChar;
        }
        logger.info("删除SFTP文件：" + ftpNewSaveDir + saveFileName);
        sftpClient.delete(ftpNewSaveDir, saveFileName);
    }

}
