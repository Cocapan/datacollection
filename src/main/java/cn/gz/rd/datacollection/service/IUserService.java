package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.ShowUser;
import cn.gz.rd.datacollection.model.SysUser;
import cn.gz.rd.datacollection.model.User;

import java.util.List;

public interface IUserService {

    int countAll() throws Exception;

    int insert(User record) throws Exception;

    User selectByAccount(String account) throws Exception;

    User selectByAccountAndPw(User user) throws Exception;

    int insert(SysUser user) throws CommonException;

    int update(SysUser user) throws CommonException;

    int delete(int userId);

    List<ShowUser> query(
            String userName, String nickName,
            Integer pageNum, Integer pageSize);

    int count(String userName, String nickName);

    boolean existSameUser(String userName);

    boolean existUserById(Integer id);

    void chgPasswd(String userName, String oldPasswd, String newPasswd);
}
