package com.example.newsapp20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    public EditText searchEt;
    private ImageButton filterBtn;
    private RecyclerView sourcesRv;

    private ArrayList<ModelSourceList> sourceLists;
    private AdapterSourceList adapterSourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        searchEt = findViewById(R.id.searchEt);
        filterBtn = findViewById(R.id.filterBtn);
        sourcesRv = findViewById(R.id.sourcesRv);

        loadSources();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterSourceList.getFilter().filter(charSequence);
                }catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // click to show filter dialog (bottom sheet)
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterBottomSheet();
            }
        });
    }

    private void filterBottomSheet() {

    }

    private void loadSources() {
        sourceLists = new ArrayList<>();
        sourceLists.clear();

        progressBar.setVisibility(View.VISIBLE);

        String url = "https://newsapi.org/v2/top-headlines/sources?apiKey=" + Constants.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //convert string to Json Object
                    JSONObject jsonObject = new JSONObject(response);
                    // get source array from that object
                    JSONArray jsonArray = jsonObject.getJSONArray("sources");

                    // get all data from that array using loop
                    for (int i=0; i< jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        String id = jsonObject1.getString("id");
                        String name = jsonObject1.getString("name");
                        String description = jsonObject1.getString("description");
                        String url = jsonObject1.getString("url");
                        String country = jsonObject1.getString("country");
                        String category = jsonObject1.getString("category");
                        String language = jsonObject1.getString("language");


                        ModelSourceList model = new ModelSourceList(id,name,description,url,category,language,country);
                        sourceLists.add(model);
                    }
                    progressBar.setVisibility(View.GONE);

                    adapterSourceList = new AdapterSourceList(MainActivity.this, sourceLists);
                    sourcesRv.setAdapter(adapterSourceList);

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.v("testing", "Line 97");
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
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
}