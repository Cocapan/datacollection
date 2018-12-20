package cn.gz.rd.datacollection.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

/**
 * 所有Ctrl的基类，用于编写一些通用方法。
 * @author Peng Xiaodong
 */
public class BaseCtrl {

    @Autowired
    private HttpSession httpSession;

    public String getDeptCodeFromSession() {
        String deptCode = (String) httpSession.getAttribute("deptCode");
        return deptCode;
    }

    public String getUserCodeFromSession() {
        String userCode = (String) httpSession.getAttribute("userCode");
        return userCode;
    }

    public String getWorkingCommitteeCodeFromSession() {
        String workingCommitteeCode = (String) httpSession.getAttribute("workingCommitteeCode");
        return workingCommitteeCode;
    }

    public Boolean getIsNDRCFromSession() {
        Boolean isNDRC = (Boolean) httpSession.getAttribute("isNDRC");
        return isNDRC;
    }

}
