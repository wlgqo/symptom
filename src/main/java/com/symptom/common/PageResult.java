package com.symptom.common;

import java.util.Collections;
import java.util.List;

public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, int page, int pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(Collections.emptyList(), 0, page, pageSize);
    }

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalPages() {
        if (pageSize <= 0) return 0;
        return (int) Math.ceil((double) total / pageSize);
    }
}
