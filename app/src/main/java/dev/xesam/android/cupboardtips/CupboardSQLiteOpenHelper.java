package dev.xesam.android.cupboardtips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dev.xesam.android.cupboardtips.model.Author;
import dev.xesam.android.cupboardtips.model.Book;
import dev.xesam.android.cupboardtips.model.SimpleBook;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.EntityConverterFactory;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 11/2/15.
 */
public class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {

    public static class ExtraInfoFieldConverter implements FieldConverter<Book.ExtraInfo> {

        @Override
        public Book.ExtraInfo fromCursorValue(Cursor cursor, int columnIndex) {
            Book.ExtraInfo value = new Book.ExtraInfo();
            value.info = cursor.getString(columnIndex);
            return value;
        }

        @Override
        public void toContentValue(Book.ExtraInfo value, String key, ContentValues values) {
            values.put(key, value.info);
        }

        @Override
        public EntityConverter.ColumnType getColumnType() {
            return EntityConverter.ColumnType.TEXT;
        }
    }

    public static class AuthorFieldConverter implements FieldConverter<Author> {

        @Override
        public Author fromCursorValue(Cursor cursor, int columnIndex) {
            Author value = new Author();
            value.name = cursor.getString(columnIndex);
            return value;
        }

        @Override
        public void toContentValue(Author value, String key, ContentValues values) {
            values.put(key, value.name);
        }

        @Override
        public EntityConverter.ColumnType getColumnType() {
            return EntityConverter.ColumnType.TEXT;
        }
    }

    public static class ListFieldConverter implements FieldConverter<List<String>> {

        public static final String SEP = ",";

        @Override
        public List<String> fromCursorValue(Cursor cursor, int columnIndex) {
            String content = cursor.getString(columnIndex);
            if (TextUtils.isEmpty(content)) {
                return new ArrayList<>();
            }
            return Arrays.asList(content.split(SEP));
        }

        @Override
        public void toContentValue(List<String> value, String key, ContentValues values) {
            if (value == null) {
                values.put(key, "");
                return;
            }
            Iterator<String> iterator = value.iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (iterator.hasNext()) {
                String item = iterator.next();
                stringBuilder.append(item);
                if (iterator.hasNext()) {
                    stringBuilder.append(SEP);
                }
            }
            values.put(key, stringBuilder.toString());
        }

        @Override
        public EntityConverter.ColumnType getColumnType() {
            return EntityConverter.ColumnType.TEXT;
        }
    }

    static {
        Cupboard cupboard = new CupboardBuilder().useAnnotations()
                .registerEntityConverterFactory(new EntityConverterFactory() {
                    @Override
                    public <T> EntityConverter<T> create(Cupboard cupboard, Class<T> type) {
                        if (type == Book.class) {
                            EntityConverter<Book> delegate = new ReflectiveEntityConverter<Book>(cupboard, Book.class) {
                                @Override
                                protected FieldConverter<?> getFieldConverter(Field field) {
                                    if ("catalogs".equals(field.getName())) {
                                        return new ListFieldConverter();
                                    }
                                    return super.getFieldConverter(field);
                                }
                            };
                            return (EntityConverter<T>) delegate;
                        }
                        return null;
                    }
                })
                .registerFieldConverterFactory(new FieldConverterFactory() {
                    @Override
                    public FieldConverter<?> create(Cupboard cupboard, Type type) {
                        if (type == Book.ExtraInfo.class) {
                            return new ExtraInfoFieldConverter();
                        } else if (type == Author.class) {
                            return new AuthorFieldConverter();
                        }
                        return null;
                    }
                }).build();

        CupboardFactory.setCupboard(cupboard);

        cupboard().register(SimpleBook.class);

        cupboard().register(Book.class);
        cupboard().register(Author.class);
    }

    private static final String DATABASE_NAME = "sample.db";
    private static final int DATABASE_VERSION = 1;

    public CupboardSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
