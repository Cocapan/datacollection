package cn.gz.rd.datacollection.model;

import java.util.Date;

/**
 * @author panxuan
 * 工委信息
 */
public class WorkingCommittee {

    /**
     * 工委编码
     */
    private String workingCommitteeId;

    /**
     * 工委名
     */
    private String workingCommitteeName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 修改时间
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

    public String getWorkingCommitteeId() {
        return workingCommitteeId;
    }

    public void setWorkingCommitteeId(String workingCommitteeId) {
        this.workingCommitteeId = workingCommitteeId;
    }

    public String getWorkingCommitteeName() {
        return workingCommitteeName;
    }

    public void setWorkingCommitteeName(String workingCommitteeName) {
        this.workingCommitteeName = workingCommitteeName;
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
