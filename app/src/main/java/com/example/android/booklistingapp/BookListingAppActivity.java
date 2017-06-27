package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklistingapp.R.id.search;


public class BookListingAppActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    // BookAdapter - list of books
    private BookAdapter mAdapter;

    // Query given by user
    private String mQuery;

    // Text displayed when the list is empty
    private TextView emptyTextView;

    // SearchView - takes the query
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_listing_app_activity);

        // Find a reference to the searchView in the layout
        searchView = (SearchView) findViewById(search);

        // Find a reference to the ListView in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find the reference to the empty text view in a layout and set empty view
        emptyTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the ListView
        bookListView.setAdapter(mAdapter);

        // Check if device is connected to the internet
        if (isConnected()) {
            // Initialize
            getLoaderManager().initLoader(0, null, this);
        } else {
            // If no internet connection
            emptyTextView.setText(R.string.no_internet_connection);
        }

        //Set an OnQueryTextListener to the searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnected()) {
                    //Restart the loader, execute query
                    getLoaderManager().restartLoader(0, null, BookListingAppActivity.this);
                    return true;
                } else {
                    // If no internet connection
                    emptyTextView.setText(R.string.no_internet_connection);
                    // if there is any data in the Adapter, say if we performed a search, lost
                    // connectivity, and tried to search again, then we need to clear out old data
                    // so we can see the empty view
                    mAdapter.clear();
                    return false;
                }
            }
        });

    }

    //We need this method outside onCreate, because we check internet connection when the search button is actually pressed
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        mQuery = searchView.getQuery().toString();
        BookLoader loader = new BookLoader(this, mQuery);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        } else {
            emptyTextView.setText(R.string.no_books);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mQuery = searchView.getQuery().toString();
        outState.putString("query", mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mQuery = savedInstanceState.getString("query");
        super.onRestoreInstanceState(savedInstanceState);
    }

}

