package nanodegree.udacity.leon.udacitypopularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MovieInfoProvider extends ContentProvider {

    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static final int MOVIE_TRAILERS = 200;
    private static final int MOVIE_TRAILER_ID = 201;
    private static final int MOVIE_REVIEWS = 300;
    private static final int MOVIE_REVIEW_ID = 301;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movies", MOVIES);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movies/#", MOVIE_ID);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movie_traliers", MOVIE_TRAILERS);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movie_traliers/#", MOVIE_TRAILER_ID);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movie_reviews", MOVIE_REVIEWS);
        uriMatcher.addURI(MovieInfoProviderContract.CONTENT_AUTHORITY, "movie_reviews/#", MOVIE_REVIEW_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        openDatabase();

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case MOVIE_ID:
                String rowIdForGeneralMovieInfo = uri.getPathSegments().get(1);
                queryBuilder.setTables(MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME);
                queryBuilder.appendWhere(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + "=" + rowIdForGeneralMovieInfo);
                break;
            case MOVIE_TRAILER_ID:
                String rowIdForMovieTrailer = uri.getPathSegments().get(1);
                queryBuilder.setTables(MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME);
                queryBuilder.appendWhere(MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_ID + " = " + rowIdForMovieTrailer);
                break;
            case MOVIE_REVIEW_ID:
                String rowIdForMovieReview = uri.getPathSegments().get(1);
                queryBuilder.setTables(MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME);
                queryBuilder.appendWhere(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_ID + " = " + rowIdForMovieReview);
        }

        // If no sortOrder is specified, sort by movie popularity
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULAIRY;
        } else {
            orderBy = sortOrder;
        }

        // Apply the query to the underlying database
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, groupBy, having, orderBy);

        // Register the contexts ContentResolver to be notified if the cursor result set changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        try {
            // Return a cursor to the query result
            return cursor;
        } finally {
            database.close();
            cursor.close();
        }

    }

    @Override
    public String getType(Uri uri) {
        /**
         * Return a String that identifies the MIME type
         * for a Content Provider URI
         */
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return "vnd.android.cursor.dir/vnd.udacity.nanodegree.movie";
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.udacity.nanodegree.movie";
            case MOVIE_TRAILERS:
                return "vnd.android.cursor.dir/vnd.udacity.nanodegree.movie_trailer";
            case MOVIE_TRAILER_ID:
                return "vnd.android.cursor.item/vnd.udacity.nanodegree.movie_trailer";
            case MOVIE_REVIEWS:
                return "vnd.android.cursor.dir/vnd.udacity.nanodegree.movie_review";
            case MOVIE_REVIEW_ID:
                return "vnd.android.cursor.item/vnd.udacity.nanodegree.movie_review";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        openDatabase();

        /* To add empty rows to your database by passing in an empty
        *   Content Values object you must use the null column hack
        *   parameter to specify the name of the column that can be set to null
        */
        String nullColumnHack = null;

        // Insert the values into the table
        long id;
        Uri insertedId;
        switch (uriMatcher.match(uri)) {
            case MOVIE_ID:
                id = database.insert(MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME, nullColumnHack, values);
                if (id > -1) {
                    // Construct and return the URI of the newly inserted row
                    insertedId = ContentUris.withAppendedId(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, id);
                    // Notify any observers of the change in the data set
                    getContext().getContentResolver().notifyChange(insertedId, null);
                    return insertedId;
                }
            case MOVIE_TRAILER_ID:
                id = database.insert(MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME, nullColumnHack, values);
                if (id > -1) {
                    insertedId = ContentUris.withAppendedId(MovieInfoProviderContract.MovieTrailerEntry.CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(insertedId, null);
                    return insertedId;
                }
            case MOVIE_REVIEW_ID:
                id = database.insert(MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME, nullColumnHack, values);
                if (id > -1) {
                    insertedId = ContentUris.withAppendedId(MovieInfoProviderContract.MovieReviewEntry.CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(insertedId, null);
                    return insertedId;
                }
        }

        database.close();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        openDatabase();

        String rowId;
        int deleteCount = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " = " + rowId +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1"
                if (selection == null) selection = "1";
                // Perform the deletion
                deleteCount = database.delete(MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME, selection, selectionArgs);
                // Notify any observers of the change in the data set
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case MOVIE_TRAILER_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " = " + rowId +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                if (selection == null) selection = "1";
                deleteCount = database.delete(MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case MOVIE_REVIEW_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + " = " + rowId +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                if (selection == null) selection = "1";
                deleteCount = database.delete(MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                break;
        }

        database.close();
        // Return the number of deleted items
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        openDatabase();

        // If this is a row URI, limit the deletion to the specified row
        String rowId;
        int updateCount = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " = " + rowId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                // Perform the update
                updateCount = database.update(MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_TRAILER_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " = " + rowId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                updateCount = database.update(MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_REVIEW_ID:
                rowId = uri.getPathSegments().get(1);
                selection = MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + " = " + rowId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                updateCount = database.update(MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }

        // Notify any observers of the change in the data set
        getContext().getContentResolver().notifyChange(uri, null);

        database.close();
        return updateCount;
    }

    public void openDatabase() throws SQLiteException {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            database = dbHelper.getReadableDatabase();
        }
    }
}
