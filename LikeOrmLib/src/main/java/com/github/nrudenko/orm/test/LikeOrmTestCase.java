package com.github.nrudenko.orm.test;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
import android.test.mock.MockCursor;
import android.test.mock.MockPackageManager;

//TODO SHOULD BE REVIEWD AND REFACTORED TO ACTUAL STATE

/**
 * NOT STABLE
 */
@Deprecated
public class LikeOrmTestCase extends AndroidTestCase {

    private Context mMockContext;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    class MockPackage extends MockPackageManager {
        @Override
        public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, flags);
            ProviderInfo providerInfo = new ProviderInfo();
            providerInfo.authority = AUTORITY;
            providerInfo.name = MockContentProvider.class.getName();

            packageInfo.providers = new ProviderInfo[]{providerInfo};
            return packageInfo;
        }
    }

    @Override
    public Context getContext() {
        return mMockContext;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        mMockContext = new MockContext3();
    }


    class MockContext3 extends MockContext {

        @Override
        public ContentResolver getContentResolver() {
            MockContentResolver mockContentResolver = new MockContentResolver();
            ContentProvider provider = new MockContentProvider() {
                @Override
                public int delete(Uri uri, String selection, String[] selectionArgs) {
                    return 0;
                }

                @Override
                public int bulkInsert(Uri uri, ContentValues[] values) {
                    return 0;
                }

                @Override
                public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                    return new MockCursor(){
                        @Override
                        public boolean moveToNext() {
                            return false;
                        }

                        @Override
                        public int getCount() {
                            return 0;
                        }
                    };
                }

                @Override
                public Uri insert(Uri uri, ContentValues values) {
                    return uri;
                }
            };
            mockContentResolver.addProvider(AUTORITY, provider);
            return mockContentResolver;
        }

        @Override
        public String getPackageName() {
            return mContext.getPackageName();
        }

        @Override
        public PackageManager getPackageManager() {
            return new MockPackage();
        }

        @Override
        public Object getSystemService(String name) {
            return mContext.getSystemService(name);
        }

    }

    public static final String AUTORITY = "auth";
}
