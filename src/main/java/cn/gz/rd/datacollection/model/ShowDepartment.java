package cn.gz.rd.datacollection.model;

import java.util.List;

/**
 * @author panxuan
 * 部门展示信息
 */
public class ShowDepartment extends Department {

    /**
     * 工委名
     */
    private String workingCommitteeName;

    /**
     * 菜单权限
     */
    private String menuAuthority;

    /**
     * 工委编码数组
     */
    private List<String> workingCommitteeIds;


    public String getWorkingCommitteeName() {
        return workingCommitteeName;
    }

    public void setWorkingCommitteeName(String workingCommitteeName) {
        this.workingCommitteeName = workingCommitteeName;
    }

    public String getMenuAuthority() {
        return menuAuthority;
    }

    public void setMenuAuthority(String menuAuthority) {
        this.menuAuthority = menuAuthority;
    }

    public List<String> getWorkingCommitteeIds() {
        return workingCommitteeIds;
    }

    public void setWorkingCommitteeIds(List<String> workingCommitteeIds) {
        this.workingCommitteeIds = workingCommitteeIds;
    }

}
