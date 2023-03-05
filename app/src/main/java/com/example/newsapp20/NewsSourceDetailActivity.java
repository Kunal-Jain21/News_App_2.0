package com.example.newsapp20;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp20.Adapter.NewsAdapter;
import com.example.newsapp20.Model.NewsModel;
import com.example.newsapp20.Model.NewsSourceModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewsSourceDetailActivity extends AppCompatActivity {

    private TextView nameTv, descriptionTv, countryTv, categoryTv, languageTv;
    private RecyclerView newsRv;

    private ArrayList<NewsModel> sourceDetailArrayList;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_source_detail);

        //actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Latest News");
        // add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // init ui view
        nameTv = findViewById(R.id.nameTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        countryTv = findViewById(R.id.countryTv);
        categoryTv = findViewById(R.id.categoryTv);
        languageTv = findViewById(R.id.languageTv);
        newsRv = findViewById(R.id.newsRv);

        // get data from intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String country = intent.getStringExtra("country");
        String category = intent.getStringExtra("category");
        String language = intent.getStringExtra("language");

        actionBar.setTitle(name); // set name of news we selected

        nameTv.setText(name);
        descriptionTv.setText(description);
        countryTv.setText("Country: "  + country);
        categoryTv.setText("Category: "  + category);
        languageTv.setText("Language: "  + language);

        loadNewsData(id);
    }

    private void loadNewsData(String id) {
        sourceDetailArrayList = new ArrayList<>();

        String url = "https://newsapi.org/v2/top-headlines?sources=" + id + "&apiKey=" + Utility.API_KEY;

        // progress bar
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading News");
        progressDialog.show();

        // request data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // we got message
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    for (int i=0; i<jsonArray.length(); i++) {
                        // each element is jsonObject
                        JSONObject jsonObjectNew = jsonArray.getJSONObject(i);
                        String title = jsonObjectNew.getString("title");
                        String description = jsonObjectNew.getString("description");
                        String url = jsonObjectNew.getString("url");
                        String urlToImage = jsonObjectNew.getString("urlToImage");
                        String publishedAt = jsonObjectNew.getString("publishedAt");
                        String content = jsonObjectNew.getString("content");

                        // convert date in req format
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String formattedDate = "";
                        try {
                            Date date = dateFormat1.parse(publishedAt);
                            formattedDate = dateFormat2.format(date);
                        }
                        catch (Exception e) {
                            formattedDate = publishedAt;
                        }
                        NewsModel model = new NewsModel(title, description, url, urlToImage,formattedDate, content);
                        sourceDetailArrayList.add(model);
                    }

                    progressDialog.dismiss();

                    // setup adapter
                    newsAdapter = new NewsAdapter(NewsSourceDetailActivity.this, sourceDetailArrayList);
                    newsRv.setAdapter(newsAdapter);

                }catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(NewsSourceDetailActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // failed getting message
                progressDialog.dismiss();
                Toast.makeText(NewsSourceDetailActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("User-Agent", "Mozilla/5.0");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}