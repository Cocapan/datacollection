package cn.gz.rd.datacollection.model;

import java.util.Date;

/**
 * @author panxuan
 * 部门实体层
 */
public class Department {

    /**
     * 部门编码
     */
    private String departmentId;

    /**
     * 部门名
     */
    private String departmentName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 人大组织部门编码
     */
    private String npcOrganizationDepartmentId;

    /**
     * 修改日期
     */
    private Date modifyDate;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 修改用户
     */
    private String modifyUser;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getNpcOrganizationDepartmentId() {
        return npcOrganizationDepartmentId;
    }

    public void setNpcOrganizationDepartmentId(String npcOrganizationDepartmentId) {
        this.npcOrganizationDepartmentId = npcOrganizationDepartmentId;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }
}
