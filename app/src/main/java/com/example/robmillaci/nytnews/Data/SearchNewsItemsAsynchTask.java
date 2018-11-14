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
    private final ArrayList<SearchNewsObjectModel> searchObjectsArray = new ArrayList<>(); //Arraylist to hold the downloaded searched news objects
    private final downloadcallback mDownloadcallback; //the callback used by this class to callback progress and datadownloaded

    public SearchNewsItemsAsynchTask(downloadcallback downloadcallback) {
        mDownloadcallback = downloadcallback;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        //Asynch task that uses and inputstream reader and a buffered reader to download the data from the supplied URL
        //A JsonReader object is then used to extract the relevant JSON field. The extracted JSON fields are passed to the constructor of the
        //Search news objects
        publishProgress(1);

        //call downloadWebinfo passing the URL passed to this asynch task
        String data = downloadWebinfo(strings[0]);
        try {

            JSONObject reader = new JSONObject(data);
            JSONObject objects = reader.getJSONObject("response"); //get the Json objects returned from the download of the data
            JSONArray objectsArray = objects.getJSONArray("docs"); //get the jason objects array

            for (int i = 0; i < objectsArray.length(); i++) {
                JSONObject arrayObject = (JSONObject) objectsArray.get(i); //get a single object from the Json objects array
                final String webLink = arrayObject.getString("web_url"); //get the web url from the Json object

                String imageUrl = downloadImage(downloadWebinfo(webLink)); //pass the web url to the downloadImage method which extracts the Image URL from the html

                String snippet = arrayObject.getString("snippet"); //get the snippet from the Json array
                JSONObject objHeadLine = arrayObject.getJSONObject("headline"); //get the headline Json object
                String headline = objHeadLine.getString("main");  //extract the headline text from the headline Json object
                String pubdate = arrayObject.getString("pub_date"); //get the published data from the json object

                //construct a new searchnewsObject passing the data extracted from the JSON
                SearchNewsObjectModel searchNewsObject = new SearchNewsObjectModel(headline, snippet, pubdate, webLink, imageUrl);
                searchObjectsArray.add(searchNewsObject);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchObjectsArray;
    }

    //method that downloaded the data related to the passed URI and returns the data as a string
    private String downloadWebinfo(String uri) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(uri);
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

    //this method parses the passed html and gets the elements with the tag 'img'. It then extracts the images URL from the parsed HTML and returns it
    private String downloadImage(String html) {
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementsByTag("img").first();

        try {
            int index = element.toString().indexOf("https");
            String extractedURL = element.toString().substring(index);
            int endIndex = extractedURL.indexOf("\"");
            return extractedURL.substring(0, endIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return "noImage";
        }
    }

    //progress update callback for displaying the progress bar as the data is downloaded Asynchronously
    @Override
    protected void onProgressUpdate(Integer... values) {
        mDownloadcallback.mcallback(values[0]);

    }

    //post execution callback to set the recyclerview adaptor with the newly downloaded data
    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        mDownloadcallback.downloadFinished(arrayList);
    }

    //interface for the callbacks. Any class using this Asynch class must implement its callbacks
    public interface downloadcallback {
        void mcallback(int progress);

        void downloadFinished(ArrayList resultData);
    }
}
