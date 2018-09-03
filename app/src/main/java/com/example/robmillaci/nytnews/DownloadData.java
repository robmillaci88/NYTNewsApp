package com.example.robmillaci.nytnews;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadData extends AsyncTask<String, Integer, ArrayList> {
    ArrayList<newsObjects> objects = new ArrayList();
    JSONObject reader;
    DownloadDataCallback mDownloadDataCallback;

    public DownloadData(DownloadDataCallback downloadDataCallback) {
        mDownloadDataCallback = downloadDataCallback;
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
                Log.d("downloading", "doInBackground: " + line);
            }

            reader = new JSONObject(sb.toString());
            JSONArray items = reader.getJSONArray("results");
            for (int i = 0; i < items.length(); i++) {
                publishProgress(i,items.length());
                JSONObject item = items.getJSONObject(i);
                String section = item.getString("section");
                String subsection = item.getString("subsection");
                String title = item.getString("title");
                String abStract = item.getString("abstract");
                String link = item.getString("url");
                String byLine = item.getString("byline");
                String pubdate = item.getString("published_date");

                try {
                    JSONArray multimedia = item.getJSONArray("multimedia");
                    JSONObject mediaObject = (JSONObject) multimedia.get(1);
                    String imgLink = mediaObject.getString("url");
                    newsObjects newsObject = new newsObjects(section, subsection, title, abStract, link, byLine, pubdate, imgLink);
                    objects.add(newsObject);
                } catch (JSONException e){
                    e.getMessage();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("progress", "onProgressUpdate: called");
        mDownloadDataCallback.progressUpdateCallback(values[0],values[1]);

    }

    @Override
    protected void onPostExecute(ArrayList a) {
       mDownloadDataCallback.downloadFinished(a);
    }

    public class newsObjects {
        String section = "";
        String subsection = "";
        String title = "";
        String abStract = "";
        String link = "";
        String byLine = "";
        String pubDate = "";
        String imgUrl = "";

        public newsObjects(String section, String subsection, String title, String abStract, String url, String byLine, String pubDate, String imgUrl) {
            this.section = section;
            this.subsection = subsection;
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

        public String getSubsection() {
            return subsection;
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

    interface DownloadDataCallback{
        void downloadFinished(ArrayList downloadData);
        void progressUpdateCallback(Integer... values);
    }

}
