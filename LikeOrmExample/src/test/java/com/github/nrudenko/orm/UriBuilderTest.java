package com.github.nrudenko.orm;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(LikeOrmTestRunner.class)
public class UriBuilderTest {

    final String TEST_TABLE_NAME = "test_table";

    private LikeOrmUriHelper.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = new LikeOrmUriHelper.Builder(Robolectric.getShadowApplication().getApplicationContext());
    }

    @Test
    public void testGetAuthority() throws Exception {

    }

    @Test
    public void testSetTable() throws Exception {
        Uri testTableUri = builder.setTable(TEST_TABLE_NAME).build();
        List<String> segments = testTableUri.getPathSegments();
        assertEquals("Too many uri segments", segments.size(), 2);
        String table = segments.get(0);
        assertEquals("Uri format wrong", table, LikeOrmUriHelper.TABLE);
        String testTable = segments.get(1);
        assertEquals("Table name wrong", testTable, TEST_TABLE_NAME);
    }

    @Test
    public void testAddGroupBy() throws Exception {

    }

    @Test
    public void testAddHaving() throws Exception {

    }

    @Test
    public void testWithoutNotify() throws Exception {

    }

    @Test
    public void testBuild() throws Exception {

    }
}