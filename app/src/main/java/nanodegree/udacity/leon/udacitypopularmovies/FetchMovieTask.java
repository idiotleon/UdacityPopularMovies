package nanodegree.udacity.leon.udacitypopularmovies;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieSimple>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    @Override
    protected ArrayList<MovieSimple> doInBackground(String... params) {

        if (params.length == 0) return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;



        return null;
    }
}
