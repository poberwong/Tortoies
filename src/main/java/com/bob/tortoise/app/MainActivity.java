package com.bob.tortoise.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bob.tortoise.R;


public class MainActivity extends Activity {
    private CustomView customView;
    private Thread thread;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customView = (CustomView) findViewById(R.id.cv);//静态添加自定义控件
        thread = new Thread(customView);
        thread.start();
    }

    public void onClick(View view) {
        if (flag) {
            flag = false;
            customView.hide();//乌龟藏起来并停止走动，即休眠线程
        } else {
            flag= true;
            customView.resume();
        }
    }
}
