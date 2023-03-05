package com.example.newsapp20.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp20.Model.NewsModel;
import com.example.newsapp20.NewsDetailActivity;
import com.example.newsapp20.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsDetailHolder> {

    private Context context;
    private ArrayList<NewsModel> newsArrayList;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public NewsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_news_source_details, parent, false);
        return new NewsDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsDetailHolder holder, int index) {
        NewsModel model = newsArrayList.get(index);

        String content = model.getContent();
        String description = model.getDescription();
        String publishedAt = model.getPublishedAt();
        String title = model.getTitle();
        String url = model.getUrl();
        String urlToImage = model.getUrlToImage();

        // set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(publishedAt);
        Picasso.get().load(urlToImage).into(holder.imageIv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    class NewsDetailHolder extends RecyclerView.ViewHolder{

        TextView titleTv, descriptionTv, dateTv;
        ImageView imageIv;

        public NewsDetailHolder(@NonNull View view) {
            super(view);
            titleTv = view.findViewById(R.id.titleTv);
            imageIv = view.findViewById(R.id.imageIv);
            descriptionTv = view.findViewById(R.id.descriptionTv);
            dateTv = view.findViewById(R.id.dateTv);
        }
    }
}
