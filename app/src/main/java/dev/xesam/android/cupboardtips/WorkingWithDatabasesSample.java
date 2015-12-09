package dev.xesam.android.cupboardtips;

import android.content.ContentValues;

import java.util.List;

import butterknife.OnClick;
import dev.xesam.android.cupboardtips.model.SimpleBook;
import dev.xesam.android.logtools.L;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 11/20/15.
 */
public class WorkingWithDatabasesSample extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.working_with_databases;
    }

    @OnClick(R.id.store_single_object)
    public void store_single_object() {
        SimpleBook simpleBook = new SimpleBook();
        simpleBook.title = "title_single_" + System.currentTimeMillis();
        cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);

        get_all_objects();
    }

    @OnClick(R.id.store_multi_objects)
    public void store_multi_objects() {
        for (int i = 0; i < 2; i++) {
            SimpleBook simpleBook = new SimpleBook();
            simpleBook.title = "title_multi_" + System.currentTimeMillis();
            cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);
        }

        get_all_objects();
    }

    @OnClick(R.id.get_single_object)
    public void get_single_object() {
        L.e(this, "################# get_single_object #################");
        SimpleBook simpleBook = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(SimpleBook.class).get();

        L.e(this, simpleBook);
    }

    @OnClick(R.id.get_all_objects)
    public void get_all_objects() {
        L.e(this, "################# get_all_objects #################");
        List<SimpleBook> items = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).query(SimpleBook.class).list();
        for (SimpleBook item : items) {
            L.e(this, item);
        }
    }

    @OnClick(R.id.update_single_object)
    public void update_single_object() {
        SimpleBook simpleBook = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(SimpleBook.class).get();

        if (simpleBook != null) {
            ContentValues values = new ContentValues(1);
            values.put("title", "update_single_object");
            int updateCount = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                    .update(SimpleBook.class, values, "title=?", simpleBook.title);
            L.e(this, "update_single_object", updateCount);
        }

        get_all_objects();
    }

    @OnClick(R.id.update_all_objects)
    public void update_all_objects() {
        ContentValues values = new ContentValues(1);
        values.put("title", "update_all_objects");
        cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).update(SimpleBook.class, values);

        get_all_objects();
    }

    @OnClick(R.id.delete_single_object)
    public void delete_single_object() {
        SimpleBook simpleBook = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(SimpleBook.class).get();

        if (simpleBook != null) {
            boolean ret = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                    .delete(SimpleBook.class, simpleBook._id);
            L.e(this, "delete_single_object", ret);
        }

        get_all_objects();
    }

    @OnClick(R.id.delete_all_object)
    public void delete_all_object() {

        cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .delete(SimpleBook.class, null);

        get_all_objects();
    }
}
