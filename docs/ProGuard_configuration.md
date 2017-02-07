---
layout: default
---
# 使用 ProGuard 的时候避免混淆字段/Keeping field names when using ProGuard

在联合使用 ProGuard 与 Cupboard （或者任何其他的 ORM）的时候，确保 ProGuard 没有修改或者移除 entity 的字段是非常重要的。
否则，如果有字段被移除，创建的数据库表可能无法兼容查询语句，
如果有字段名被修改，查询同样可能失败，“数据库表列名重复”的错误也可能发生。

为了确保 ProGuard 不影响你的 model 定义,
假设你的 model 定义在 your.package.name.model 包中，使用下面的 ProGuard 配置可以保证 model 的定义不被修改：

    -keep class your.package.name.model.** {*;}

如果你的 entity 定义在其他的 package 中，你需要像上面一样，为每一个 package 都增加一个行 ProGuard 配置。

*(译者：或者开启注解，使用 @Column 为字段指定具体的列名，具体参见 [兼容已有数据库【Working_with_existing_data_structures_and_annotation_support】](./doc/Working_with_existing_data_structures_and_annotation_support.md))*