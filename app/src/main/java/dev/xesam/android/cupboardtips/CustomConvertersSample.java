package dev.xesam.android.cupboardtips;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import dev.xesam.android.cupboardtips.model.Author;
import dev.xesam.android.cupboardtips.model.Book;
import dev.xesam.android.logtools.L;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 12/8/15.
 */
public class CustomConvertersSample extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.working_with_databases;
    }

    @OnClick(R.id.store_single_object)
    public void store_single_object() {
        Book book = new Book();
        Author author = new Author();
        author.name = "author_a";

        Book.ExtraInfo info = new Book.ExtraInfo();
        info.info = "info_a";

        List<String> catalogs = new ArrayList<>();
        catalogs.add("catalog_a");
        catalogs.add("catalog_b");
        catalogs.add("catalog_c");

        book.title = "title_single_" + System.currentTimeMillis();
        book.author = author;
        book.info = info;
        book.catalogs = catalogs;

        cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(book);

        get_all_objects();
    }

    @OnClick(R.id.store_multi_objects)
    public void store_multi_objects() {
        for (int i = 0; i < 2; i++) {
            Book book = new Book();
            Author author = new Author();
            author.name = "author_a";

            Book.ExtraInfo info = new Book.ExtraInfo();
            info.info = "info_a";

            List<String> catalogs = new ArrayList<>();
            catalogs.add("catalog_a");
            catalogs.add("catalog_b");

            book.title = "title_multi_" + System.currentTimeMillis();
            book.author = author;
            book.info = info;
            book.catalogs = catalogs;

            cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(book);
        }

        get_all_objects();
    }

    @OnClick(R.id.get_single_object)
    public void get_single_object() {
        L.e(this, "################# get_single_object #################");
        Book book = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Book.class).get();

        L.e(this, book);
    }

    @OnClick(R.id.get_all_objects)
    public void get_all_objects() {
        L.e(this, "################# get_all_objects #################");
        List<Book> items = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Book.class).list();
        for (Book item : items) {
            L.e(this, item);
        }
    }

    @OnClick(R.id.update_single_object)
    public void update_single_object() {
        Book book = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Book.class).get();

        if (book != null) {
            //对于复杂对象，用 ContentValues 需要对 model 转换细节了解过多，所以不要直接使用 ContentValues
            book.title = "update_single_object";
            ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);
            int updateCount = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                    .update(Book.class, values);
            L.e(this, "update_single_object", updateCount);
        }

        get_all_objects();
    }

    @OnClick(R.id.update_all_objects)
    public void update_all_objects() {

        Book book = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Book.class).get();

        if (book != null) {
            book.title = "update_all_objects";
            ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);
            values.remove(BaseColumns._ID);

            cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                    .update(Book.class, values);
        }

        get_all_objects();
    }

    @OnClick(R.id.delete_single_object)
    public void delete_single_object() {
        Book book = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .query(Book.class).get();

        if (book != null) {
            boolean ret = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                    .delete(Book.class, book._id);
            L.e(this, "delete_single_object", ret);
        }

        get_all_objects();
    }

    @OnClick(R.id.delete_all_object)
    public void delete_all_object() {
        cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase())
                .delete(Book.class, null);

        get_all_objects();
    }
}
