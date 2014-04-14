package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;

import com.github.nrudenko.orm.sql.TableJoin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LikeOrmUriHelper {

    public static final String JOIN = "join";
    public static final String TABLE = "table";
    public static final String SHOULD_NOTIFY = "should_notify";
    private final Uri baseUri;

    public LikeOrmUriHelper(Context context, Class<? extends ContentProvider> providerClass) {
        String authority = MetaDataParser.getAuthority(context, providerClass);
        this.baseUri = Uri.parse("content://" + authority);
    }

    public LikeOrmUriHelper(Context context) {
        String authority = MetaDataParser.getFirstAuthority(context);
        this.baseUri = Uri.parse("content://" + authority);
    }

    public Uri getTableUri(String mainTable, TableJoin... joins) {
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (joins.length > 0) {
            uriBuilder.appendPath(JOIN).appendPath(mainTable);
            for (int i = 0; i < joins.length; i++) {
                TableJoin join = joins[i];
                uriBuilder.appendQueryParameter(TABLE.concat(join.getCls().getSimpleName()), join.getSql());
            }
        } else {
            uriBuilder.appendPath(TABLE).appendPath(mainTable);
        }
        return uriBuilder.build();
    }

    public List<String> getAllTables(Uri uri) {
        List<String> result = new ArrayList<String>();

        String mainTable = uri.getLastPathSegment();
        result.add(mainTable);

        Set<String> queryParameterNames = uri.getQueryParameterNames();
        Iterator<String> iterator = queryParameterNames.iterator();
        while (iterator.hasNext()) {
            String table = iterator.next();
            if (table.startsWith(TABLE)) {
                result.add(table.replaceFirst(TABLE, ""));
            }
        }
        return result;
    }

    public static boolean shouldNotify(Uri uri) {
        return uri.getBooleanQueryParameter(SHOULD_NOTIFY, true);
    }

    public boolean isJoinUri(Uri mainUri) {
        return JOIN.equals(mainUri.getPathSegments().get(0));
    }
}
