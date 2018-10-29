package com.example.robmillaci.nytnews.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList downloadedData;
    private Context mContext;
    public static ArrayList<String> articlesReadArray;

    public RecyclerViewAdapter(ArrayList downloadedData, Context context) {
        this.downloadedData = downloadedData;
        this.mContext = context;
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
        final NewsObjectModel object = (NewsObjectModel) downloadedData.get(position);
        holder.section.setText(object.getSection() + " > " + object.getSubsection());
        holder.title.setText(object.getTitle());
        holder.abStract.setText(object.getAbStract());
        holder.link.setText(object.getLink());
        holder.byLine.setText(object.getByLine());

        String rawDate = object.getPubDate();
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(rawDate);
            holder.pubDate.setText("Published data: " + outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(mContext).load(object.getImgUrl()).into(holder.newsImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", object.getLink());
                mContext.startActivity(intent);
                articlesReadArray.add(holder.link.getText().toString());
                notifyDataSetChanged();
            }
        });


        if (articlesReadArray != null) {
            for (Object s : articlesReadArray) {
                if (s.equals(holder.link.getText().toString())) {
                    holder.getRead().setVisibility(View.VISIBLE);
                    holder.getReadText().setVisibility(View.VISIBLE);
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

