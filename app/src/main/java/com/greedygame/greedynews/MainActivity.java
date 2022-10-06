package com.greedygame.greedynews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greedygame.greedynews.activity.SavedNewsActivity;
import com.greedygame.greedynews.adapter.NewsAdapter;
import com.greedygame.greedynews.model.Articles;
import com.greedygame.greedynews.model.BaseModel;
import com.greedygame.greedynews.network.ApiManager;
import com.greedygame.greedynews.network.CONST;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    List<Articles> articlesList;
    EditText edtSearch;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences saveLocalData;
    SharedPreferences.Editor editor;
    CardView cardSave;
    ImageView imgFilter;
    TextView tvEmpty;
    Dialog filterpopup;
    String country_code = "in";
    String category_field = "";
    String other = "";
    int country_index = 0, category_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_lay);
        edtSearch = findViewById(R.id.edt_search);
        cardSave = findViewById(R.id.card_save);
        imgFilter = findViewById(R.id.img_filter);
        tvEmpty = findViewById(R.id.tv_empty);
        saveLocalData= getSharedPreferences("MySharedPref",MODE_PRIVATE);
        editor = saveLocalData.edit();
        articlesList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                edtSearch.setText("");
                getData();
            }
        });
        cardSave.setOnClickListener(view -> {
            startActivity(new Intent(this, SavedNewsActivity.class));
        });
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
        imgFilter.setOnClickListener(view -> {
            showFilterPopup();
        });
        getData();
    }

    public void getData(){
        new ApiManager(CONST.getBaseUrl()).service.getHeadline(CONST.getAPI_Key(),country_code,category_field, "",other, "100").enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.body() != null){
                    if (response.body().status.equals("ok")){
                        articlesList.clear();
                        articlesList.addAll(response.body().articles);
                        editor.putString("localData", new Gson().toJson(response.body()));
                        editor.commit();
                        setAdapter(articlesList);
                    }
                    else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                articlesList = new ArrayList<>();
                articlesList = new Gson().fromJson(saveLocalData.getString("localData",new Gson().toJson(new BaseModel())), BaseModel.class).articles;
                setAdapter(articlesList);
            }
        });
    }
    public void setAdapter(List<Articles> articlesList){
        newsAdapter = new NewsAdapter(getApplicationContext(), articlesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (articlesList.size()==0){
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(newsAdapter);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (newsAdapter != null){
            newsAdapter.notifyDataSetChanged();
        }
    }
    public void showFilterPopup(){
        filterpopup = new Dialog(this);
        filterpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterpopup.setContentView(R.layout.filter_popup);
        filterpopup.show();
        Spinner countrySpinner = filterpopup.findViewById(R.id.country_spinner);
        Spinner categorySpinner = filterpopup.findViewById(R.id.category_spinner);
        EditText edtOther = filterpopup.findViewById(R.id.edt_other);
        Button btnApply = filterpopup.findViewById(R.id.btn_apply);
        Button btnReset = filterpopup.findViewById(R.id.btn_reset);
        edtOther.setText(other);
        String[] country = {"India", "United States of America", "Bangladesh", "Iceland", "South Africa", "Singapore"};
        String[] country_iso = {"in", "us", "bd", "is", "za", "sg"};
        ArrayAdapter countryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country_code = country_iso[i];
                country_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] categories = {"","Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology"};
        ArrayAdapter categorySpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_field = categories[i].toLowerCase(Locale.ROOT);
                category_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        categorySpinner.setSelection(category_index);
        countrySpinner.setSelection(country_index);

        btnApply.setOnClickListener(view -> {
            other = edtOther.getText().toString().toLowerCase(Locale.ROOT);
            filterpopup.hide();
            swipeRefreshLayout.setRefreshing(true);
            getData();
        });
        btnReset.setOnClickListener(view -> {
            categorySpinner.setSelection(0);
            countrySpinner.setSelection(0);
            edtOther.setText("");
            other = "";
        });

    }
}