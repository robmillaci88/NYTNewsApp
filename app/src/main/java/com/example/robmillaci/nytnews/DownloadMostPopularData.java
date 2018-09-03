package com.example.robmillaci.nytnews;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadMostPopularData extends AsyncTask<String, Integer, ArrayList> {
    ArrayList<DownloadMostPopularData.topnewsObjects> objects = new ArrayList();
    JSONObject reader;
    DownloadMostPopularDataCallback mDownloadMostPopularDataCallback;
    String category = "";

    public DownloadMostPopularData(DownloadMostPopularDataCallback downloadMostPopularDataCallback, String category) {
        mDownloadMostPopularDataCallback = downloadMostPopularDataCallback;
        this.category = category;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        URL url = null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            reader = new JSONObject(sb.toString());
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


                DownloadMostPopularData.topnewsObjects newsObject = new DownloadMostPopularData.topnewsObjects(section, title, abStract, link, byLine, pubdate, imgUrl);
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

    public class topnewsObjects {
        String section = "";
        String title = "";
        String abStract = "";
        String link = "";
        String byLine = "";
        String pubDate = "";
        String imgUrl = "";

        public topnewsObjects(String section, String title, String abStract, String url, String byLine, String pubDate, String imgUrl) {
            this.section = section;
            this.title = title;
            this.abStract = abStract;
            this.link = url;
            this.byLine = byLine;
            this.pubDate = pubDate;
            this.imgUrl = imgUrl;
            Log.d("news", "newsObjects: object created");
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getSection() {
            return section;
        }


        public String getTitle() {
            return title;
        }

        public String getAbStract() {
            return abStract;
        }

        public String getLink() {
            return link;
        }

        public String getByLine() {
            return byLine;
        }

        public String getPubDate() {
            return pubDate;
        }
    }

    interface DownloadMostPopularDataCallback{
        void popularDataDownloadFinished(ArrayList data,String category);
        void progressUpdateCallback(Integer... values);
    }

}
