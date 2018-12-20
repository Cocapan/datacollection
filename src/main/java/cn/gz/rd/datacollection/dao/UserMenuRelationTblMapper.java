package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.UserMenuRelationTbl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 用户菜单管理表数据访问层
 */
@Mapper
@Component
public interface UserMenuRelationTblMapper {

    /**
     * 插入部门菜单关联信息
     * @param userMenuRelationTbl 用户菜单关联信息
     * @return 1 插入成功, 0 插入失败
     */
    int insert(UserMenuRelationTbl userMenuRelationTbl);

    /**
     * 根据菜单编码, 删除部门菜单关联信息
     * @param menuId 菜单编码
     * @return 1 删除成功, 0 删除失败
     */
    int deleteByMenuIdAndUserId(@Param(value = "menuId") int menuId, @Param(value = "userId")int userId);

    /**
     * 根据用户编码, 获取菜单权限信息列表
     * @param userId 用户编码
     * @return 菜单权限信息列表
     */
    List<String> selectByUserId(int userId);

    List<Integer> selectMenuIdsByUserId(int userId);

}
