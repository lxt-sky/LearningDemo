package com.sanmen.bluesky.learningdemo.ui.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sanmen.bluesky.learningdemo.R;
import com.sanmen.bluesky.learningdemo.widget.DoubleTimeSelectDialog;
import com.sanmen.bluesky.learningdemo.widget.RoundProgressView;

/**
 * @author lxt_bluesky
 * create at 2018/9/25 11:43
 * @description this is MainActivity
 */
public class MainActivity extends AppCompatActivity {

    RoundProgressView roundProgressView;

    Button btnDialog;

    DoubleTimeSelectDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();

    }

    private void initLayout() {
        roundProgressView = findViewById(R.id.rpvProgress);
//        roundProgressView.setStokeCap(Paint.Cap.ROUND);

        roundProgressView.setProgress(30,true);

        btnDialog = findViewById(R.id.button);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoubleTimePicker();
            }
        });
        //设置最大进度
//        roundProgressView.setMaxProgress(80);
//        roundProgressView.setBaseUnit("fen");
//        roundProgressView.setBorderWidth(16);
//        roundProgressView.setTextSize(16);
//        roundProgressView.setBorderColor(Color.BLUE);
//        roundProgressView.setStartPosition(RoundProgressView.LEFT);

    }

    private void showDoubleTimePicker() {
        if (dialog==null){
            dialog=new DoubleTimeSelectDialog(this,"2017-07-07","2030-09-31","2010-09-31");
            dialog.setType(DoubleTimeSelectDialog.TYPE.HOUR_MINUTE);
            dialog.setOnDateSelectFinished(new DoubleTimeSelectDialog.OnDateSelectFinished() {
                @Override
                public void onSelectFinished(String startTime, String endTime) {

                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface d) {
                    dialog=null;
                }
            });

            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }
}
