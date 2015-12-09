package dev.xesam.android.cupboardtips;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import dev.xesam.android.cupboardtips.model.Author;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 11/10/15.
 */
public class CupContentProvider extends ContentProvider {

    protected CupboardSQLiteOpenHelper cupboardSQLiteOpenHelper;

    public static String AUTHORITY = "dev.xesam.android.cupboardtips";
    public static final Uri AUTHORS_URI = Uri.parse("content://" + AUTHORITY + "/author");

    private static UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int AUTHOR = 1;
    private static final int AUTHORS = 2;

    static {
        sMatcher.addURI(AUTHORITY, "author", AUTHORS);
        sMatcher.addURI(AUTHORITY, "author/#", AUTHOR);
    }

    @Override
    public boolean onCreate() {
        cupboardSQLiteOpenHelper = new CupboardSQLiteOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sMatcher.match(uri)) {
            case AUTHORS:
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .query(Author.class).
                                withProjection(projection).
                                withSelection(selection, selectionArgs).
                                orderBy(sortOrder).
                                getCursor();
            case AUTHOR:
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .query(Author.class).
                                byId(ContentUris.parseId(uri)).
                                getCursor();
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sMatcher.match(uri)) {
            case AUTHORS:
                long _id = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .put(Author.class, values);
                Uri retUri = ContentUris.withAppendedId(uri, _id);
                getContext().getContentResolver().notifyChange(retUri, null);
                return retUri;
            default:
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sMatcher.match(uri)) {
            case AUTHORS:
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .delete(Author.class, null);
            case AUTHOR:
                long _id = ContentUris.parseId(uri);
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .delete(Author.class, "_id=?", _id + "");
            default:
                return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sMatcher.match(uri)) {
            case AUTHORS:
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .update(Author.class, values, selection, selectionArgs);
            case AUTHOR:
                long _id = ContentUris.parseId(uri);
                return cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                        .update(Author.class, values, "_id=?", _id + "");
        }
        return 0;
    }
}
