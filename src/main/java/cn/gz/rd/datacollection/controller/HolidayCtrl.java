package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.request.HolidayRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.Holiday;
import cn.gz.rd.datacollection.service.HolidayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * @author panxuan
 * 假期控制层
 */
@Controller
@RequestMapping("/api/holiday")
public class HolidayCtrl {

    private static Logger logger = LoggerFactory.getLogger(HolidayCtrl.class);

    /**
     * 假期服务接口
     */
    private HolidayService holidayService;

    @Autowired
    public HolidayCtrl(HolidayService holidayService){
        this.holidayService = holidayService;
    }

    /**
     * 增加假期
     * @param holiday 假期
     * @return 响应结果
     */
    @RequestMapping("/insert/v1")
    @ResponseBody
    public ControllerResponse addHoliday(HttpSession session, @RequestBody Holiday holiday) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = holidayService.addHoliday(session, holiday);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 修改假期
     * @param holiday 假期
     * @return 响应结果
     */
    @RequestMapping("/update/v1")
    @ResponseBody
    public ControllerResponse alterHoliday(HttpSession session, @RequestBody Holiday holiday) {
        ControllerResponse response = new ControllerResponse();
        try {
            holidayService.alterHoliday(session, holiday);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 删除假期
     * @param id 假期唯一标识
     * @return 响应结果
     */
    @RequestMapping("/delete/v1")
    @ResponseBody
    public ControllerResponse deleteHoliday(@RequestParam(value = "id") int id) {
        ControllerResponse response = new ControllerResponse();
        try {
            holidayService.deleteHoliday(id);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取全部假期列表
     * @param request 分页请求参数
     * @return 响应结果
     */
    @RequestMapping("/list/v1")
    @ResponseBody
    public ControllerResponse getHolidayList(@RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = holidayService.getHolidayList(request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 条件查询, 获取假期列表
     * @param request 分页及条件请求参数
     * @return 响应结果
     */
    @RequestMapping("/conditionQuery/v1")
    @ResponseBody
    public ControllerResponse getHolidayByCondition(@RequestBody HolidayRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = holidayService.getHolidayByCondition(request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取年份列表
     * @return 年份列表
     */
    @RequestMapping("/year/list/v1")
    @ResponseBody
    public ControllerResponse getYears() {
        ControllerResponse response = new ControllerResponse();
        try {
            response.setData(holidayService.getYears());
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

}
