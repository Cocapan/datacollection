package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 菜单数据访问
 */
@Mapper
@Component
public interface MenuMapper {

    /**
     *  获取假期列表
     * @return 假期列表
     */
    List<Menu> queryAll();

}
