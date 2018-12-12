package com.wxcs.order.revivew.po;

import lombok.Data;

import java.util.List;
@Data
public class PageInfo<T> {
    private List<T> data;
    private int pages;
    public PageInfo(List<T> data, int pages) {
        this.data = data;
        this.pages = pages;
    }
}
