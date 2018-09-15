package com.mikuwxc.autoreply.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mikuwxc.autoreply.R;

public class AuthorityActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);

    }


    public void close(View v){
        this.finish();
    }

}
