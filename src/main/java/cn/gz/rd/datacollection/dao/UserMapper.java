package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    int countAll();

    int insert(User record);

    /**
     * 通过账号, 获取用户信息
     * @param account 账号
     * @return 用户信息
     */
    User selectByAccount(String account);

    User selectByAccountAndPw(User user);

    int updatePasswd(
            @Param("userName") String userName,
            @Param("password") String password);

    String selectPhoneNumberByDepartmentId(String departmentId);

}