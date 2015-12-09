package dev.xesam.android.cupboardtips.model;

/**
 * Created by xesamguo@gmail.com on 11/20/15.
 */
public class Author {
    public Long _id;
    public String name;

    @Override
    public String toString() {
        return "Author{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                '}';
    }
}
