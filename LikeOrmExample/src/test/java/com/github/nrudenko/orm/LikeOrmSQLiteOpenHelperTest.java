package com.github.nrudenko.orm;

import android.database.sqlite.SQLiteDatabase;

import com.github.nrudenko.orm.example.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;

@RunWith(LikeOrmTestRunner.class)
public class LikeOrmSQLiteOpenHelperTest {

    private DatabaseHelper dbHelper;
    @Mock
    private SQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        dbHelper = new DatabaseHelper(Robolectric.getShadowApplication().getApplicationContext());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnCreate() throws Exception {
        dbHelper.onCreate(db);
        verify(db).execSQL("CREATE TABLE IF NOT EXISTS " +
                "attach (Attach._id INTEGER DEFAULT 0,Attach.url TEXT,Attach.messageId TEXT);" +
                "CREATE TABLE IF NOT EXISTS example_model (ExampleModel._id INTEGER DEFAULT 0," +
                "ExampleModel.text TEXT,ExampleModel.date NUMERIC,ExampleModel.intVal " +
                "INTEGER DEFAULT 0);");
    }
}