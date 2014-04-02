package com.github.nrudenko.orm;

import android.content.Context;
import android.support.v4.content.CursorLoader;

public class QueryCursorLoader extends CursorLoader {

    public QueryCursorLoader(Context context, QueryBuilder builder) {
        super(context, builder.getUri(), builder.getProjection(), builder.getWhere(), builder.getWhereArgs(), builder.getOrderBy());
    }
}
