#Cupboard Tips

[Cupboard 官方托管](https://bitbucket.org/littlerobots/cupboard)

##Cupboard for Android

Cupboard 是针对 Android 的一个简单的持久化存储方案，简单而且容易与现有代码集成。

我们更倾向于认为 Cupboard 是一个存取对象的方式。它并不是一个正真的ORM，因为为了保持简单，它并不会去维护对象之间的关系。

###设计理念
我们设计 Cupboard 是因为现有的持久化框架并不能满足我们的需求。

1. 我们想要一个非侵入的：不必要继承某个特殊的Activity，你的 model 也不必要无实现某个特殊的接口，如果不愿意，都不必要实现 DAO 模式
2. 我们需要一个通用的选择：在整个应用中都可以使用所定义的 model 对象，而并不局限于数据库
3. 我们需要他完美适应 Android 自有的类，比如 Cursor 以及 ContentValues，这样，可以在任何时候回退到 Android 框架里面。

###Using Cupboard

引入 Cupboard 依赖，然后静态导入 cupboard():

build.gradle:

    compile 'nl.qbusict:cupboard:(insert latest version)'
    最新是 2.1.4 所以可以这么写： compile 'nl.qbusict:cupboard:2.1.4'
    
    
java 类:

    import static nl.qbusict.cupboard.CupboardFactory.cupboard;

在代码中可以这么调用：
    
    public long storeBook(SQLiteDatabase database, Book book) {
        return cupboard().withDatabase(database).put(book);
    }

上面的代码将一个 Book 实体存入数据库中，然后返回记录的 is， 就这么简单。

###Entities

Cupboard 中的实体就是一个POJO，Cupboard使用反射来操作随想的字段，没有使用注解，因为 Android 中的注解反射实在是太低效了。

实体在使用之前，需要先使用 Cupboard.register() 进行注册。一个示例：

    public class Book {
        public Long _id;
        public String title;
        public Author author;
        public Date publishDate;
    }

实体的字段名对应 SQLite 数据库中的 列名，表名根据实体名得来，本例中，表明就是 book。每个实体都应该有一个 Long 类型的 _id 字段，为 Long 是因为这样 _id 就可以取 null了。
这并不奇怪，Android 原生的 Cursor 本身就期望一个 _id。
实体的一个字段可以是任何基本类型，相应的包装类，java.util.Date，或者另一个实体。

###Operations

你可以：

1. 使用 put 来创建或者更新一个实体
2. 使用 get 来获取一个实体
3. 使用 delete 来删除一个实体
4. 使用 query 来查询实体

当使用 withDatabase() 【见后文】的时候，也可以像使用 SQL 一样来执行 update 操作。

###Compartments

在许多通常的应用中，你需要从不同的组件访问 model，比如 ContentProvider 可能就持有与 SQLiteDatabase 的连接，然后你从 Activity 或者 Service 来访问。

如果你只能在 ContentProvider 使用持久化库，在其他组件里面只能使用原始的 ContentValues， 那么这不就是一个笑话吗。

Cupboard 以及预料到这一点：

    SQliteDataBase db = getDatabase();
    
    // 将一个实体保存到数据库中
    cupboard().withDatabase(db).put(book);
    Cursor cursor = getCursor();
    
    // 从 cursor 获取第一个条记录
    Book book = cupboard().withCursor(cursor).get(Book.class);
    
    // 遍历所有的记录
    Iterable<Book> itr = cupboard().withCursor(cursor).iterate();
    for (Book book : itr) {
      // do something with book
    }
    
    // 使用 content provider 来保存一条记录
    Uri bookUri = ...
    cupboard().withContext(this).put(bookUri, book);
    
    //将 Book 实体传递给另一个需要 ContentValues 对象参数的方法
    ContentValues values = cupboard().withEntity(Book.class).toContentValues(book);
    
    // 我们可能正在构建一组 ContentProviderOperation 
    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(10);
    Book newBook = new Book();
    ...
    cupboard().withOperations(ops).put(bookUri, book).put(bookUri, newBook);
    
    
##官方文档翻译

1. [Working_with_databases](./doc/Working_with_databases.md)
1. [Working_with_ContentProviders](./doc/Working_with_ContentProviders.md)
1. [Working_with_Cursors](./doc/Working_with_Cursors.md)
1. [Working_with_existing_data_structures_and_annotation_support](./doc/Working_with_existing_data_structures_and_annotation_support.md)
1. [Custom_Converters](./doc/Custom_Converters.md)
1. [ProGuard_configuration](./doc/ProGuard_configuration.md)

##Demo

参见工程 app
    
##Q&A

###_id的生成机制
注意每个 model 里面定义的 _id，Cupboard 要求使用 Long 类型，这样就可以根据 _id 是否为 null 来判断是否需要自动分配 _id。
当然，你也可以定义为 long 或者 int 等等任意其他类型的数值，但是，由于数值的默认值都是 0，因此，Cupboard 无法判断这个值到底是默认值还是用户赋值。
从而需要用户自己来为维护 _id 的分配。比如，定义如下：

    public class SimpleBook {
        public long _id;
        public String title;
    }
    
如果你使用下面的调用：

    SimpleBook simpleBook = new SimpleBook();
    simpleBook.title = "title_single_" + System.currentTimeMillis();
    cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);

那么，不论你调用多少次，数据库里面只会得到一条数据，而且 _id = 0；所以除非你手动赋值 _id：

    SimpleBook simpleBook = new SimpleBook();
    simpleBook._id = System.currentTimeMillis();
    simpleBook.title = "title_single_" + System.currentTimeMillis();
    cupboard().withDatabase(cupboardSQLiteOpenHelper.getWritableDatabase()).put(simpleBook);
    
所以，如果 _id 没有特别的需求，还是按照 Cupboard 默认要求。

###Q群：315658668

