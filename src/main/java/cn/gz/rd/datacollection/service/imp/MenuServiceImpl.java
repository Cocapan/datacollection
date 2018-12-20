package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.dao.MenuMapper;
import cn.gz.rd.datacollection.model.Menu;
import cn.gz.rd.datacollection.service.MenuService;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author panxuan
 * 菜单服务实现类
 */
@Service
public class MenuServiceImpl implements MenuService {

    private MenuMapper menuMapper;

    @Autowired
    public MenuServiceImpl(MenuMapper menuMapper){
        this.menuMapper = menuMapper;
    }


    @Override
    public Map<String, Object> getMenuList() {
        List<Menu> menus = menuMapper.queryAll();
        return ResultMapUtils.getResultMap(menus);
    }
}
