---
layout: default
---
# Cupboard for Android 非官方翻译

Cupboard 是一个适用于 Android 的持久化存储方案，简单而且容易与现有代码集成。

更准确的说， Cupboard 只是一个存取对象方案。为了保简洁，它并不会去维护对象之间的关系，所以也并不是一个真正的ORM。

## 设计理念
设计 Cupboard 是因为现有的持久化框架并不能满足实际的需求，我们真正想要的是：

1. 非侵入的：不必要继承某个特殊的Activity，model 也不必要去实现某个特殊的接口，甚至都不必要实现 DAO 模式
2. 通用的选择：在整个应用中都可以使用所定义的 model 对象，而并不局限于数据库
3. 完美适应 Android 自有的类，比如 Cursor 以及 ContentValues，这样，可以在任何时候回退到 Android 框架本身的实现

## 官网

[Cupboard 官网(目测被墙了)](https://bitbucket.org/littlerobots/cupboard)

## 官方文档的非官方翻译

1. [快速入门](#快速入门)
1. [使用数据库【Working_with_databases】](./Working_with_databases.md)
1. [使用 ContentProviders【Working_with_ContentProviders】](./Working_with_ContentProviders.md)
1. [使用 Cursor【Working_with_Cursors】](./Working_with_Cursors.md)
1. [兼容已有数据库【Working_with_existing_data_structures_and_annotation_support】](./Working_with_existing_data_structures_and_annotation_support.md)
1. [自定义 Converter【Custom_Converters】](./Custom_Converters.md)
1. [ProGuard 混淆【ProGuard_configuration】](./ProGuard_configuration.md)

[官方文档的非官方翻译与示例](https://github.com/xesam/CupboardTips)

## 快速入门

### 使用方式/Using Cupboard

引入 Cupboard 依赖，然后静态导入 cupboard():

build.gradle:

```gradle
    compile 'nl.qbusict:cupboard:(insert latest version)'
    //最新是 2.2.0 所以可以这么写： compile 'nl.qbusict:cupboard:2.2.0'
```

java 类:

```java
    import static nl.qbusict.cupboard.CupboardFactory.cupboard;
```

在代码中可以这么调用：

```java
    public long storeBook(SQLiteDatabase database, Book book) {
        return cupboard().withDatabase(database).put(book);
    }
```

上面的代码将一个 Book entity 存入数据库中，然后返回记录的 id， 就这么简单！

### Entities

Cupboard 中的 entity 就是一个POJO，Cupboard使用反射来操作字段（没有使用任何注解，因为 Android 中的注解反射实在是太低效了）。

entity 在使用之前，需要先使用 Cupboard.register() 进行注册：

```java
    public class Book {
        public Long _id;
        public String title;
        public Author author;
        public Date publishDate;
    }
```

entity 的字段名对应 SQLite 数据库中的 列名，表名根据 entity 名生成，本例中，表名就是 book。
每个 entity 都应该有一个 Long 类型的 _id 字段，采用 Long 类型是因为这样 _id 就可以取 null了。
而且，Android 原生的 Cursor 本身就期望一个 _id。
entity 的字段可以是任何基本类型，或者相应的包装类，或者java.util.Date，更或者另一个 entity 。

### Operations

你可以：

1. 使用 put 来创建或者更新一个 entity
2. 使用 get 来获取一个 entity
3. 使用 delete 来删除一个 entity
4. 使用 query 来查询 entity

当使用 withDatabase() 【见后文】的时候，也可以像使用 SQL 一样来执行 update 操作。

### Compartments

在应用中，我们通常的需要从不同的组件来访问 model，比如 ContentProvider 持有 SQLiteDatabase 的连接，然后从 Activity 或者 Service 来访问。

如果你只能在 ContentProvider 使用持久化库，而在其他组件里面只能使用原始的 ContentValues， 这不就是一个笑话吗。

Cupboard 已经预料到这一点：

```java
    SQliteDataBase db = getDatabase();

    // 将一个 entity 保存到数据库中
    cupboard().withDatabase(db).put(book);
    Cursor cursor = getCursor();

    // 从 cursor 获取第一条记录
    Book book = cupboard().withCursor(cursor).get(Book.class);

    // 遍历所有的记录
    Iterable<Book> itr = cupboard().withCursor(cursor).iterate();
    for (Book book : itr) {
      // do something with book
    }

    // 使用 content provider 来保存一条记录
    Uri bookUri = ...
    cupboard().withContext(this).put(bookUri, book);

    //将 Book  entity 传递给另一个需要 ContentValues 对象参数的方法
    ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);

    // 我们可能正在构建一组 ContentProviderOperation
    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(10);
    Book newBook = new Book();
    ...
    cupboard().withOperations(ops).put(bookUri, book).put(bookUri, newBook);
```

## Demo

参见工程 [demo app](https://github.com/xesam/Cupboard-CN)

## Q&A

### _id的生成机制
注意每个 model 里面定义的 _id，Cupboard 要求使用 Long 类型，这样就可以根据 _id 是否为 null 来判断是否需要自动分配 _id。
当然，你也可以定义为 long 或者 int 等等任意其他类型的数值，但是，由于数值的默认值都是 0，因此，Cupboard 无法判断这个值到底是默认值还是用户赋值。
从而需要用户自己来为维护 _id 的分配。比如，定义如下：

```java
    public class SimpleBook {
        public long _id;
        public String title;
    }
```

如果你使用下面的调用：

```java
    SimpleBook simpleBook = new SimpleBook();
    simpleBook.title = "title_single_" + System.currentTimeMillis();
    cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);
```

那么，不论你调用多少次，数据库里面只会得到一条数据，而且 _id = 0；所以除非你手动赋值 _id：

```java
    SimpleBook simpleBook = new SimpleBook();
    simpleBook._id = System.currentTimeMillis();
    simpleBook.title = "title_single_" + System.currentTimeMillis();
    cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);
```

所以，如果 _id 没有特别的需求，还是按照 Cupboard 默认要求。

###### 有问题请联系 [xesam](https://github.com/xesam)，或者加 QQ 群 315658668 讨论
