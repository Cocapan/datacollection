package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.JkwwPrjImgInfoMapper;
import cn.gz.rd.datacollection.model.JkwwPrjImgInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JkwwPrjImgInfoService {

    @Autowired
    private JkwwPrjImgInfoMapper prjImgInfoMapper;

    public JkwwPrjImgInfo selectByPrimaryKey(Integer infoId) {
        return prjImgInfoMapper.selectByPrimaryKey(infoId);
    }

}
