package com.example.mytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.mytrip.R;
import com.example.mytrip.utils.Item;

import java.util.List;

import javax.inject.Inject;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardAdapterViewHolder> {

    private NavController navController;
    private RequestManager requestManager;
    private List<Item> itemList;

    @Inject
    public DashboardAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void setData(List<Item> itemList, NavController navController) {
        this.itemList = itemList;
        this.navController = navController;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DashboardAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new DashboardAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapterViewHolder holder, int position) {
        if (itemList != null) {
            Item item = itemList.get(position);
            requestManager.load(item.getItemImage()).into(holder.imageView);
            holder.cardView.setOnClickListener(clicked -> {
                switch (item.getItem()) {
                    case EXPENSE_TRACKER:
                        navController.navigate(R.id.action_nav_home_to_expenseFragment);
                        break;
                    case TRAVEL_EVENTS:
                        navController.navigate(R.id.action_nav_home_to_eventFragment);
                        break;
                    case TRAVEL_GALLERY:
                        navController.navigate(R.id.action_nav_home_to_momentFragment);
                        break;
                    case WEATHER:
                        navController.navigate(R.id.action_nav_home_to_weatherFragment);
                        break;
                    case LOCATION:
                        navController.navigate(R.id.action_nav_home_to_locationFragment);
                        break;
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        if (itemList == null) return 0;
        else return itemList.size();
    }

    static class DashboardAdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CardView cardView;

        DashboardAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            cardView = itemView.findViewById(R.id.item);
        }
    }
}
