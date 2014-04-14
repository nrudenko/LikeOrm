package com.github.nrudenko.orm;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.CursorLoader;

import java.util.List;

public class QueryCursorLoader extends CursorLoader {

    private ContentObserver joinContentObserver;

    public QueryCursorLoader(Context context, Class<? extends LikeOrmContentProvider> providerClass, QueryBuilder builder) {
        super(context, builder.getUri(), builder.getProjection(), builder.getWhere(), builder.getWhereArgs(), builder.getOrderBy());
    }

    public QueryCursorLoader(Context context, final QueryBuilder builder) {
        this(context, builder, false);
    }

    public QueryCursorLoader(Context context, final QueryBuilder builder, boolean notifyFromAllTables) {
        super(context, builder.getUri(), builder.getProjection(), builder.getWhere(), builder.getWhereArgs(), builder.getOrderBy());
        if (notifyFromAllTables) {
            final Uri mainUri = builder.getUri();

            if (LikeOrmUriHelper.isJoinUri(mainUri)) {
                joinContentObserver = new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        getContext().getContentResolver().notifyChange(mainUri, null);
                    }
                };
                List<String> tables = LikeOrmUriHelper.getAllTables(mainUri);
                for (String table : tables) {
                    Uri uri = new LikeOrmUriHelper.Builder(context).addTable(table).build();
                    context.getContentResolver().registerContentObserver(uri, false, joinContentObserver);
                }
            }
        }
    }

    @Override
    protected void onStopLoading() {
        if (joinContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(joinContentObserver);
        }
    }
}
