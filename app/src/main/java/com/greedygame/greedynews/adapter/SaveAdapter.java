package com.greedygame.greedynews.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greedygame.greedynews.R;
import com.greedygame.greedynews.activity.DetailsNews;
import com.greedygame.greedynews.model.Articles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.MyViewHolder> {
    Context context;
    List<Articles> articlesList;
    public SaveAdapter(Context context, List<Articles> articlesList) {
        this.context = context;
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_saved_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(articlesList.get(position).title);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {

            String reformattedStr = myFormat.format(fromUser.parse(articlesList.get(position).publishedAt));
            holder.tvDate.setText(reformattedStr + "   " +articlesList.get(position).author);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context).
                load(articlesList.get(position).urlToImage).
                placeholder(R.drawable.image_not_found).
                into(holder.imgBanner);

        holder.rlWholeView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsNews.class);
            intent.putExtra("detailed_news", new Gson().toJson(articlesList.get(position)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView imgBanner;
        RelativeLayout rlWholeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgBanner = itemView.findViewById(R.id.img_banner);
            rlWholeView = itemView.findViewById(R.id.rl_whole_view);
        }
    }
}
