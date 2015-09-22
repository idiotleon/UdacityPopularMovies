package nanodegree.udacity.leon.udacitypopularmovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieInfoProviderContract {
    /**
     * The authority of the movie info provider
     */
    public static final String CONTENT_AUTHORITY
            = "nanodegree.udacity.leon.udacitypopularmovies.provider.MovieInfoProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

/*    public static final String PATH_GENERAL_MOVIE_INFO = "general_movie_info";
    public static final String PATH_MOVIE_TRAILER = "movie_trailer";
    public static final String PATH_MOVIE_REVIEW = "movie_review";*/

    public static final class GeneralMovieInfoEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "general_movie_info";

        public static final String MOVIE_COLUMN_ID = "movie_id";
        public static final String MOVIE_COLUMN_ORIGINAL_TITLE = "movie_original_title";
        public static final String MOVIE_COLUMN_IMAGE_URL = "movie_image_url";
        public static final String MOVIE_COLUMN_PLOT_SYNOPSIS = "movie_plot_synopsis";
        public static final String MOVIE_COLUMN_USER_RATING = "movie_rating";
        public static final String MOVIE_COLUMN_RELEASE_DATE = "movie_date";
        public static final String MOVIE_COLUMN_POPULARITY = "movie_popularity";
        public static final String MOVIE_COLUMN_FAVORITE_STATUS = "movie_favorite_status";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }

    public static final class MovieTrailerEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "movie_trailer";

        /**
         * Fields, together with Movie_ID, to create Movie Trailer table
         */
        public static final String MOVIE_TRAILER_COLUMN_ID = "movie_trailer_url_id";
        public static final String MOVIE_TRAILER_COLUMN_URL = "movie_trailer_url";
        public static final String MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID = "movie_trailer_url_foreign_key_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }

    public static final class MovieReviewEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "movie_review";

        /**
         * Fields, together with Movie_ID, to create Movie Review table
         */
        public static final String MOVIE_REVIEW_COLUMN_ID = "movie_review_id";
        public static final String MOVIE_REVIEW_COLUMN_AUTHOR = "movie_review_author";
        public static final String MOVIE_REVIEW_COLUMN_CONTENT = "movie_review_content";
        public static final String MOVIE_REVIEW_COLUMN_URL = "movie_review_url";
        public static final String MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID = "movie_review_id_foreign_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }
}
