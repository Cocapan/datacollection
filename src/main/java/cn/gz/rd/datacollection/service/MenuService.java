package cn.gz.rd.datacollection.service;

import java.util.Map;

/**
 * @author panxuan
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 获取菜单列表
     * @return 菜单列表
     */
    Map<String, Object> getMenuList();

}
