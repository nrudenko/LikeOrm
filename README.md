LikeOrm
=======

Simple orm like lib for android

For implementation see LikeOrmExample.

Main points:

- "database" model should be annotated by @Table
- extend Database helper from com.github.nrudenko.orm.LikeOrmDatabaseHelper
and fill method appendSchemas() with your db entities clases

- install plugin LikeOrm schemes plugin from jetbrains plugin repos

......
