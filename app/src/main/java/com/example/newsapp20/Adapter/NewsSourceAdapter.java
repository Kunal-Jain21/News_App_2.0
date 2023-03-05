package com.example.newsapp20.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp20.FilterSourceList;
import com.example.newsapp20.Model.NewsSourceModel;
import com.example.newsapp20.NewsSourceDetailActivity;
import com.example.newsapp20.R;

import java.util.ArrayList;

public class NewsSourceAdapter extends RecyclerView.Adapter<NewsSourceAdapter.HolderSourceList> implements Filterable {

    private final Context context;
    public ArrayList<NewsSourceModel> sourceLists, filterList;
    private FilterSourceList filter;

    public NewsSourceAdapter(Context context, ArrayList<NewsSourceModel> sourceLists) {
        this.context = context;
        this.sourceLists = sourceLists;
        this.filterList = sourceLists;
    }

    @NonNull
    @Override
    public HolderSourceList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_source_list, parent, false);
        return new HolderSourceList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSourceList holder, int position) {
        NewsSourceModel model = sourceLists.get(position);

        // get data
        String id = model.getId();
        String name = model.getName();
        String description = model.getDescription();
        String country = model.getCountry();
        String category = model.getCategory();
        String language = model.getLanguage();

        // set Data to ui
        holder.nameTv.setText(name);
        holder.descriptionTv.setText(description);
        holder.countryTv.setText(String.format("Country: %s", country));
        holder.categoryTv.setText(String.format("Category: %s", category));
        holder.languageTv.setText(String.format("Language: %s", language));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, NewsSourceDetailActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("description", description);
            intent.putExtra("country", country);
            intent.putExtra("category", category);
            intent.putExtra("language", language);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sourceLists.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterSourceList(this, filterList);
        }
        return filter;
    }

    class HolderSourceList extends RecyclerView.ViewHolder {

        TextView nameTv, descriptionTv, countryTv, categoryTv, languageTv;

        public HolderSourceList(@NonNull View view) {
            super(view);
            nameTv = view.findViewById(R.id.nameTv);
            descriptionTv = view.findViewById(R.id.descriptionTv);
            countryTv = view.findViewById(R.id.countryTv);
            categoryTv = view.findViewById(R.id.categoryTv);
            languageTv = view.findViewById(R.id.languageTv);
        }
    }
}
