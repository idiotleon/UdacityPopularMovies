package nanodegree.udacity.leon.udacitypopularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Leon on 8/5/2015.
 */
public class FavoriteStatus {

    public static void markAsFavorite(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, 1).commit();
    }

    public static void cancelFavoriteStatus(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, 2).commit();
    }

    public static int getFavoriteStatus(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }
}
