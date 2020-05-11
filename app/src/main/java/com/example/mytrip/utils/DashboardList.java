package com.example.mytrip.utils;

import com.example.mytrip.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardList {

    public static List<Item> getList() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(DashboardItem.EXPENSE_TRACKER, R.drawable.expense));
        itemList.add(new Item(DashboardItem.TRAVEL_EVENTS, R.drawable.events));
        itemList.add(new Item(DashboardItem.TRAVEL_GALLERY, R.drawable.gallary));
        itemList.add(new Item(DashboardItem.WEATHER, R.drawable.weather));
        itemList.add(new Item(DashboardItem.LOCATION, R.drawable.location));
        return itemList;
    }
}
