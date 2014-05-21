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
    public static final String NOTIFY = "notify";
    public static final String GROUP_BY = "groupBy";

    public static class Builder {
        private final Uri.Builder uriBuilder;
        private final Uri baseUri;

        public Builder(Context context, Class<? extends ContentProvider> providerClass) {
            String authority = MetaDataParser.getAuthority(context, providerClass);
            this.baseUri = Uri.parse("content://" + authority);
            uriBuilder = baseUri.buildUpon();
        }

        public Builder(Context context) {
            String authority = MetaDataParser.getFirstAuthority(context);
            this.baseUri = Uri.parse("content://" + authority);
            this.uriBuilder = baseUri.buildUpon();
        }

        public Builder addTable(String mainTable, TableJoin... joins) {
            if (joins.length > 0) {
                uriBuilder.appendPath(JOIN).appendPath(mainTable);
                for (int i = 0; i < joins.length; i++) {
                    TableJoin join = joins[i];
                    uriBuilder.appendQueryParameter(TABLE.concat(join.getCls().getSimpleName()), join.getSql());
                }
            } else {
                uriBuilder.appendPath(TABLE).appendPath(mainTable);
            }
            return this;
        }

        public Builder addGroupBy(String groupBy) {
            uriBuilder.appendQueryParameter(GROUP_BY, groupBy);
            return this;
        }

        public Builder addHaving() {
            //TODO should be implement
            return this;
        }

        public Builder withoutNotify() {
            uriBuilder.appendQueryParameter(NOTIFY, Boolean.FALSE.toString());
            return this;
        }

        public Uri build() {
            return uriBuilder.build();
        }
    }

    public static List<String> getJoins(Uri uri) {
        List<String> result = new ArrayList<String>();
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        Iterator<String> iterator = queryParameterNames.iterator();
        while (iterator.hasNext()) {
            String param = iterator.next();
            if (param.startsWith(TABLE)) {
                result.add(uri.getQueryParameter(param));
            }
        }
        return result;
    }

    public static List<String> getAllTables(Uri uri) {
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
        return uri.getBooleanQueryParameter(NOTIFY, true);
    }

    public static boolean isJoinUri(Uri uri) {
        return JOIN.equals(uri.getPathSegments().get(0));
    }

    public static String getGroupBy(Uri uri) {
        return uri.getQueryParameter(GROUP_BY);
    }
}
