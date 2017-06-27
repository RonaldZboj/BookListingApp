package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by RonaldZboj on 2017-06-26.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    //Query URL
    private String mUrl;

    //Constructor for BookLoader
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {

        //No Url, return early
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse and extract a list of books
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
