package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Leon on 8/21/2015.
 */
public class GeneralHelper {

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
