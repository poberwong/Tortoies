package com.bob.tortoise.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import com.bob.tortoise.utils.Constants;
import com.bob.tortoise.utils.MeasureUtil;

/**
 * Created by bob on 15-5-14.
 */
public class CustomView extends View implements Runnable, SensorEventListener {
    private Paint paint;//画笔
    private boolean isHide = false;//是否躲进龟壳
    private int eyeCount = 0;

    private int direction;//定义方向
    private int speed = 12;//移动步长
    private int hz = 100;//刷新频率

    private int[] size;//屏幕尺寸数组，单位是px
    private int[] foot = new int[2];//四肢坐标
    private int[] body = new int[2];//身体坐标
    private float[] rollers = new float[3];//重力感应的三个坐标

    /*
     * View控件必须要有长宽参数，所以上边这个构造函数基本用不上，而
     * 下边这个在静态添加组件的时候会被调用。
     */
    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//初始化传感器
        size = MeasureUtil.getScreenSize((Activity) context);//获取屏幕尺寸
        init();//对画笔和坐标进行初始化
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//注册为游戏级别的灵敏度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isHide) {
            paint.setColor(Color.BLACK);
            canvas.drawCircle(body[0], body[1] - 250, 85, paint);//head edge
            paint.setColor(Color.GREEN);
            canvas.drawCircle(body[0], body[1] - 250, 80, paint);//head

            paint.setColor(Color.BLACK);
            canvas.drawCircle(foot[0] + 140, foot[1] - 140, 55, paint);//paint the leg  edge
            canvas.drawCircle(foot[0] - 140, foot[1] - 140, 55, paint);
            canvas.drawCircle(foot[0] + 140, foot[1] + 140, 55, paint);
            canvas.drawCircle(foot[0] - 140, foot[1] + 140, 55, paint);
            paint.setColor(Color.GREEN);
            canvas.drawCircle(foot[0] + 140, foot[1] - 140, 50, paint);//paint the leg
            canvas.drawCircle(foot[0] - 140, foot[1] - 140, 50, paint);
            canvas.drawCircle(foot[0] + 140, foot[1] + 140, 50, paint);
            canvas.drawCircle(foot[0] - 140, foot[1] + 140, 50, paint);

            paint.setColor(Color.BLACK);
            eyeCount++;
            if (eyeCount % 10 == 0) {//眼睛的选择性绘制
                canvas.drawLine(body[0] - 35, body[1] - 265, body[0] - 25, body[1] - 265, paint);
                canvas.drawLine(body[0] + 25, body[1] - 265, body[0] + 35, body[1] - 265, paint);
            } else {
                canvas.drawCircle(body[0] - 30, body[1] - 265, 10, paint);//eye
                canvas.drawCircle(body[0] + 30, body[1] - 265, 10, paint);
            }
        }
        paint.setColor(Color.BLACK);
        canvas.drawCircle(body[0], body[1], 205, paint);//paint the body edge
        paint.setColor(Color.GREEN);
        canvas.drawCircle(body[0], body[1], 200, paint);//body

        paint.setColor(Color.BLACK);
        paint.setTextSize(80);
        if (!isHide) {
            canvas.drawText("玄", body[0] - 40, body[1] - 20, paint);
            canvas.drawText("武", body[0] - 40, body[1] + 90, paint);
        } else {
            canvas.drawText("放老子", body[0] - 110, body[1] - 20, paint);
            canvas.drawText("出  去", body[0] - 100, body[1] + 90, paint);
        }
    }

    public void hide() {

        isHide = true;
    }

    public void resume() {
        isHide = false;
    }

    private void init() {//对画笔的初始化
        /**
         * 画笔初建的时候就设置消除锯齿
         * 当然也可以调用	paint.setAntiAlias(true);的方式达到同样效果
         */
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        //paint.setStyle(Paint.Style.STROKE);//设置画笔为描边,即空心图形
        paint.setStrokeWidth(5);//设置画笔宽度,单位为px

        body[0] = size[0] / 2 - 50;
        body[1] = size[1] / 2;//身体的坐标为屏幕中心
        foot[0] = body[0];
        foot[1] = body[1];
        direction = Constants.DIRECTION.UP;//默认方向为上
    }

    private void move(int direction, int... params) {

        switch (direction) {
            case Constants.DIRECTION.UP://上
                if (params[1] - 340 > 0)
                    params[1] -= speed;
                break;
            case Constants.DIRECTION.DOWN://下
                if (params[1] + 260 < size[1])
                    params[1] += speed;
                break;
            case Constants.DIRECTION.LEFT://左
                if (params[0] - 205 > 0)
                    params[0] -= speed;
                break;
            case Constants.DIRECTION.RIGHT://右
                if (params[0] + 205 < size[0])
                    params[0] += speed;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!isHide) {//避开处理线程这个比较复杂的问题
                    move(direction, foot);
                    Thread.sleep(hz);//移动脚步之后立即重绘，否则身体和教的相对位置永远不会改变
                    postInvalidate();
                    move(direction, body);
                    Thread.sleep(hz);
                    postInvalidate();// 进行异步重绘
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {//重力方向监听
        /**
         * 屏幕向上（0,0,10）//水平放置
         * 下：（0,0，10）
         * 左：（10,0,0）
         * 右：（0,10,0）
         */
        rollers[0] = event.values[SensorManager.DATA_X];
        rollers[1] = event.values[SensorManager.DATA_Y];
        rollers[2] = event.values[SensorManager.DATA_Z];

        int temp = direction;
        switch (maxRoller()) {
            case 0:
                if (rollers[maxRoller()] < 0)
                    temp = Constants.DIRECTION.RIGHT;
                else
                    temp = Constants.DIRECTION.LEFT;
                break;
            case 1:
                if (rollers[maxRoller()] < 0)
                    temp = Constants.DIRECTION.UP;
                else
                    temp = Constants.DIRECTION.DOWN;

        }
        if (temp != direction){//防止同方向还进行方向重置
            direction = temp;
            foot[0] = body[0];
            foot[1] = body[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int maxRoller() {//查找绝对值最大的一个轴的下角标
        int maxIndex = 0;
        for (int i = 1; i < rollers.length; i++) {
            if (Math.abs(rollers[i]) > Math.abs(rollers[maxIndex]))
                maxIndex = i;
        }
        return maxIndex;
    }
}
