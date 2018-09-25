package com.sanmen.bluesky.learningdemo.ui.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sanmen.bluesky.learningdemo.R;
import com.sanmen.bluesky.learningdemo.widget.RoundProgressView;

/**
 * @author lxt_bluesky
 * create at 2018/9/25 11:43
 * @description this is MainActivity
 */
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
//        roundProgressView.setStokeCap(Paint.Cap.ROUND);

        roundProgressView.setProgress(25,true);
        //设置最大进度
//        roundProgressView.setMaxProgress(80);
//        roundProgressView.setBaseUnit("S");
//        roundProgressView.setBorderWidth(16);
//        roundProgressView.setTextSize(16);
//        roundProgressView.setBorderColor(Color.BLUE);
//        roundProgressView.refresh();

    }
}
