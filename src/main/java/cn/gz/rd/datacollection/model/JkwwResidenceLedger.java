package cn.gz.rd.datacollection.model;

/**
 * 居住区配套设施建设移交历史遗留问题台账
 */
public class JkwwResidenceLedger {

    /**
     * 类型名称：应建未建、应交未交、未办产权、改变用途、其它类别
     */
    private String typeName;

    /**
     * 合计
     */
    private Integer total;

    /**
     * 越秀区的数据总量
     */
    private Integer yxq;

    /**
     * 海珠区的数据总量
     */
    private Integer hzq;

    /**
     * 荔湾区的数据总量
     */
    private Integer lwq;

    /**
     * 天河区的数据总量
     */
    private Integer thq;

    /**
     * 白云区的数据总量
     */
    private Integer byq;

    /**
     * 黄埔区的数据总量
     */
    private Integer hpq;

    /**
     * 花都区的数据总量
     */
    private Integer hdq;

    /**
     * 番禺区的数据总量
     */
    private Integer pyq;

    /**
     * 南沙区的数据总量
     */
    private Integer nsq;

    /**
     * 从化区的数据总量
     */
    private Integer chq;

    /**
     * 增城区的数据总量
     */
    private Integer zcq;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getYxq() {
        return yxq;
    }

    public void setYxq(Integer yxq) {
        this.yxq = yxq;
    }

    public Integer getHzq() {
        return hzq;
    }

    public void setHzq(Integer hzq) {
        this.hzq = hzq;
    }

    public Integer getLwq() {
        return lwq;
    }

    public void setLwq(Integer lwq) {
        this.lwq = lwq;
    }

    public Integer getThq() {
        return thq;
    }

    public void setThq(Integer thq) {
        this.thq = thq;
    }

    public Integer getByq() {
        return byq;
    }

    public void setByq(Integer byq) {
        this.byq = byq;
    }

    public Integer getHpq() {
        return hpq;
    }

    public void setHpq(Integer hpq) {
        this.hpq = hpq;
    }

    public Integer getHdq() {
        return hdq;
    }

    public void setHdq(Integer hdq) {
        this.hdq = hdq;
    }

    public Integer getPyq() {
        return pyq;
    }

    public void setPyq(Integer pyq) {
        this.pyq = pyq;
    }

    public Integer getNsq() {
        return nsq;
    }

    public void setNsq(Integer nsq) {
        this.nsq = nsq;
    }

    public Integer getChq() {
        return chq;
    }

    public void setChq(Integer chq) {
        this.chq = chq;
    }

    public Integer getZcq() {
        return zcq;
    }

    public void setZcq(Integer zcq) {
        this.zcq = zcq;
    }
}
