package dev.xesam.android.cupboardtips.model;

import java.util.List;

/**
 * Created by xesamguo@gmail.com on 11/20/15.
 */
public class Book {
    public static class ExtraInfo {
        public String info;

        @Override
        public String toString() {
            return "ExtraInfo{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    public Long _id;
    public String title;
    public Author author;
    public List<String> catalogs;
    public ExtraInfo info;

    @Override
    public String toString() {
        return "Book{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", author=" + author +
                ", catalogs.size=" + catalogs.size() +
                ", info=" + info +
                '}';
    }
}
