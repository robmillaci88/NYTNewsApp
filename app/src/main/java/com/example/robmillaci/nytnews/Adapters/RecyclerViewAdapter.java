package com.example.robmillaci.nytnews.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robmillaci.nytnews.Activities.WebActivity;
import com.example.robmillaci.nytnews.Models.NewsObjectModel;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.GsonHelper;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList downloadedData; //an arraylist that holds the downloaded data
    private Context mContext; //the context of the calling activity
    private ArrayList<String> articlesReadArray; //an arraylist that holds all read articles

    public RecyclerViewAdapter(ArrayList downloadedData, Context context) {
        this.downloadedData = downloadedData;
        this.mContext = context;
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", MODE_PRIVATE);

        if (prefs != null) {
            try {
                //restore previously read articles
                //noinspection unchecked
                articlesReadArray = GsonHelper.getMyArray(context, "RecyclerViewReadArray");
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
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.MyViewHolder holder, final int position) {
        final NewsObjectModel object = (NewsObjectModel) downloadedData.get(position); //extract the revelant News object from the arraylist
        holder.section.setText(object.getSection() + " > " + object.getSubsection()); //set the views section text with the objects section text
        holder.title.setText(object.getTitle()); //sets the views title to the objects title
        holder.abStract.setText(object.getAbStract()); //sets the views abstract to the objects abstract
        holder.link.setText(object.getLink()); //sets the views link to the objects link
        holder.byLine.setText(object.getByLine()); //sets the views byLine to the objects byLine

        String rawDate = object.getPubDate(); //extract the raw published date of the news object and format the date
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(rawDate);
            holder.pubDate.setText("Published data: " + outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(mContext).load(object.getImgUrl()).into(holder.newsImage); //using picasso download the objects image and load this into the imageview

        //sets the views onclick listener to display the news article in a webview in order to see the full article
        //also then add this article to the articlesReadArray and store this in shared preferences
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", object.getLink());
                mContext.startActivity(intent);
                articlesReadArray.add(holder.link.getText().toString());
                GsonHelper.storeMyArray(mContext, "RecyclerViewReadArray", articlesReadArray);
                notifyDataSetChanged();
            }
        });

        //determines wether the article has previously been read. If it has notify the user that they have read this.
        if (articlesReadArray != null) {
            for (Object s : articlesReadArray) {
                if (s.equals(holder.link.getText().toString())) {
                    holder.getRead().setVisibility(View.VISIBLE);
                    holder.getReadText().setVisibility(View.VISIBLE);
                }else {
                    holder.getRead().setVisibility(View.GONE);
                    holder.getReadText().setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return downloadedData.size();

    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView section;
        TextView title;
        TextView abStract;
        TextView link;
        TextView byLine;
        TextView pubDate;
        ImageView newsImage;
        ImageView read;
        TextView readText;

        private MyViewHolder(View itemView) {

            super(itemView);
            this.section = itemView.findViewById(R.id.section);
            this.title = itemView.findViewById(R.id.newsTitle);
            this.abStract = itemView.findViewById(R.id.aBstract);
            this.link = itemView.findViewById(R.id.webLink);
            this.byLine = itemView.findViewById(R.id.byline);
            this.pubDate = itemView.findViewById(R.id.pubdate);
            this.newsImage = itemView.findViewById(R.id.newsImage);
            this.read = itemView.findViewById(R.id.read);
            this.readText = itemView.findViewById(R.id.readText);

        }

        public ImageView getRead() {
            return read;
        }

        public TextView getReadText() {
            return readText;
        }
    }


}

