package com.example.robmillaci.nytnews.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robmillaci.nytnews.Data.SearchNewsItemsAsynchTask;
import com.example.robmillaci.nytnews.Models.SearchNewsObjectModel;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Activities.WebActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.MyViewHolder> {

    private ArrayList downloadedData;
    private Context mContext;
    public static ArrayList<String> articlesReadArray;


    public SearchAdaptor(ArrayList downloadedData, Context mContext) {
        this.downloadedData = downloadedData;
        this.mContext = mContext;
        if (articlesReadArray == null) {
            Log.d("doin", "SearchAdaptor: created new array list");
            articlesReadArray = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recycler_view, parent, false);
        return new SearchAdaptor.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final SearchNewsObjectModel object = (SearchNewsObjectModel) downloadedData.get(position);
        holder.headline.setText(object.getHeadline());
        holder.snippet.setText(object.getSnippet());

        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String rawDate = object.pubDate;

        try {
            Date date = inputFormat.parse(rawDate);
            holder.pubDate.setText("Published data: " + outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (object.getImageUrl().equals("noImage")) {
            holder.searchImage.setImageResource(R.drawable.noimage);
        } else {
            Picasso.with(mContext).load(object.getImageUrl()).into(holder.searchImage);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", object.webLink);
                mContext.startActivity(intent);
                articlesReadArray.add(object.webLink);
                notifyDataSetChanged();
            }
        });

        for (String s : articlesReadArray) {
            if (s.equals(object.webLink)) {
                holder.getRead().setVisibility(View.VISIBLE);
                holder.getReadText().setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return downloadedData.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView headline;
        TextView snippet;
        TextView pubDate;
        ImageView read;
        ImageView searchImage;
        TextView readText;

        MyViewHolder(View itemView) {
            super(itemView);
            this.headline = itemView.findViewById(R.id.headline);
            this.snippet = itemView.findViewById(R.id.snippet);
            this.pubDate = itemView.findViewById(R.id.pubdate);
            this.read = itemView.findViewById(R.id.read);
            this.readText = itemView.findViewById(R.id.readText);
            this.searchImage = itemView.findViewById(R.id.searchImage);
        }

        public ImageView getRead() {
            return read;
        }

        public TextView getReadText() {
            return readText;
        }

    }
}
