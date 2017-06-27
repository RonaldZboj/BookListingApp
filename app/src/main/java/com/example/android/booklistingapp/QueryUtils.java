package com.example.android.booklistingapp;

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
import java.util.List;

/**
 * Helper methods related to requesting and receiving books data from Google Books.
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Title KEY for parsing JSON
    private static final String KEY_TITLE = "title";

    //Authors KEY for parsing JSON
    private static final String KEY_AUTHORS = "authors";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of books
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of books
        return books;
    }

    /**
     * Create a URL with the query which user enter in search bar
     */
    private static URL createUrl(String query) {
        URL url = null;
        try {
            //Put the search query in Google Api url and create URL
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=15");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the URL wit user query
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        //Create a List for the Book Objects
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject jsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "items",
            JSONArray bookArray = jsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an Book object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i
                JSONObject currentBook = bookArray.getJSONObject(i);

                // Get volumeInfo key to extract title and author
                JSONObject volume = currentBook.getJSONObject("volumeInfo");


                // Extract the value for the key called "title" and "author"
                // but first according to review 1 I check if JSONObject has the required field (title and authors)
                String title;
                if (volume.has(KEY_TITLE)) {
                    title = volume.getString(KEY_TITLE);
                } else {
                    title = "No title";
                }

                String author;
                if (volume.has(KEY_AUTHORS)) {
                    author = volume.getJSONArray(KEY_AUTHORS).get(0).toString();
                } else {
                    author = "Unknown author";
                }

                // Create a new Book object with the title and author
                Book book = new Book(title, author);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of book
        return books;
    }

}
