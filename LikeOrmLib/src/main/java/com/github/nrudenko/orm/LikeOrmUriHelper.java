package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;

import com.github.nrudenko.orm.sql.TableJoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
            String authority = getAuthority(context, providerClass);
            this.baseUri = Uri.parse("content://" + authority);
            uriBuilder = baseUri.buildUpon();
        }

        public Builder(Context context) {
            String authority = getAuthority(context, null);
            this.baseUri = Uri.parse("content://" + authority);
            this.uriBuilder = baseUri.buildUpon();
        }

        protected String getAuthority(Context context, Class<? extends ContentProvider> providerClass) {
            if (providerClass == null) {
                return new MetaDataParser().getFirstAuthority(context);
            } else {
                return new MetaDataParser().getAuthority(context, providerClass);
            }
        }

        public Builder setTable(String mainTable, TableJoin... joins) {
            if (joins.length > 0) {
                uriBuilder.appendPath(JOIN).appendPath(mainTable);
                for (int i = 0; i < joins.length; i++) {
                    TableJoin join = joins[i];
                    uriBuilder.appendQueryParameter(
                            TABLE.concat(join.getCls().getSimpleName()), join.getSql());
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
        Set<String> queryParameterNames = getQueryParameterNames(uri);
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

        Set<String> queryParameterNames = getQueryParameterNames(uri);
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
        return getBooleanQueryParameter(uri, NOTIFY, true);
    }

    public static boolean isJoinUri(Uri uri) {
        return JOIN.equals(uri.getPathSegments().get(0));
    }

    public static String getGroupBy(Uri uri) {
        return uri.getQueryParameter(GROUP_BY);
    }

    public static boolean getBooleanQueryParameter(Uri uri, String key, boolean defaultValue) {
        String flag = uri.getQueryParameter(key);
        if (flag == null) {
            return defaultValue;
        }
        flag = flag.toLowerCase(Locale.ROOT);
        return (!"false".equals(flag) && !"0".equals(flag));
    }

    public static Set<String> getQueryParameterNames(Uri uri) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }
}
