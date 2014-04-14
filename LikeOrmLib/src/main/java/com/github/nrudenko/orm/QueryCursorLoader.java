package com.github.nrudenko.orm;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.List;

public class QueryCursorLoader extends CursorLoader {

    private ContentObserver joinContentObserver;

    public QueryCursorLoader(Context context, Class<? extends LikeOrmContentProvider> providerClass, QueryBuilder builder) {
        super(context, builder.getUri(), builder.getProjection(), builder.getWhere(), builder.getWhereArgs(), builder.getOrderBy());
    }

    public QueryCursorLoader(Context context, final QueryBuilder builder) {
        super(context, builder.getUri(), builder.getProjection(), builder.getWhere(), builder.getWhereArgs(), builder.getOrderBy());

        LikeOrmUriHelper likeOrmUriHelper = new LikeOrmUriHelper(context);

        final Uri mainUri = builder.getUri();

        if (likeOrmUriHelper.isJoinUri(mainUri)) {
            joinContentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    Log.d("ss", uri.toString());
                    getContext().getContentResolver().notifyChange(mainUri, null);
                }
            };
            List<String> tables = likeOrmUriHelper.getAllTables(mainUri);
            for (String table : tables) {
                context.getContentResolver().registerContentObserver(likeOrmUriHelper.getTableUri(table), false, joinContentObserver);
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
