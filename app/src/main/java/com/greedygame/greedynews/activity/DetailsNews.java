package com.greedygame.greedynews.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greedygame.greedynews.R;
import com.greedygame.greedynews.adapter.NewsAdapter;
import com.greedygame.greedynews.model.Articles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailsNews extends AppCompatActivity {
    Articles articles;
    TextView tvTitle, tvDescription, tvContent, tvDate, tvAuthor, tvSource;
    ImageView imgBanner, imgSave;
    CardView cardBody;
    Button btnSave;
    SharedPreferences saveLocalData;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_news);

        getSupportActionBar().hide();

        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);
        tvContent = findViewById(R.id.tv_content);
        tvDate = findViewById(R.id.tv_date);
        tvAuthor = findViewById(R.id.tv_author);
        tvSource = findViewById(R.id.tv_source);
        imgBanner = findViewById(R.id.img_banner);
        imgSave = findViewById(R.id.img_save);
        cardBody = findViewById(R.id.card_body);
        btnSave = findViewById(R.id.btn_save);

        saveLocalData= getSharedPreferences("MySharedPref",MODE_PRIVATE);
        editor = saveLocalData.edit();

        cardBody.setBackgroundResource(R.drawable.top_round_conrer);

        articles = new Articles();
        articles = new Gson().fromJson((getIntent().getStringExtra("detailed_news")), Articles.class);

        tvTitle.setText(articles.title);
        tvDescription.setText(articles.description);
        tvContent.setText(articles.content);
        tvAuthor.setText(articles.author);
        tvSource.setText(articles.source.name);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {

            String reformattedStr = myFormat.format(fromUser.parse(articles.publishedAt));
            tvDate.setText(reformattedStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(this).
                load(articles.urlToImage).
                placeholder(R.drawable.image_not_found).
                into(imgBanner);
        if (new Gson().toJson(getSavedData()).contains(new Gson().toJson(articles.url))){
            imgSave.setImageResource(R.drawable.save_fill);
            btnSave.setText("SAVED");
        }
        else {
            imgSave.setImageResource(R.drawable.save_outline);
            btnSave.setText("SAVE");
        }

        imgSave.setOnClickListener(view -> {
            saveNews(articles);
        });
        btnSave.setOnClickListener(view -> saveNews(articles));
    }
    private void saveNews(Articles articles) {
        if (new Gson().toJson(getSavedData()).contains(new Gson().toJson(articles.url))){
            removeData(articles);
            imgSave.setColorFilter(Color.WHITE);
            btnSave.setText("SAVE");
            imgSave.setImageResource(R.drawable.save_outline);
        }
        else {
            saveData(articles);
            imgSave.setColorFilter(Color.WHITE);
            imgSave.setImageResource(R.drawable.save_fill);
            btnSave.setText("SAVED");
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
}