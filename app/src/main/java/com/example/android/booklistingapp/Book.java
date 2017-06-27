package com.example.android.booklistingapp;

/**
 * {@Book} represents a book object - search results from google API
 */

public class Book {

    // String for book title
    String mTitle;

    // String for book author
    String mAuthor;

    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
