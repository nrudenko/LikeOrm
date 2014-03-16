package com.github.nrudenko.orm.example;

import android.database.DatabaseUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.githab.nrudenko.orm.example.R;
import com.github.nrudenko.orm.example.model.Attach;
import com.github.nrudenko.orm.example.model.ExampleModel;
import com.github.nrudenko.orm.OrmUri;
import com.github.nrudenko.orm.example.model.schema.AttachSchema;
import com.github.nrudenko.orm.example.model.schema.ExampleModelSchema;

import java.util.Date;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insert();
        update();
    }

    private void insert() {
        ExampleModel exampleModel = new ExampleModel();
        exampleModel.setDate(new Date());
        exampleModel.setText("text");
        exampleModel.setIntVal(1);
        getContentResolver().insert(OrmUri.get(ExampleModel.class), exampleModel.toContentValues());
    }

    private void update() {
        Attach attach = new Attach();
        attach.setUrl("url");
//        new Update(Attach.class)
//            .with(attach)
//            .where(AttachSchema.MESSAGE_ID)
//            .is(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
