package com.example.mytrip.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mytrip.R;
import com.example.mytrip.data.model.Moment;

import java.util.List;

import javax.inject.Inject;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {
    private List<Moment> momentList;
    private RequestManager requestManager;

    @Inject
    public MomentAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void setData(List<Moment> momentList) {
        this.momentList = momentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_moment, parent, false);
        return new MomentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, int position) {
        if (momentList != null) {
            Moment moment = momentList.get(position);
            holder.date.setText(moment.getDate());
            holder.message.setText(moment.getMessage());
            holder.progressBar.setVisibility(View.VISIBLE);
            requestManager.load(moment.getUri())
                    .fitCenter()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (momentList == null) return 0;
        return momentList.size();
    }

    static class MomentViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView message;
        private ImageView imageView;
        private ProgressBar progressBar;

        MomentViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateVIew);
            message = itemView.findViewById(R.id.messageVIew);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
