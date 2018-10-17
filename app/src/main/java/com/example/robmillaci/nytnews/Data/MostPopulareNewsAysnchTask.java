package com.example.robmillaci.nytnews.Data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.robmillaci.nytnews.Models.TopNewsObjectModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MostPopulareNewsAysnchTask extends AsyncTask<String, Integer, ArrayList> {
    private ArrayList<TopNewsObjectModel> objects = new ArrayList<>();
    private DownloadMostPopularDataCallback mDownloadMostPopularDataCallback;
    private String category;

    public MostPopulareNewsAysnchTask(DownloadMostPopularDataCallback downloadMostPopularDataCallback, String category) {
        mDownloadMostPopularDataCallback = downloadMostPopularDataCallback;
        this.category = category;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        URL url;
        try {
            url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            JSONObject reader = new JSONObject(sb.toString());
            JSONArray items = reader.getJSONArray("results");
            for (int i = 0; i < items.length(); i++) {
                publishProgress(i,items.length());
                JSONObject item = items.getJSONObject(i);
                String section = item.getString("section");
                String title = item.getString("title");
                String abStract = item.getString("abstract");
                String link = item.getString("url");
                String byLine = item.getString("byline");
                String pubdate = item.getString("published_date");

                JSONArray multimedia = item.getJSONArray("media");
                JSONObject metaData = multimedia.getJSONObject(0);
                JSONArray metaArray = metaData.getJSONArray("media-metadata");
                JSONObject imgArray = metaArray.getJSONObject(1);
                String imgUrl = imgArray.getString("url");

                TopNewsObjectModel newsObject = new TopNewsObjectModel(section, title, abStract, link, byLine, pubdate, imgUrl);
                objects.add(newsObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mDownloadMostPopularDataCallback.progressUpdateCallback(values);
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        mDownloadMostPopularDataCallback.popularDataDownloadFinished(arrayList,category);
    }


    public interface DownloadMostPopularDataCallback{
        void popularDataDownloadFinished(ArrayList data,String category);
        void progressUpdateCallback(Integer... values);
    }

}
