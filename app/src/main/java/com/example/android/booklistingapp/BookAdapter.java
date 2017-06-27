package com.example.android.booklistingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RonaldZboj on 2017-06-25.
 */

public class BookAdapter extends ArrayAdapter<Book> {


    /**
     * @param context The current context.
     * @param books   A List of Book objects to display in a list
     */
    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //Get the current Book Object
        Book currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        // Display the title of the current book in that TextView
        titleTextView.setText(currentBook.getTitle());

        // Find the TextView in the list_item.xml layout with the ID author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        // Display the title of the current book in that TextView
        authorTextView.setText(currentBook.getAuthor());


        return listItemView;
    }

}
