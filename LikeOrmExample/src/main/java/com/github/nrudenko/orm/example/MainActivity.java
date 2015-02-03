package com.github.nrudenko.orm.example;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.githab.nrudenko.orm.example.R;
import com.github.nrudenko.orm.CursorUtil;
import com.github.nrudenko.orm.QueryBuilder;
import com.github.nrudenko.orm.example.model.Attach;
import com.github.nrudenko.orm.example.model.Audio;
import com.github.nrudenko.orm.example.model.ExampleModel;
import com.github.nrudenko.orm.example.model.Image;
import com.github.nrudenko.orm.example.model.Message;

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
        new QueryBuilder<ExampleModel>(this, SimpleContentProvider.class)
                .table(ExampleModel.class)
                .insert(exampleModel);

        Audio audio = new Audio();
        audio.setDate(new Date());
        audio.setPath("audio path");

        Image image = new Image();
        audio.setDate(new Date());
        audio.setPath("image");

        Message message = new Message();
        message.setDate(new Date());
        message.setAudio(audio);
        message.setImage(image);
        message.setText("lucky message");

        new QueryBuilder<Message>(this, SimpleContentProvider.class)
                .table(Message.class)
                .insert(message);
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
