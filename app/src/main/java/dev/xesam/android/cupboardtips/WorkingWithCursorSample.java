package dev.xesam.android.cupboardtips;

import android.database.Cursor;

import butterknife.OnClick;
import dev.xesam.android.cupboardtips.model.SimpleBook;
import dev.xesam.android.logtools.L;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by xesamguo@gmail.com on 12/8/15.
 */
public class WorkingWithCursorSample extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.working_with_cursor;
    }

    @OnClick(R.id.cursor)
    public void cursor() {
        Cursor cursor = cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).query(SimpleBook.class).getCursor();

        SimpleBook simpleBook = cupboard().withCursor(cursor).get(SimpleBook.class);
        L.e(this, "first", simpleBook);

        QueryResultIterable<SimpleBook> itr = cupboard().withCursor(cursor).iterate(SimpleBook.class);
        for (SimpleBook item : itr) {
            L.e(this, item);
        }
        itr.close();
    }
}
