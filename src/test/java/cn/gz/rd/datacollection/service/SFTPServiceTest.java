package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Peng Xiaodong
 */
public class SFTPServiceTest extends DataCenterApplicationTests {

    private SFTPClient sftpClient;

    @Autowired
    private SFTPService sftpService;

    @Before
    public void before() {
        sftpClient = sftpService.createClientAndLgoin();
    }

    @After
    public void after() {
        sftpClient.logout();
    }

    @Test
    public void testListFiles() throws SftpException {
        Vector<?> listFiles = sftpClient.listFiles("/root/app/out.*");
        for (int i = 0, size = listFiles.size(); i < size; i++) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) listFiles.get(i);
            System.out.println(entry.getFilename());
        }
    }

    @Test
    public void testDownloadFile() throws FileNotFoundException, SftpException {
        sftpClient.download("/root/app",
                "autoStartDataCollection.sh",
                "C:\\Users\\Shadon\\Desktop\\autoStartDataCollection.sh");
    }

    @Test
    public void testUploadFile() throws IOException {
        File uploadFile = new File("C:\\Users\\Shadon\\Desktop\\autoStartDataCollection.sh");
        sftpClient.upload("/root/app/pxd/", "haha.sh", FileUtils.openInputStream(uploadFile));
    }

    @Test
    public void testMkdirs() {
        String testRootDir = "/test-dir-pxd";
        String createDir = testRootDir + "/file/城乡建委/市城市更新局/广州市城/市更新总体规划/";
        sftpClient.mkdirs(createDir);
        boolean exist = sftpClient.exist(createDir);
        Assert.assertTrue("创建多级目录失败：" + createDir, exist);
    }

    @Test
    public void testCd() {
        sftpClient.deleteByContains("/root/pxd-test", "彭小冬");
    }

    @Test
    public void deleteByPattern() {
        sftpClient.deleteByContains("/root/pxd-test", "txt");
    }
}
