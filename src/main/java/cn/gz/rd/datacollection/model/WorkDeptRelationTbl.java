package cn.gz.rd.datacollection.model;

import java.util.Date;

/**
 * @author panxuan
 * 工委信息关联信息
 */
public class WorkDeptRelationTbl {

    /**
     * 序号
     */
    private int id;

    /**
     * 工委编码
     */
    private String workingCommitteeId;

    /**
     * 部门编码
     */
    private String departmentId;

    /**
     * 创建日期
     */
    private Date createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkingCommitteeId() {
        return workingCommitteeId;
    }

    public void setWorkingCommitteeId(String workingCommitteeId) {
        this.workingCommitteeId = workingCommitteeId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
