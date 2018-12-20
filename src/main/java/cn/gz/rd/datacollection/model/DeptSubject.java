package cn.gz.rd.datacollection.model;

import java.util.Date;

public class DeptSubject {
    private Integer xh;

    private String ztbh;

    private String bmbh;

    private Date cjsj;

    public Integer getXh() {
        return xh;
    }

    public void setXh(Integer xh) {
        this.xh = xh;
    }

    public String getZtbh() {
        return ztbh;
    }

    public void setZtbh(String ztbh) {
        this.ztbh = ztbh;
    }

    public String getBmbh() {
        return bmbh;
    }

    public void setBmbh(String bmbh) {
        this.bmbh = bmbh;
    }

    public Date getCjsj() {
        return cjsj;
    }

    public void setCjsj(Date cjsj) {
        this.cjsj = cjsj;
    }
}