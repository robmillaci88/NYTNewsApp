package com.example.robmillaci.nytnews.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robmillaci.nytnews.Models.SearchNewsObjectModel;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Activities.WebActivity;
import com.example.robmillaci.nytnews.Utils.GsonHelper;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private ArrayList downloadedData; //arraylist to hold the downloaded data
    private Context mContext; //the calling activities context
    public static ArrayList<String> articlesReadArray; //arraylist holding the read articles


    public SearchAdapter(ArrayList downloadedData, Context mContext) {
        this.downloadedData = downloadedData;
        this.mContext = mContext;
        SharedPreferences prefs = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);

        if (prefs != null) {
            try {
                //restore previously read articles
                //noinspection unchecked
                articlesReadArray = GsonHelper.getMyArray(mContext, "RecyclerViewReadArray");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (articlesReadArray == null) {
            articlesReadArray = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recycler_view, parent, false);
        return new SearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final SearchNewsObjectModel object = (SearchNewsObjectModel) downloadedData.get(position); //extract the relevant searchNewsObject
        holder.headline.setText(object.getHeadline()); //set the views headline text to the objects headline text

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/OldNewspaperTypes.ttf");
        holder.headline.setTypeface(typeface);

        holder.snippet.setText(object.getSnippet()); //sets the views snippet text to the objects snippet text

        Typeface typefaceabstract = Typeface.createFromAsset(mContext.getAssets(),"fonts/Chenier_Regular.ttf");
        holder.snippet.setTypeface(typefaceabstract);

        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String rawDate = object.pubDate; //extract the objects raw published date , format this date and then set the views published date

        try {
            holder.pubDate.setText(mContext.getString(R.string.published_date, outputFormat.format(inputFormat.parse(rawDate))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (object.getImageUrl().equals("noImage")) {
            holder.searchImage.setImageResource(R.drawable.noimage); //if no image URL is in the downloaded data, set a placeholder image
        } else {
            Picasso.with(mContext).load(object.getImageUrl()).into(holder.searchImage); //otherwise using Picasso, download the image and load it into the views
                                                                                        // imageview
        }

        //sets the views onclicklistener to displat the full article in a webview
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", object.webLink);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                articlesReadArray.add(object.webLink);
                notifyDataSetChanged();
            }
        });

        //determines wether the article has previously been read. If it has notify the user that they have read this.
        if (articlesReadArray != null) {
            if (articlesReadArray.contains(object.webLink)) {
                holder.getRead().setVisibility(View.VISIBLE);
                holder.getReadText().setVisibility(View.VISIBLE);
            }else {
                holder.getRead().setVisibility(View.GONE);
                holder.getReadText().setVisibility(View.GONE);
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
