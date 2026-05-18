package com.iblog.util;

import java.util.List;

public class PageBean {
    private int curPage;      // 当前页码（从1开始）
    private int pageSize;     // 每页大小
    private int totalRows;    // 总记录数
    private List<?> data;     // 当前页数据

    public PageBean() {}

    public PageBean(int curPage, int pageSize, int totalRows, List<?> data) {
        this.curPage = curPage;
        this.pageSize = pageSize;
        this.totalRows = totalRows;
        this.data = data;
    }

    // 计算总页数
    public int getTotalPages() {
        if (totalRows <= 0) return 0;
        return (totalRows + pageSize - 1) / pageSize;
    }

    // 是否有上一页
    public boolean hasPrevious() {
        return curPage > 1;
    }

    // 是否有下一页
    public boolean hasNext() {
        return curPage < getTotalPages();
    }

    // 获取 SQL LIMIT 偏移量
    public int getOffset() {
        return (curPage - 1) * pageSize;
    }

    // getter/setter
    public int getCurPage() { return curPage; }
    public void setCurPage(int curPage) { this.curPage = curPage; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public List<?> getData() { return data; }
    public void setData(List<?> data) { this.data = data; }
}
