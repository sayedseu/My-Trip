package com.example.mytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.R;
import com.example.mytrip.data.model.Event;
import com.example.mytrip.ui.events.EventViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<Event> eventList;
    private EventViewModel eventViewModel;

    @Inject
    public EventsAdapter() {
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (eventList != null) {
            Event event = eventList.get(position);
            holder.destinationTV.setText("Destination:  " + event.getDestination());
            holder.budgetTV.setText("Budget:  \u09F3 " + event.getBudget() + "/-");
            holder.dateTV.setText("From:" + event.getFromDate() + "  -  To:" + event.getToDate());
            holder.fab.setOnClickListener(click -> {
                eventViewModel.deleteItem(event.getId());
            });
        }
    }

    @Override
    public int getItemCount() {
        if (eventList != null) return eventList.size();
        else return 0;
    }

    public void setData(List<Event> eventList, EventViewModel eventViewModel) {
        this.eventList = eventList;
        this.eventViewModel = eventViewModel;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationTV;
        private TextView budgetTV;
        private TextView dateTV;
        private FloatingActionButton fab;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationTV = itemView.findViewById(R.id.destination);
            budgetTV = itemView.findViewById(R.id.budget);
            dateTV = itemView.findViewById(R.id.date);
            fab = itemView.findViewById(R.id.deleteButton);
        }
    }
}
