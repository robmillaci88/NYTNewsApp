package com.example.robmillaci.nytnews.Data;

import android.os.AsyncTask;

import com.example.robmillaci.nytnews.Models.SearchNewsObjectModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchNewsItemsAsynchTask extends AsyncTask<String, Integer, ArrayList> {
    ArrayList<SearchNewsObjectModel> searchObjectsArray = new ArrayList<>();
    JSONObject reader;
    String imageUrl;
    downloadcallback mDownloadcallback;
    int objectsArraySize;

    public SearchNewsItemsAsynchTask(downloadcallback downloadcallback) {
        mDownloadcallback = downloadcallback;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        publishProgress(1);
        String data = downloadWebinfo(strings[0]);
        try {
            reader = new JSONObject(data);

            JSONObject objects = reader.getJSONObject("response");
            JSONArray objectsArray = objects.getJSONArray("docs");

            objectsArraySize = objectsArray.length();
            for (int i = 0; i < objectsArray.length(); i++) {

                JSONObject arrayObject = (JSONObject) objectsArray.get(i);
                final String webLink = arrayObject.getString("web_url");

                imageUrl = downloadImage(downloadWebinfo(webLink));

                String snippet = arrayObject.getString("snippet");
                String pubDate = arrayObject.getString("pub_date");
                JSONObject objHeadLine = arrayObject.getJSONObject("headline");
                String headline = objHeadLine.getString("main");

                SearchNewsObjectModel searchNewsObject = new SearchNewsObjectModel(headline, snippet, pubDate, webLink, imageUrl);
                searchObjectsArray.add(searchNewsObject);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchObjectsArray;
    }

    private String downloadWebinfo(String uri) {
        StringBuilder sb = new StringBuilder();
        URL url = null;
        try {
            url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private String downloadImage(String html) {
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementsByTag("img").first();

        try {
            int index = element.toString().indexOf("https");
            String extractedURL = element.toString().substring(index);
            int endIndex = extractedURL.indexOf("\"");
            return extractedURL.substring(0, endIndex);
        }catch (Exception e){
            e.printStackTrace();
            return "noImage";
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mDownloadcallback.mcallback(values[0]);

    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        mDownloadcallback.downloadFinished(arrayList);
    }

    public interface downloadcallback{
        void mcallback(int progress);
        void downloadFinished(ArrayList resultData);
    }
}
