package cn.gz.rd.datacollection.model;

import java.util.List;

/**
 * @author panxuan
 * 用户信息
 */
public class User {

    /**
     * 序号
     */
    private int id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 部门编码
     */
    private String departmentId;

    /**
     * 工委编码
     */
    private String workingCommitteeId;

    /**
     * 角色
     */
    private String role;

    /**
     * 菜单权限列表
     */
    private List<String> menuAuthorityList;

    private boolean isBatchUpload;

    private String codeTableUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getWorkingCommitteeId() {
        return workingCommitteeId;
    }

    public void setWorkingCommitteeId(String workingCommitteeId) {
        this.workingCommitteeId = workingCommitteeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getMenuAuthorityList() {
        return menuAuthorityList;
    }

    public void setMenuAuthroityList(List<String> menuAuthorityList) {
        this.menuAuthorityList = menuAuthorityList;
    }

    public void setMenuAuthorityList(List<String> menuAuthorityList) {
        this.menuAuthorityList = menuAuthorityList;
    }

    public boolean isBatchUpload() {
        return isBatchUpload;
    }

    public void setBatchUpload(boolean batchUpload) {
        isBatchUpload = batchUpload;
    }

    public String getCodeTableUrl() {
        return codeTableUrl;
    }

    public void setCodeTableUrl(String codeTableUrl) {
        this.codeTableUrl = codeTableUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", workingCommitteeId='" + workingCommitteeId + '\'' +
                ", role='" + role + '\'' +
                ", menuAuthorityList=" + menuAuthorityList +
                ", isBatchUpload=" + isBatchUpload +
                ", codeTableUrl='" + codeTableUrl + '\'' +
                '}';
    }
}