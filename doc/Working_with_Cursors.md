#Working with Cursors

Android 的 API 通常需要一个 Cursor 来处理列表结果。
当 [working with databases](./Working_with_databases.md) 或者 [working with content providers](./Working_with_ContentProviders.md) 的时候，
Cupboard允许你将查询结果转换为一个 Cursor。
如果你需要将一个 cursor 转换为 entity 或者 entity 迭代器，你可以使用 withCursor()。

#Getting the first result from a cursor

    Cursor cursor = ... // should not be null!
    // 获取第一个 Book 结果，注意，此时并未关闭 cursor
    Book book = cupboard().withCursor(cursor).get(Book.class);

#Iterate over the results

    Cursor cursor = ... // should not be null!
    QueryResultIterable<Book> itr = cupboard().withCursor(cursor).iterate(Book.class);
    for (Book book : itr) {
      // iterate over books
    }
    // 下面的操作会关闭 cursor
    itr.close();