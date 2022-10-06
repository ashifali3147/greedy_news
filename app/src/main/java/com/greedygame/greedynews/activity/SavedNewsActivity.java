package com.greedygame.greedynews.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greedygame.greedynews.R;
import com.greedygame.greedynews.adapter.NewsAdapter;
import com.greedygame.greedynews.adapter.SaveAdapter;
import com.greedygame.greedynews.model.Articles;

import java.util.ArrayList;
import java.util.List;

public class SavedNewsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SaveAdapter saveAdapter;
    List<Articles> articlesList;
    EditText edtSearch;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences saveLocalData;
    SharedPreferences.Editor editor;
    CardView cardBody;
    TextView tvEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_lay);
        edtSearch = findViewById(R.id.edt_search);
        cardBody = findViewById(R.id.card_body);
        tvEmpty = findViewById(R.id.tv_empty);
        cardBody.setBackgroundResource(R.drawable.top_round_conrer);
        saveLocalData= getSharedPreferences("MySharedPref",MODE_PRIVATE);
        editor = saveLocalData.edit();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Articles> sortedList = new ArrayList<>();
                for (Articles list : articlesList){
                    if (list.title.toLowerCase().contains(String.valueOf(charSequence).toLowerCase())){
                        sortedList.add(list);
                    }
                }
                setAdapter(sortedList);
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        articlesList = new ArrayList<>();
        articlesList = new Gson().fromJson(saveLocalData.getString("saveData", new Gson().toJson(articlesList)), new TypeToken<List<Articles>>(){}.getType());
        setAdapter(articlesList);
    }

    public void setAdapter(List<Articles> articlesList){
        saveAdapter = new SaveAdapter(getApplicationContext(), articlesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (articlesList.size()==0){
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setAdapter(saveAdapter);
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}