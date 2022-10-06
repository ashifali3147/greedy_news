package com.greedygame.greedynews.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.transition.Hold;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greedygame.greedynews.R;
import com.greedygame.greedynews.activity.DetailsNews;
import com.greedygame.greedynews.model.Articles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    Context context;
    List<Articles> articlesList;
//    List<Articles> saveList = new ArrayList<>();
    SharedPreferences saveLocalData;
    SharedPreferences.Editor editor;
    public NewsAdapter(Context context, List<Articles> articlesList) {
        this.context = context;
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_new_view, parent, false);
        saveLocalData= context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        editor = saveLocalData.edit();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(articlesList.get(position).title);
        holder.tvDescription.setText(articlesList.get(position).description);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {

            String reformattedStr = myFormat.format(fromUser.parse(articlesList.get(position).publishedAt));
            holder.tvDate.setText(reformattedStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context).
                load(articlesList.get(position).urlToImage).
                placeholder(R.drawable.image_not_found).
                into(holder.imgBanner);

        holder.btnRead.setOnClickListener(view -> {
            startDetailActivity(position);
        });
        holder.cardSave.setOnClickListener(view -> {
            saveNews(position, holder);
        });
        holder.btnSave.setOnClickListener(view -> {
            saveNews(position, holder);
        });
        holder.rlWholeView.setOnClickListener(view -> {
            startDetailActivity(position);
        });
        if (new Gson().toJson(getSavedData()).contains(new Gson().toJson(articlesList.get(position).url))){
            holder.cardSave.setCardBackgroundColor(Color.GREEN);
            holder.imgSave.setColorFilter(Color.WHITE);
            holder.btnSave.setText("SAVED");
        }
        else {
            holder.cardSave.setCardBackgroundColor(Color.GRAY);
            holder.imgSave.setColorFilter(Color.BLACK);
            holder.btnSave.setText("SAVE");
        }
    }

    private void startDetailActivity(int position) {
        Intent intent = new Intent(context, DetailsNews.class);
        intent.putExtra("detailed_news", new Gson().toJson(articlesList.get(position)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void saveNews(int position, MyViewHolder holder) {
        if (new Gson().toJson(getSavedData()).contains(new Gson().toJson(articlesList.get(position).url))){
            removeData(articlesList.get(position));
            holder.cardSave.setCardBackgroundColor(Color.GRAY);
            holder.imgSave.setColorFilter(Color.BLACK);
            holder.btnSave.setText("SAVE");

        }
        else {
//            saveList.add(articlesList.get(position));
            saveData(articlesList.get(position));
            holder.cardSave.setCardBackgroundColor(Color.GREEN);
            holder.imgSave.setColorFilter(Color.WHITE);
            holder.btnSave.setText("SAVED");
        }
    }

    private void removeData(Articles removeList) {
        List<Articles> removeArticles = new ArrayList<>();
        if (getSavedData()!=null) {
            removeArticles.addAll(getSavedData());
        }
        removeArticles.remove(getIndex(removeList));
        editor.putString("saveData", new Gson().toJson(removeArticles));
        editor.commit();
    }

    private int getIndex(Articles removeList) {
        int index = 0;
        for (int i=0; i<getSavedData().size(); i++){
            if (getSavedData().get(i).url.equals(removeList.url)){
                index = i;
            }
        }
        return index;
    }

    private void saveData(Articles saveList) {
        List<Articles> saveArticles = new ArrayList<>();
        if (getSavedData()!=null) {
            saveArticles.addAll(getSavedData());
        }
        saveArticles.add(saveList);
        editor.putString("saveData", new Gson().toJson(saveArticles));
        editor.commit();
    }

    private List<Articles> getSavedData(){
        List<Articles> getArticles = new ArrayList<>();
        getArticles = new Gson().fromJson(saveLocalData.getString("saveData", new Gson().toJson(getArticles)), new TypeToken<List<Articles>>(){}.getType());
        return getArticles;
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        ImageView imgBanner, imgSave;
        Button btnSave, btnRead;
        CardView cardSave;
        RelativeLayout rlWholeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgBanner = itemView.findViewById(R.id.img_banner);
            btnSave = itemView.findViewById(R.id.btn_save);
            btnRead = itemView.findViewById(R.id.btn_read);
            imgSave = itemView.findViewById(R.id.img_save);
            cardSave = itemView.findViewById(R.id.card_save);
            rlWholeView = itemView.findViewById(R.id.rl_whole_view);
        }
    }
}
