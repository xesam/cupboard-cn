# Working with databases
Cupboard 的核心目标是从 SQLiteDatabase 中存取对象。
本章就描述如何使用最少的 SQL 来操作 database。
在 Android 开发中，通常使用 SQLiteOpenHelper 来创建和管理数据库连接，但不论是从 SQLiteOpenHelper 还是从其他地方（第三方）获取的 SQLiteDatabase 引用，
本章的内容都同样适用。

操作数据库从 withDatabase() 开始。

## 创建数据库/Setting up the database
在使用 Cupboard 之前，需要事先将待操作的 entity 注册到 Cupboard，推荐在 SQLiteOpenHelper 的静态初始化块中来进行注册：

```java
    import static nl.qbusict.cupboard.CupboardFactory.cupboard;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    public class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myapp.db";
        private static final int DATABASE_VERSION = 1;
    
        static {
            // 注册 model
            cupboard().register(Book.class);
            cupboard().register(Author.class);
        }
    
        public CupboardSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    
        @Override
        public void onCreate(SQLiteDatabase db) {
            // 创建 table
            cupboard().withDatabase(db).createTables();
            // ...其他语句
        }
    
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 升级 tables，添加 col，创建新表
            // 注意，已经存在的 col 不会被转换
            cupboard().withDatabase(db).upgradeTables();
            // ...迁移数据
        }
    }
```

*注：不需要继承或者重写任何东西，这就是一个标准的 SQLiteOpenHelper，只不过在其中增加了几个 Cupboard 调用而已。
如果你正在使用 SQLiteOpenHelper，集成 Cupboard 就非常容易了。*

首先，静态导入 Cupboard，在静态初始块中注册 entity，然后你就可以使用下面的语句来创建或者更新数据库：

创建：

```java
    cupboard().withDatabase(db).createTables();
```

更新：

```java
    cupboard().withDatabase(db).upgradeTables();
```

即使你并没有使用 SQLiteOpenHelper，你仍然需要使用 register() 来注册 entity，同时使用 createTables() 或者 updateTables() 来建立数据库。

## 保存对象/Storing objects

你的 model 必须定义一个 Long 类型的 _id 字段。

*(译者：即使不定义也没有关系，只不过 _id 是 Android 数据库的最佳实践，所以还是加上比较好。另外， _id 不一定要 Long 类型， long也可以，不过 Long 可以取值为 null，long 类型却不行)*

要保存一个对象，只需要调用 withDatabase(db).put() 即可 ：

```java
    Book book = ...
    long id = cupboard().withDatabase(db).put(book);
```

如果 book 有 _id 属性，那么，数据库中与 _id 匹配的记录都会被替换，如果 _id 为 null，那么一个新的 book 记录就会被创建。无论是哪种情况，put()都会返回对应的记录 id。

## 获取对象/Getting objects

通过 id 获取对象也很简单：

```java
    Book book = cupboard().withDatabase(db).get(Book.class, 12L);
```
上面的语句返回 id 为 12 的记录，没有则返回 null。

如果要查询 book 集合，使用 query()

```java
    // 获取第一条结果
    Book book = cupboard().withDatabase(db).query(Book.class).get();
    // 获取查询游标
    Cursor books = cupboard().withDatabase(db).query(Book.class).getCursor();
    try {
      // Iterate books
      QueryResultIterable<Book> itr = cupboard().withDatabase(db).query(Book.class).query().iterator();
      /**
      * 译者：原始文档有误，原始文档是 QueryResultIterable<Book> itr = cupboard().withDatabase(db).query(Book.class).iterate(); 调用错误
        应该是原作者笔误，或者没有更新文档。
      */
      
      for (Book book : itr) {
        // do something with book
      }
    } finally {
      // close the cursor
      itr.close();
    }
    // Get the first matching book with title Android
    Book book = cupboard().withDatabase(db).query(Book.class).withSelection("title = ?", "Android").get();
```

query() 的返回值可以是单个 entity，也可以是 entity 的迭代器，或者是一个 Cursor。

## 更新对象/Updating objects
使用 put() 可以整体替换或者更新一个 entity。但是如果你只是想部分更新一个 entity，或者一次性更新多个 entity，你可以使用 update()。
通常，这在 ContentProvider 中获取 ContentValues 的时候用得比较多。

*(译者：put 一次只能更新单个记录， update 可以更新很多)*

```java
    ContentValues values = new ContentValues(1);
    values.put("title", "Android")
    // update all books where the title is 'android'
    cupboard().withDatabase(db).update(Book.class, values, "title = ?", "android");
```

## 删除对象/Deleting objects

删除操作与 put() 和 get() 一样简单。

```java
    // by id
    cupboard().withDatabase(db).delete(Book.class, 12L);
    // by passing in the entity
    cupboard().withDatabase(db).delete(book);
    // or by selection
    cupboard().withDatabase(db).delete(Book.class, "title = ?", "android");
    // delete all entries in a given table
    cupboard().withDatabase(db).delete(Book.class, null);
```

## Tips and tricks

### Entity转ContentValues/Entity to ContentValues

如果你需要直接操作 SQLiteDatabase，你可以将任意已注册的 entity 转换为 ContentValues:

```java
    ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);
    // you can also reuse ContentValues
    values = cupboard().withEntity(Book.class).toContentValues(book, values);
```

### Cursor转Entity/Cursor to entity or entities

参阅 [working with Cursors](./Working_with_Cursors.md)

### Getting the entity table

如果你想要获取某个 entity 对应的 SQL 表名，不要直接去通过类名判断，而应该使用 withEntity() 或者 getTable()。
这样的话，即使 entity 到 table 的映射关系发生了变化，你的代码也无需做出更改。

```java
    String table = cupboard().withEntity(Book.class).getTable();
    // shortcut
    String table = cupboard().getTable(Book.class);
```

### 频繁操作/Multiple operations

如果你需要对数据库进行多次操作，不想每次都要从 withDatabase() 调用开始，你可以简单的将 DatabaseCompartment 引用保存到一个变量，然后直接使用即可：
```java
    public void doDatabaseWork(SQLiteDatabase database, Book book) {
        DatabaseCompartment dbc = cupboard().withDatabase(database);
        dbc.put(book);
        dbc.update(Book.class, "title = ?", "android");
    }
```


























