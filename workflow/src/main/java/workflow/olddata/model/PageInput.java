package workflow.olddata.model;

public class PageInput extends  BaseInputType {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页显示多少条
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 是否是降序
     */
    private Boolean desc;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Boolean getDesc() {
        return desc;
    }

    public void setDesc(Boolean desc) {
        this.desc = desc;
    }
}
