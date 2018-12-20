package cn.gz.rd.datacollection.controller.request;

/**
 * @author panxuan
 * 分页查询请求所需参数
 */
public class PageRequest {

    /**
     * 页码
     */
    private int pageNum;

    /**
     * 每页显示条数
     */
    private int pageSize;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
