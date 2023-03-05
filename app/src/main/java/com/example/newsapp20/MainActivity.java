package com.example.newsapp20;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp20.Adapter.NewsSourceAdapter;
import com.example.newsapp20.Model.NewsSourceModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    public EditText searchEt;
    private ImageButton filterBtn;
    private RecyclerView sourcesRv;

    private ArrayList<NewsSourceModel> sourceLists;
    private NewsSourceAdapter newsSourceAdapter;

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
                    newsSourceAdapter.getFilter().filter(charSequence);
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // click to show filter dialog (bottom sheet)
        filterBtn.setOnClickListener(view -> filterBottomSheet());
    }

    private String selectedCountry = "All",selectedCategory = "All",selectedLanguage = "All";
    private int selectedCountryPosition = 0,selectedCategoryPosition = 0,selectedLanguagePosition = 0;

    private void filterBottomSheet() {
        View view = LayoutInflater.from(this).inflate(R.layout.filter_layout, null);
        Spinner countrySpinner = view.findViewById(R.id.countrySpinner);
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        Spinner languageSpinner = view.findViewById(R.id.languageSpinner);
        Button applyBtn = view.findViewById(R.id.applyBtn);

        // create ArrayAdapter
        ArrayAdapter<String> adapterCountries = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Utility.COUNTRIES);
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Utility.CATEGORIES);
        ArrayAdapter<String> adapterLanguages = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Utility.LANGUAGES);
        // specify the layout to use when the list of choices appear
        adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        countrySpinner.setAdapter(adapterCountries);
        categorySpinner.setAdapter(adapterCategories);
        languageSpinner.setAdapter(adapterLanguages);

        countrySpinner.setSelection(selectedCountryPosition);
        categorySpinner.setSelection(selectedCategoryPosition);
        languageSpinner.setSelection(selectedLanguagePosition);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = Utility.COUNTRIES[i];
                selectedCountryPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = Utility.CATEGORIES[i];
                selectedCategoryPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = Utility.LANGUAGES[i];
                selectedLanguagePosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        applyBtn.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            loadSources();
        });
    }

    private void loadSources() {

        Objects.requireNonNull(getSupportActionBar()).setSubtitle("Country: " + selectedCountry +
                " Category: " + selectedCategory + " Language: " + selectedLanguage);
        sourceLists = new ArrayList<>();
        sourceLists.clear();

        if (selectedCountry.equals("All")) {
            selectedCountry = "";
        }
        if (selectedCategory.equals("All")) {
            selectedCategory = "";
        }
        if (selectedLanguage.equals("All")) {
            selectedLanguage = "";
        }

        progressBar.setVisibility(View.VISIBLE);

        String url = "https://newsapi.org/v2/top-headlines/sources?apiKey=" + Utility.API_KEY + "&country="
                + selectedCountry + "&category=" + selectedCategory +
                "&language=" + selectedLanguage;

        Log.v("testing", url);
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


                        NewsSourceModel model = new NewsSourceModel(id,name,description,url,category,language,country);
                        sourceLists.add(model);
                    }
                    progressBar.setVisibility(View.GONE);

                    newsSourceAdapter = new NewsSourceAdapter(MainActivity.this, sourceLists);
                    sourcesRv.setAdapter(newsSourceAdapter);

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
            public Map<String, String> getHeaders() {
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