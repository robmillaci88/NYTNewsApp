package com.example.robmillaci.nytnews.Data;

import android.os.AsyncTask;

import com.example.robmillaci.nytnews.Models.TopNewsObjectModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MostPopularNewsAysnchTask extends AsyncTask<String, Integer, ArrayList> {
    private final ArrayList<TopNewsObjectModel> objects = new ArrayList<>(); //arraylist to hold the downloaded data's TopNewsObjects
    private final DownloadMostPopularDataCallback mDownloadMostPopularDataCallback; //the callback to be used from this class
    private final String category; //the category of downloaded data

    public MostPopularNewsAysnchTask(DownloadMostPopularDataCallback downloadMostPopularDataCallback, String category) {
        mDownloadMostPopularDataCallback = downloadMostPopularDataCallback;
        this.category = category;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        //Asynch task that uses and inputstream reader and a buffered reader to download the data from the supplied URL
        //A JsonReader object is then used to extract the relevant JSON field. The extracted JSON fields are passed to the constructor of the
        //top news objects
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

                String section = item.getString("section"); //get the section string
                String title = item.getString("title"); //get the title string
                String abStract = item.getString("abstract"); //get the abstract string
                String link = item.getString("url"); //get the link string
                String byLine = item.getString("byline"); //get the byline string
                String pubdate = item.getString("published_date"); //get the pubdate string

                JSONArray multimedia = item.getJSONArray("media"); //get the multimedia array
                JSONObject metaData = multimedia.getJSONObject(0); //get the metadata object
                JSONArray metaArray = metaData.getJSONArray("media-metadata"); //get the metaArray from the metadata object
                JSONObject imgArray = metaArray.getJSONObject(1); //get the image information from the metaArray
                String imgUrl = imgArray.getString("url"); //get the URL of the image from the imgArray object

                TopNewsObjectModel newsObject = new TopNewsObjectModel(title,
                        abStract, link, byLine, pubdate, imgUrl);
                objects.add(newsObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    //progress update callback for displaying the progress bar as the data is downloaded Asynchronously
    @Override
    protected void onProgressUpdate(Integer... values) {
        mDownloadMostPopularDataCallback.progressUpdateCallback(values);
    }


    //post execution callback to set the recyclerview adaptor with the newly downloaded data
    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        mDownloadMostPopularDataCallback.popularDataDownloadFinished(arrayList,category);
    }

    //interface for the callbacks. Any class using this Asynch class must implement its callbacks
    public interface DownloadMostPopularDataCallback{
        void popularDataDownloadFinished(ArrayList data,String category);
        void progressUpdateCallback(Integer... values);
    }

}
