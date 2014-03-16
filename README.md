orm
===

Simple orm like lib for android

For implementation see LikeOrmExample.

Main points:

- extend your db models from OrmModel
- extend Database helper from com.github.nrudenko.orm.BaseSQLiteOpenHelper
and fill method appendSchemas() with your db entities clases

- install plugin OrmGeneratorPlugin.jar which will
generate schemas for your db entities

......
