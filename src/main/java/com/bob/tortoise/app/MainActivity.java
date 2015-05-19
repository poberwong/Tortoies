package com.bob.tortoise.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bob.tortoise.R;


public class MainActivity extends Activity implements View.OnClickListener {
    private CustomView customView;
    private Thread thread;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customView = (CustomView) findViewById(R.id.cv);//静态添加自定义控件
        thread = new Thread(customView);
        thread.start();//开启线程
/*        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }
    @Override
    public void onClick(View view) {//两种监听方式，总共三种，甚至更多
        if (flag) {
            flag = false;
            customView.hide();//乌龟藏起来并停止走动
        } else {
            flag= true;
            customView.resume();
        }
    }
}
