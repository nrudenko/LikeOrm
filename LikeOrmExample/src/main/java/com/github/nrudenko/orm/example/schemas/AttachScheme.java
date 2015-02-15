package com.github.nrudenko.orm.example.schemas;

import com.github.nrudenko.orm.commons.BaseScheme;
import com.github.nrudenko.orm.commons.DbType;
import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.example.model.Attach;


/* AUTO-GENERATED FILE. CAREFUL TO MODIFY.
 *
 * This class was automatically generated by the
 * OrmGeneratorPlugin any changes will be overwritten
 * after next schema auto-generation
 */

public final class AttachScheme implements BaseScheme{

    public static final Column _ID = new Column("Attach._id",  DbType.INT); // PRIMARY AUTOINCREMENT
    public static final Column URL = new Column("Attach.url",  DbType.TEXT);
    public static final Column MESSAGE_ID = new Column("Attach.messageId",  DbType.TEXT); // UNIQUE ON CONFLICT REPLACE

    public static Class MODEL_CLASS = Attach.class;
    public static String TABLE_NAME = "attach";
    public static Column[] PROJECTION = new Column[]{_ID,URL,MESSAGE_ID};
    public static String CUSTOM_SQL = "";
}