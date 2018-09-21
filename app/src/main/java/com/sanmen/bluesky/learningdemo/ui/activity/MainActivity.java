package com.sanmen.bluesky.learningdemo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sanmen.bluesky.learningdemo.R;
import com.sanmen.bluesky.learningdemo.widget.RoundProgressView;

public class MainActivity extends AppCompatActivity {

    RoundProgressView roundProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
    }

    private void initLayout() {
        roundProgressView = findViewById(R.id.rpvProgress);
        roundProgressView.setProgress(20);
    }
}
