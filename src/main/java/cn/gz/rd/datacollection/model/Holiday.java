package cn.gz.rd.datacollection.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author panxuan
 * 假期实体类
 */
public class Holiday {

    /**
     * id 唯一标识
     */
    private int id;

    /**
     * 假期名
     */
    private String name;

    /**
     * 假期起始日期
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date startDate;

    /**
     * 假期结束日期
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date endDate;

    /**
     * 补班1
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date firstExtraWorkDate;

    /**
     * 补班2
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date secondExtraWorkDate;

    /**
     * 补班3
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8" )
    private Date thirdExtraWorkDate;

    private String createUser;

    private String modifyUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getFirstExtraWorkDate() {
        return firstExtraWorkDate;
    }

    public void setFirstExtraWorkDate(Date firstExtraWorkDate) {
        this.firstExtraWorkDate = firstExtraWorkDate;
    }

    public Date getSecondExtraWorkDate() {
        return secondExtraWorkDate;
    }

    public void setSecondExtraWorkDate(Date secondExtraWorkDate) {
        this.secondExtraWorkDate = secondExtraWorkDate;
    }

    public Date getThirdExtraWorkDate() {
        return thirdExtraWorkDate;
    }

    public void setThirdExtraWorkDate(Date thirdExtraWorkDate) {
        this.thirdExtraWorkDate = thirdExtraWorkDate;
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
