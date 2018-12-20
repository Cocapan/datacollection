package cn.gz.rd.datacollection.controller.request;

/**
 * @author panxuan
 * 部门管理模块接收的请求参数
 */
public class DepartmentRequest extends PageRequest{

    /**
     * 部门id
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
     * 工委编码数组
     */
    private String[] workingCommitteeIdArray;

    /**
     * 菜单编码数组
     */
    private int[] menuIdArray;

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

    public String[] getWorkingCommitteeIdArray() {
        return workingCommitteeIdArray;
    }

    public void setWorkingCommitteeIdArray(String[] workingCommitteeIdArray) {
        this.workingCommitteeIdArray = workingCommitteeIdArray;
    }

    public int[] getMenuIdArray() {
        return menuIdArray;
    }

    public void setMenuIdArray(int[] menuIdArray) {
        this.menuIdArray = menuIdArray;
    }
}
