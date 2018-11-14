package com.example.robmillaci.nytnews.Data;

import android.os.AsyncTask;

import com.example.robmillaci.nytnews.Models.NewsObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsListAsynchTask extends AsyncTask<String, Integer, ArrayList> {
    private final ArrayList<NewsObjectModel> objects = new ArrayList<>(); //Arraylist to hold the downloaded news objects
    private final DownloadDataCallback mDownloadDataCallback; //the callback used by this class to callback progress and datadownloaded

    public NewsListAsynchTask(DownloadDataCallback downloadDataCallback) {
        mDownloadDataCallback = downloadDataCallback;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        //Asynch task that uses and inputstream reader and a buffered reader to download the data from the supplied URL
        //A JsonReader object is then used to extract the relevant JSON field. The extracted JSON fields are passed to the constructor of the
        //news objects
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
            JSONArray items = reader.getJSONArray("results"); //get the JSON results from the reader
            for (int i = 0; i < items.length(); i++) {
                publishProgress(i,items.length());
                JSONObject item = items.getJSONObject(i); //get the JSON object
                String section = item.getString("section"); //get the Json objects section text
                String subsection = item.getString("subsection"); //get the Json objects subsection text
                String title = item.getString("title"); //get the Json objects title
                String abStract = item.getString("abstract"); //get the Json objects abstract
                String link = item.getString("url"); //get the Json objects URL
                String byLine = item.getString("byline"); //get the Json objects byline
                String pubdate = item.getString("published_date"); //get the Json objects published date

                try {
                    JSONArray multimedia = item.getJSONArray("multimedia"); //get the multimedia Json array associated with the Json object
                    JSONObject mediaObject = (JSONObject) multimedia.get(1); //get the media object from the Json array
                    String imgLink = mediaObject.getString("url"); //get the image link from the media object

                    //construct a new newsObject, passing the relevant extracted data above and then add this object to the objects arraylist
                    NewsObjectModel newsObject = new NewsObjectModel(section, subsection, title, abStract, link, byLine, pubdate, imgLink);
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

    //progress update callback for displaying the progress bar as the data is downloaded Asynchronously
    @Override
    protected void onProgressUpdate(Integer... values) {
        mDownloadDataCallback.progressUpdateCallback(values[0],values[1]);
    }

    //post execution callback to set the recyclerview adaptor with the newly downloaded data
    @Override
    protected void onPostExecute(ArrayList a) {
       mDownloadDataCallback.downloadFinished(a);
    }

    //interface for the callbacks. Any class using this Asynch class must implement its callbacks
    public interface DownloadDataCallback{
        void downloadFinished(ArrayList downloadData);
        void progressUpdateCallback(Integer... values);
    }

}
