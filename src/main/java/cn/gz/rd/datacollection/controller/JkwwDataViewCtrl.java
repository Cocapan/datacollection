package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.service.JkwwwSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 科教文卫业务数据操作接口
 */
@Controller
@RequestMapping("/jkwww/view")
public class JkwwDataViewCtrl {

    private static final Logger logger = LoggerFactory.getLogger(JkwwDataViewCtrl.class);

    @Autowired
    private JkwwwSubjectService subjectService;

    /**
     * 各个主题数据查看
     * @param deptCode 部门编号
     * @param countRate 统计周期
     * @param subjectCode 主题编号
     * @return ControllerResponse
     */
    @ResponseBody
    @RequestMapping(value = "/data/v1", method = RequestMethod.GET)
    public ControllerResponse viewDataSubject(
            String deptCode, String countRate, String subjectCode) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> data =
                    subjectService.viewDataSubject(subjectCode, deptCode, countRate);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error("查询出现异常，请联系管理员！", e);
        }
        return response;
    }

}
