package dev.xesam.android.cupboardtips.model;

/**
 * Created by xesamguo@gmail.com on 12/8/15.
 */
public class SimpleBook {
    public Long _id;
    public String title;

    @Override
    public String toString() {
        return "SimpleBook{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                '}';
    }
}
