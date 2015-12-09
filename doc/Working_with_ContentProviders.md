#Working with ContentProviders
在典型的 Android 结构中,使用 ContentProvider 来访问数据。
ContentProvider 是一种用来“提供内容”的抽象。尽管如此，最常见的应用场景还是使用 ContentProvider 来控制 SQLiteDatabase，然后通过 Uri 来提供“增删改查”的功能。
在 ContentProvider 的实现中，你可以像在 [working with databases](./Working_with_databases.md) 中描述的那样来使用 Cupboard，直接使用 Cupboard 来存取数据。

本章介绍怎样与 ContentProvider 进行操作。

为了操作 ContentProvider，你需要使用 withContext() 方法。

##注册 entity/Registering entities

与 [working with databases](./Working_with_databases.md) 描述的一样，同样需要使用 register() 来注册 entity，通常来说，你可以在 ContentProvider 来进行注册。
当然，我们推荐你在 Application 中使用静态初始化块来进行初始化注册。

##保存对象/Storing objects

如果要保存一个对象，使用 withContext(context).put() 并传入 ContentProvider 的基础 URI 即可， Cupboard 假定所有的操作都是 REST 风格。
如果你的 Books Uri 是类似 content://com.example.provider/books 的形式，
那么 Cupboard 就假定 id 等于 12 的 book entity 的 uri 是 content://com.example.provider/books/12。

下面演示了如何 put 一个对象：

    static final Uri BOOK_URI = Uri.parse("content://com.example.provider/books");
    Book book = ...
    Uri uri = cupboard().withContext(getContext()).put(BOOK_URI, book);

如果 book 设置了 _id 属性，那么 ContentResolver.insert() 就认为 content://com.example.provider/books/12 的 _id 是12
否则，ContentResolver.insert() 就在 content://com.example.provider/books 上进行操作。

##获取对象/Getting objects

为了通过 id 获取一个 entity，你需要为这个entity构建一个 uri

    // The book id is encoded in the Uri
    Uri bookUri = ContentUris.withAppendedId(BOOKS_URI, 12);
    Book book = cupboard().withContext(getContext()).get(bookUri, Book.class);
    
这将得到 _id 为 12 的 book 对象。如果没有 _id 为 12 的 book，就返回 null。 
这背后的操作机制是， ContentResolver.query() 在 uri 上的调用返回一个 cursor，那么第一个结果会作为结果 entity 返回。
然后 cursor 会被关闭。为了查询 books 使用 query()：

    // get the first book in the result
    Book book = cupboard().withContext(getContext()).query(BOOKS_URI, Book.class).get();
    // Get the cursor for this query
    Cursor books = cupboard().withContext(getContext()).query(BOOKS_URI, Book.class).getCursor();
    try {
      // Iterate books
      QueryResultIterable<Book> itr = cupboard().withContext(getContext()).query(BOOKS_URI, Book.class).query();
      for (Book book : itr) {
        // do something with book
      }
    } finally {
      // close the cursor
      itr.close();
    }
    // Get the first matching book with title Android
    Book book = cupboard().withContext(getContext()).query(BOOKS_URI, Book.class).withSelection("title = ?", "Android").get();
 
query() 的结果可以是一个 entity， 也可以是一个迭代器，或者一个 Cursor。

##更新对象/Updating objects

对比 withDatabase() ，由于我们使用 ContentResolver 来操作（需要 uri），因此没有纯粹的 update 方法。

    // This is standard Android framework code
    ContentValues values = new ContentValues(1);
    values.put("title", "Android")
    // update all books where the title is 'android'
    getContentResolver().update(BOOKS_URI, values, "title = ?", new String[] { "Android" });
    
##删除对象/Deleting objects

使用 delete 来删除一个对象。

    // by passing in the entity, will append book._id to the Uri
    cupboard().withContext(getContext()).delete(BOOKS_URI, book);

如果你需要通过条件删除，那么方式与 update 相同

##Tips and tricks

###Entity to ContentValues

如果你需要一个 entity 对应的 ContentValues 对象来操作 ContentResolver，可以使用 withEntity() 来进行这种转换。

    ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);
    // you can also reuse ContentValues
    values = cupboard().withEntity(Book.class).toContentValues(book, values);

###Cursor to entity or entities
参考 [Working with Cursors](./Working_with_Cursors.md)

###Multiple operations

如果你需要对 ContentProviders 进行多次操作，每次都调用 withContext() 肯定是令人厌烦的。
所以，你可以简单的将 ProviderCompartment 的引用保存到一个变量中，然后在后面的操作中直接引用即可：
    
    public void doDataStorageWork(Book book) {
        ProviderCompartment pc = cupboard().withContext(getContext())
        pc.put(BOOKS_URI, book);
        Book other = pc.get(ContentUris.withAppendedId(BOOKS_URI, 15), Book.class);
    }