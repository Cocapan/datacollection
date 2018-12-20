package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.ShowUser;
import cn.gz.rd.datacollection.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SysUserMapper {

    int deleteByPrimaryKey(Integer userId);

    int insert(SysUser user);

    int insertSelective(SysUser user);

    SysUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(SysUser user);

    int updateByPrimaryKey(SysUser user);

    /**
     * 如果密码为空，则不修改密码；其他字段为空也会进行修改。
     */
    int updateByUserIdNotPasswd(SysUser user);

    List<ShowUser> selectByPage(
            @Param("userName") String userName,
            @Param("nickName") String nickName,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize);

    int count(@Param("userName") String userName, @Param("nickName") String nickName);

    int countSanmeUserName(String userName);

    List<String> selectMenuAuthorityByUserId(int userId);

    List<Integer> getMenuIdsByUserId(int userId);
}