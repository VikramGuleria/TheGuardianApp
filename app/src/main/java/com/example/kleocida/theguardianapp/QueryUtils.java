package com.example.kleocida.theguardianapp;

/**
 * Created by Kleocida on 2017. 06. 04..
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper class related to requesting and receiving data from GUARDIAN API.
 */
class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {

    }

    static ArrayList<ArticleData> fetchArticleData(final String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }
        return extractArticlesFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building URL", e);
        }
        return url;
    }

    // Make HTTP request
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON data.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder readString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                readString.append(line);
                line = bufferedReader.readLine();
            }
        }
        return readString.toString();
    }

    /**
     * Return a list of {@link ArticleData} objects that has been built up from
     * parsing the given JSON response.
     */
    private static ArrayList<ArticleData> extractArticlesFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<ArticleData> articles = new ArrayList<>();
        try {
            JSONObject responseObjMain = new JSONObject(jsonResponse);
            JSONObject responseObj = responseObjMain.getJSONObject("response");
            JSONArray resultsArray = responseObj.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                String sectionName = null;
                String webTitle = null;
                String webPublicationDate = null;
                String shortUrl = null;
                Bitmap thumbnailBitmap = null;

                JSONObject result = resultsArray.getJSONObject(i);
                if (result.has("sectionName")) {
                    sectionName = result.getString("sectionName");
                }

                if (result.has("webTitle")) {
                    webTitle = result.getString("webTitle");
                }

                if (result.has("webPublicationDate")) {
                    webPublicationDate = result.getString("webPublicationDate");
                }

                JSONObject fields = result.getJSONObject("fields");

                if (fields.has("shortUrl")) {
                    shortUrl = fields.getString("shortUrl");
                }

                if (fields.has("thumbnail")) {
                    String thumbnail = fields.getString("thumbnail");
                    URL url = new URL(thumbnail);
                    thumbnailBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }

                ArticleData articleData = new ArticleData(sectionName, webTitle, thumbnailBitmap, webPublicationDate, shortUrl);
                articles.add(articleData);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON file", e);
        } catch (MalformedURLException urle) {
            Log.e(LOG_TAG, "Malformed URL has occurred", urle);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "I/O exception occurred", ioe);
        }
        return articles;
    }
}
