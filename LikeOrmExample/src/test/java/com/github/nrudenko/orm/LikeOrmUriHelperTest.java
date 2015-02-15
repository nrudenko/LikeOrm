package com.github.nrudenko.orm;

import android.net.Uri;

import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.sql.TableJoin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(LikeOrmTestRunner.class)
public class LikeOrmUriHelperTest {

    private static final String TEST_TABLE_NAME = "test_table";
    private LikeOrmUriHelper.Builder builder;

    private Uri JOIN_URI = Uri.parse("content://com.github.nrudenko.orm.example/join/test_table?tableJoinedTable=LEFT%20JOIN%20JoinedTable%20ON%20left_col%3Dright_col&tableJoinedTable2=LEFT%20JOIN%20JoinedTable2%20ON%20left_col2%3Dright_col2");

    @Before
    public void setUp() throws Exception {
        builder = new LikeOrmUriHelper.Builder(Robolectric.getShadowApplication().getApplicationContext());
    }

    class JoinedTable {
    }

    class JoinedTable2 {
    }

    @Test
    public void testJoinUrlCreation() {
        TableJoin join = new TableJoin(JoinedTable.class, new Column("left_col"), new Column("right_col"));
        TableJoin join2 = new TableJoin(JoinedTable2.class, new Column("left_col2"), new Column("right_col2"));
        Uri joinUri = builder.setTable(TEST_TABLE_NAME, join, join2).build();
        assertEquals(joinUri, JOIN_URI);
    }

    @Test
    public void testGetJoins() throws Exception {
        List<String> joins = LikeOrmUriHelper.getJoins(JOIN_URI);
        assertEquals("More then need joins", joins.size(), 2);
        assertEquals("Wrong first join", joins.get(0), "LEFT JOIN JoinedTable ON left_col=right_col");
        assertEquals("Wrong second join", joins.get(1), "LEFT JOIN JoinedTable2 ON left_col2=right_col2");
    }

    @Test
    public void testGetAllTables() throws Exception {
        List<String> allTables = LikeOrmUriHelper.getAllTables(JOIN_URI);
        assertTrue("Wrong tables amount", allTables.size() == 3);
        assertTrue(allTables.contains(TEST_TABLE_NAME));
        assertTrue(allTables.contains("JoinedTable"));
        assertTrue(allTables.contains("JoinedTable2"));
    }

    @Test
    public void testShouldNotify() throws Exception {
        Uri uri = builder.setTable(TEST_TABLE_NAME).build();
        boolean shouldNotify = LikeOrmUriHelper.shouldNotify(uri);
        assertTrue("Not build uri by default with notify", shouldNotify);

    }

    @Test
    public void testShouldNotNotify() throws Exception {
        Uri uri = builder.setTable(TEST_TABLE_NAME).withoutNotify().build();
        boolean shouldNotify = LikeOrmUriHelper.shouldNotify(uri);
        assertFalse("Not build uri without notify", shouldNotify);
    }

    @Test
    public void testIsJoinUri() throws Exception {
        boolean isJoinUri = LikeOrmUriHelper.isJoinUri(JOIN_URI);
        assertTrue(isJoinUri);
    }

    @Test
    public void testIsNotJoinUri() throws Exception {
        Uri notJoinUrl = builder.setTable(TEST_TABLE_NAME).build();
        boolean isJoinUri = LikeOrmUriHelper.isJoinUri(notJoinUrl);
        assertFalse(isJoinUri);
    }

    @Test
    public void testGetGroupBy() throws Exception {
        String groupBy = "groupcolumn by ASC";
        Uri groupByUri = builder.setTable(TEST_TABLE_NAME).addGroupBy(groupBy).build();
        assertEquals(LikeOrmUriHelper.getGroupBy(groupByUri), groupBy);
    }
}