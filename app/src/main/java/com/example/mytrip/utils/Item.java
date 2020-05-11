package com.example.mytrip.utils;

public class Item {
    private DashboardItem item;
    private int itemImage;

    public Item(DashboardItem item, int itemImage) {
        this.item = item;
        this.itemImage = itemImage;
    }

    public DashboardItem getItem() {
        return item;
    }

    public int getItemImage() {
        return itemImage;
    }
}
