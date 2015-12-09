package dev.xesam.android.cupboardtips;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

import java.util.List;

import butterknife.OnClick;
import dev.xesam.android.cupboardtips.model.Author;
import dev.xesam.android.logtools.L;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 12/8/15.
 */
public class WorkingWithContentProvidersSample extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.working_with_databases;
    }

    @OnClick(R.id.store_single_object)
    public void store_single_object() {
        Author author = new Author();
        author.name = "name_single_" + System.currentTimeMillis();

        cupboard().withContext(getContext()).put(CupContentProvider.AUTHORS_URI, author);

        get_all_objects();
    }

    @OnClick(R.id.store_multi_objects)
    public void store_multi_objects() {
        for (int i = 0; i < 2; i++) {
            Author author = new Author();
            author.name = "name_multi_" + System.currentTimeMillis();
            cupboard().withContext(getContext()).put(CupContentProvider.AUTHORS_URI, author);
        }

        get_all_objects();
    }

    @OnClick(R.id.get_single_object)
    public void get_single_object() {
        L.e(this, "################# get_single_object #################");

        Uri uri = ContentUris.withAppendedId(CupContentProvider.AUTHORS_URI, 1);

        Author author = cupboard().withContext(getContext()).query(uri, Author.class).get();

        L.e(this, author);
    }

    @OnClick(R.id.get_all_objects)
    public void get_all_objects() {
        L.e(this, "################# get_all_objects #################");
//        Cursor cursor = getContext().getContentResolver().query(CupContentProvider.AUTHORS_URI, null, null, null, null);
        List<Author> items = cupboard().withContext(getContext()).query(CupContentProvider.AUTHORS_URI, Author.class).list();
        for (Author item : items) {
            L.e(this, item);
        }
    }

    @OnClick(R.id.update_single_object)
    public void update_single_object() {
        Author author = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Author.class).get();

        if (author != null) {

            ContentValues values = new ContentValues(1);
            values.put("name", "update_single_object");

            Uri uri = ContentUris.withAppendedId(CupContentProvider.AUTHORS_URI, author._id);
            int updateCount = cupboard().withContext(getContext()).update(uri, values);
            L.e(this, "update_single_object", updateCount);
        }

        get_all_objects();
    }

    @OnClick(R.id.update_all_objects)
    public void update_all_objects() {
        ContentValues values = new ContentValues(1);
        values.put("name", "update_all_objects");
        cupboard().withContext(getContext()).update(CupContentProvider.AUTHORS_URI, values);

        get_all_objects();
    }

    @OnClick(R.id.delete_single_object)
    public void delete_single_object() {
        Author author = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Author.class).get();

        if (author != null) {
            Uri uri = ContentUris.withAppendedId(CupContentProvider.AUTHORS_URI, author._id);
            int count = cupboard().withContext(getContext()).delete(uri, "_id=?", author._id + "");
//            或者
//            int count = cupboard().withContext(getContext()).delete(CupContentProvider.AUTHORS_URI, author);
            L.e(this, "delete_single_object", count);
        }

        get_all_objects();
    }

    @OnClick(R.id.delete_all_object)
    public void delete_all_object() {
        cupboard().withContext(getContext()).delete(CupContentProvider.AUTHORS_URI, null, new String[]{});

        get_all_objects();
    }
}
