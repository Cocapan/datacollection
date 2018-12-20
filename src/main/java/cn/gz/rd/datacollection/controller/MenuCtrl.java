package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author panxuan
 * 菜单控制层
 */
@Controller
@RequestMapping("/api/menu")
public class MenuCtrl {

    private static Logger logger = LoggerFactory.getLogger(MenuCtrl.class);

    /**
     * 菜单服务接口
     */
    private MenuService menuService;

    @Autowired
    public MenuCtrl(MenuService menuService){
        this.menuService = menuService;
    }


    /**
     * 获取全部菜单列表
     * @return 响应结果
     */
    @RequestMapping("/list/v1")
    @ResponseBody
    public ControllerResponse getMenuList() {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = menuService.getMenuList();
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

}
