package com.example.movie_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public static final String API_MOVIE = "https://api.themoviedb.org/3/movie/550?api_key=3aadbfabe5fbabe9f229b5e7a05a2696";
    private RecyclerView recyclerView;
    private Adapter adapter;

    private ArrayList<Movie> movieList = new ArrayList<Movie>();

    SearchView searchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AsyncFetch asyncFetch = new AsyncFetch();
        asyncFetch.execute();

        for (Movie movie: movieList) {
            System.err.println("\n\n\n\n" + movie);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null ) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconified(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            // From all movie list creates specific list by name
            ArrayList<Movie> movieListByName = JSON.getMovieListByName(movieList, query);

            if (movieListByName.size() == 0) {
                SearchActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.search_no_results) + query, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Setup and Handover data to recyclerView
            recyclerView = (RecyclerView) findViewById(R.id.movie_list);
            adapter = new Adapter(SearchActivity.this, movieListByName);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        }
    }


    private class AsyncFetch extends AsyncTask<String, String, JSONObject> {
        ProgressDialog pdLoading = new ProgressDialog(SearchActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage(getResources().getString(R.string.search_loading_data));
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                JSONObject jsonObject = JSON.readJsonFromUrl(API_MOVIE);
                //JSONArray jsonArray = null;

                //jsonArray = JSON.getJSONArray(jsonObject);
// Pabandyti
//                jsonArray = jsonObject.toJSONArray(jsonObject.names());

                movieList = JSON.getList(jsonObject);
                //movieList = JSON.getList(jsonArray);

                //System.err.println(jsonObject.toString());
                return jsonObject;
            } catch (JSONException | IOException e1) {

// TODO: Parodyti Mokytojui krūta dalyką.
                System.err.println("StackTrace 👇");
                e1.printStackTrace();

                SearchActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(
                                SearchActivity.this,
                                getResources().getText(R.string.search_error_reading_data) + e1.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pdLoading.dismiss();
            if (movieList != null ) {
                SearchActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.search_found_entries_from_api) +  movieList.size(),  Toast.LENGTH_LONG).show();
                    }
                });

            }
//            SearchActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
//                    Toast.makeText(SearchActivity.this, json.toString(), Toast.LENGTH_LONG).show();
//                }
//            });
//
        }//onPostExecute

    }//AsyncFetch

} // SearchActivity
