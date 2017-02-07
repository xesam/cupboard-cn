# CustomConverters
通常来说，保持对象模型的简单总是一个好的策略：不要使用复杂类型，也不要使用反模式的复杂关系。这样便于存取。
但有时候，却不得不使用复杂对象，或者复用现有的对象模型。比如，这个模型被一个API 使用。
当遇到这种情况时，Cupboard 的默认策略就会导致运行错误，从而导致 entity 无法被保存。

幸运的是， Cupboard提供了一个插件式的方法来配置如何从 Cursor 中获取 entity，以及怎样将一个 entity 转换为 ContentValues。

## FieldConverters
一个 entity 包含多个属性（或者叫字段），最终，任何类型的属性都需要转换成数据库可以接收的类型才能保存到数据库中。
比如，java.lang.String 默认会转换为 SQLite TEXT， java.util.Date 默认转换额为 SQLite NUMBER。

FieldConverter 允许配置某个属性在这两者之间的转换方式，Cupboard 提供了一种机制允许你为不同的字段配置不同的 FieldConverter，
以及针对多个类型或者复杂对象（泛型）注册单个（多个）FieldConverterFactory。只要你能找到合适的表示形式，就可以使用 FieldConverter 将任意类型映射到数据库的对应列中。

SQLite 自身定义了 TEXT， NUMBER， REAL 以及 BLOB 集中列类型。Cupboard 补充了一种类型 JOIN。
JOIN 列表明此列的值并不是真的被存储到数据库中，而是通过某种方式计算得来的，典型的就是 SQL JOIN 查询（名字来源）。
通过将一列的 FieldConverter 返回类型指定为 JOIN， 就可能定义一种“计算”或者“只读”的字段类型。

通过将一列的 FieldConverter 返回类型指定为 null， Cupboard 也可以忽略这些特定的类型。
默认的 ReflectiveEntityConverter 会忽略 transient， final 和 static 属性。另外，如果注解被启用，任何被 @Ignore 标记的字段也会被忽略。

配置 Converter 需要通过 CupboardBuilder 的方式来创建 Cupboard 实例， 然后将 FieldConverter 以及 FieldConverterFactory 注册到 Cupboard 的实例上。
如果你想让 cupboard() 直接返回扩展后的 Cupboard 实例，一定不要忘记调用 CupboardFactory.setCupboard() 将默认的实现替换为这个新实例。

## EntityConverters
签名说到， entity 包含许多字段。但是一个 entity 同样会映射到一个数据库表.
EntityConverter 用来配置怎样从 Cursor 中获取 entity，以及怎样将 entity 转换为 ContentValues。.
注意，EntityConverter 作用于 entity 的所有字段，而 FieldConverter 只处理一种字段类型
典型来说，EntityConverter 通过 Cupboard 来查询相应的 FieldConverter，然后委托 FieldConverter 来处理具体的事务并生成 ContentValues。
EntityConverter 通过指定列名，类型以及表名，决定了怎样将 entity 映射到 数据库中。

Cupboard 提供了默认的 ReflectiveEntityConverter，你可以通过扩展这个类来定义自己的映射规则，也可以为所有的 entity 类型替换掉默认的 EntityConverter
不过，不论你用哪种方式，都需要先使用 CupboardBuilder 来注册 EntityConverter。

## Summary

如果 Cupboard 的默认配置无法（或者不应该）持久化某个类型的字段，你就需要一个自定义的 FieldConverter，或者可选的 FieldConverterFactory

如果你需要对某个 entity 进行特别的处理，你可能就需要注册一个 EntityConverterFactory，通过扩展 ReflectiveEntityConverter 的途径来进行某些适配处理。

如果你希望改变 entity 在数据库里面的存储形式，比如，想要所有的字段都使用 json 文件的形式保存到数据库中，你就需要新注册一个 EntityConverterFactory 来处理所有的 entity

## 示例/Example

[看这里](https://gist.github.com/hvisser/7c10d433bbf01306f158)演示了怎样使用 FieldConverter 和 EntityConverter 将一个属性以 json 字符串的形式存取。