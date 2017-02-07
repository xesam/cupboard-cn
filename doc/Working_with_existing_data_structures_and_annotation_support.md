# Working with existing data
Cupboard 不仅可以存取你自己的数据，还可以与已有的数据进行集成。

许多 Android 框架会返回一个 Cursor 对象，你可以使用 cupboard().withCursor() 来进行衔接。也可以使用 cupboard().withDatabase() 来对已有的数据库进行操作。

通常来说，当使用 Cursor 来处理已有的数据时，更倾向于使用单独的 Cupboard 实例来进行操作，而不是使用 CupboardFactory.cupboard() 得到的全局 Cupboard 实例。

## 注解/Annotations

为了更简单的与已有的数据结合，Cupboard 提供了两个辅助注解。

    @Column 用来为实体字段提供一个列名

    @Ignore 用来忽略一个实体字段，而无需就将这个字段声明为 transient。

在与第三方库共用 model 的时候(比如使用 Gson 或者 Jackson 来序列化数据的时候)，这样会非常方便。

另外，@Index 和 @CompositeIndex 可以用来定义 schema。

## 启用注解/Enabling annotations

出于性能的考虑，特别是在低版本的 Android 系统上，运行时注解反射会相当慢，所以，注解默认是关闭的。

启用注解，你可以使用 CupboardBuilder 来构建一个 Cupboard 实例：

```java
    Cupboard cupboard = new CupboardBuilder().useAnnotations().build();
```

为了启用全局的注解，使用 CupboardFactory 来构建一个全局的 Cupboard 实例即可。

```java
    CupboardFactory.setCupboard(new CupboardBuilder().useAnnotations().build());
```

## 只在创建 schema 的时候启用注解/Enabling annotations for schema creation only

使用 @Column 的例子参见： [the sample](https://bitbucket.org/qbusict/cupboard/src/e7548ad8e9f8933240a6617facca3d6ecb7840e1/sample/src/main/java/nl/qbusict/cupboard/example/ContactsActivity.java?at=default)

如果只是为了使用 @Index 或者 @CompositeIndex 而启用全局的 cupboard() 实例注解，就有点得不偿失了。
这个情况下，可以使用 CupboardBuilder 来创建一个专门用于创建 table 的 Cupboard 实例:

```java
    SQLiteDatabase db = ...
    Cupboard annotatedCupboard = new CupboardBuilder(cupboard()).useAnnotations().build();
    annotatedCupboard.withDatabase(db).createTables();
```
