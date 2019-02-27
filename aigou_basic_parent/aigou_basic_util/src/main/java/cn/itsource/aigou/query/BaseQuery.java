package cn.itsource.aigou.query;

public class BaseQuery {
    //query做为查询: keyword
    private String keyword;//查询关键字

    private Integer page=1;//当前页 (page-1)*size
    private Integer rows=10;//每页条数

    private Integer start=0;//从哪里开始

    public Integer getStart() {
        // (page-1)*size
        return (this.page-1)*this.rows;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
