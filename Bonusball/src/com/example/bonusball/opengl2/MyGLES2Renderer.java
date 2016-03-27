/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bonusball.opengl2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.example.bonusball.ball.BallForCH;
import com.example.bonusball.hzk.ScreenPoint;
import com.example.bonusball.opengl.Circle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLES2Renderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private float ratio = 0;

    List<BallForCH> points = new ArrayList<BallForCH>();

    float[] coords = new float[]{
            -0.8f, -0.4f * 1.732f, 0.0f,
            0.8f, -0.4f * 1.732f, 0.0f,
            0.0f, 0.4f * 1.732f, 0.0f,
    };
    FloatBuffer floatBuffer = null;


    private float width;
    private float height;
    private float openglFrustumHalfWidth;
    private float openglFrustumHalfHeight;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
//    private final float[] mRotationMatrix = new float[16];

    CircleES2 circle;

    public void onDrawFrame(GL10 gl) {

        updatePositions();


        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //设置眼球位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);
//        GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        //ModelViewProjection = Projextion x View
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        for (BallForCH point : points) {
            ((CircleES2) point).drawSelf(mMVPMatrix);
        }

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {


        //设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);

        //计算GLSurfaceView的宽高比
        ratio = (float) height / (float) width;
        // 调用此方法计算产生透视投影矩阵
        //设置平截头体
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);


        this.width = width;
        this.height = height;
        openglFrustumHalfWidth = 1;
        openglFrustumHalfHeight = ratio;


    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置屏幕背景色RGBA
        GLES20.glClearColor(0, 0, 0, 1);

        if(points.size() > 1) {
            // 切到后台回来，会执行这个操作
            return;
        }

        Random random = new Random();

        for (int i = 0; i < 50; i++) {
            points.add(new CircleES2(random.nextFloat(), random.nextFloat(), 2,  random.nextFloat() * 0.1f));

        }


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(coords);
        floatBuffer.position(0);


    }


    float mouseX, mouseY;// 当前鼠标坐标
    float mouseVX, mouseVY;// 鼠标速度
    float prevMouseX, prevMouseY;// 上次鼠标坐标
    boolean isMouseDown = false;// 鼠标左键是否按下
    /**
     * 摩擦力
     */
    private final static float FRICTION = 0.96f;        //

    Random mRandom = new Random();

    /**
     * 改变所有球的位置
     * 根据每个球当前的速度
     * 如果碰到边界 反弹
     */
    private void updatePositions() {
        // TODO Auto-generated method stub

        //如果字写完了停止线程
        //允许有两个球。。。迟迟跑不到位

        mouseVX = mouseX - prevMouseX;
        mouseVY = mouseY - prevMouseY;
        prevMouseX = mouseX;
        prevMouseY = mouseY;

        float toDist = openglFrustumHalfHeight * 1.86f;
        float stirDist = openglFrustumHalfHeight * 0.125f;
        float blowDist = openglFrustumHalfHeight * 0.5f;

        for (BallForCH b : points) {
            float x = b.getX();
            float y = b.getY();
            float vX = b.getVX();
            float vY = b.getVY();

            float dX = x - mouseX;
            float dY = y - mouseY;
            if (b.isReadyToForm())//当前球处于构建汉字状态
            {
                dX = x - b.getTargetX();
                dY = y - b.getTargetY();

            }
            float d = (float) Math.sqrt(dX * dX + dY * dY);
            dX = d > 0 ? dX / d : 0;
            dY = d > 0 ? dY / d : 0;


//            //鼠标按下监听 点击屏幕。。。
//            if (isMouseDown && d < blowDist) {
//                float blowAcc = (1 - (d / blowDist)) * 14;
//                vX += dX * blowAcc + 0.5f - mRandom.nextFloat() / Integer.MAX_VALUE;
//                vY += dY * blowAcc + 0.5f - mRandom.nextFloat() / Integer.MAX_VALUE;
//            }
            //修改速度
            //离触摸点越远速度越小
            if (d < toDist) {
                float toAcc = (1 - (d / toDist)) * openglFrustumHalfHeight * 0.0014f;
                vX -= dX * toAcc;
                vY -= dY * toAcc;
            }

            if (d < stirDist) {
                float mAcc = (1 - (d / stirDist)) * openglFrustumHalfHeight * 0.00026f;
                vX += mouseVX * mAcc;
                vY += mouseVY * mAcc;
            }
            vX *= FRICTION;
            vY *= FRICTION;

            float avgVX = (float) Math.abs(vX);
            float avgVY = (float) Math.abs(vY);
            float avgV = (avgVX + avgVY) * 0.5f;

            if (avgVX < 0.001)
                vX *= mRandom.nextFloat() / Integer.MAX_VALUE * 3;//float(mRandom.nextInt()) / Integer.MAX_VALUE * 3;
            if (avgVY < 0.001) vY *= mRandom.nextFloat() / Integer.MAX_VALUE * 3;

            float sc = avgV * 0.45f;
            sc = Math.max(Math.min(sc, 3.5f), 0.4f);


            //移动，遇到边界反弹
            float nextX = x + vX;
            float nextY = y + vY;

            if (nextX > openglFrustumHalfWidth) {
                nextX = openglFrustumHalfWidth;
                vX *= -1;
            } else if (nextX < -openglFrustumHalfWidth) {
                nextX = -openglFrustumHalfWidth;
                vX *= -1;
            }
            if (nextY > openglFrustumHalfHeight) {
                nextY = openglFrustumHalfHeight;
                vY *= -1;
            } else if (nextY < -openglFrustumHalfHeight) {
                nextY = -openglFrustumHalfHeight;
                vY *= -1;
            }

            b.setVX(vX);
            b.setVY(vY);
            b.setPosX(nextX);
            b.setPosY(nextY);

        }
    }


    public void onRenderTouch(int action, float x, float y) {

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isMouseDown = true;
                break;
            case MotionEvent.ACTION_MOVE:

                isMouseDown = false;


                //转换为平截头体上面的大小,View原点在左下角，opengl原点在中心
                mouseX = convertX(x);// 宽度为1 *2
                mouseY = convertY(y);// 高度是ratio *2
                if (prevMouseX == 0) {
                    prevMouseX = mouseX;
                    prevMouseY = mouseY;
                }
                break;
            case MotionEvent.ACTION_UP:
                isMouseDown = false;
                break;
            default:
                break;
        }
    }

    private float convertY(float y) {
        return -(ratio * 2 * y / height) + openglFrustumHalfHeight;
    }

    private float convertX(float x) {
        return (2 * x / width) - openglFrustumHalfWidth;
    }


    //默认每5秒画完一个字，用一个timer来计数
    private Timer mTimer;
    ArrayList<ScreenPoint> screenlist = null;

    /**
     * 获取需要填充的坐标点
     *
     * @param list
     */
    public void formChinese(ArrayList<ScreenPoint> list) {
        screenlist = new ArrayList<ScreenPoint>();
        //取出需要填充的坐标点
        for (ScreenPoint point : list) {
            if (point.isFalg()) {
                screenlist.add(point);
            }
        }

        //一切准备就绪 游戏开始
        startGame();
    }

    public void startGame() {
        int index = 0;
        int size = screenlist.size();

        //修改每个小球的target坐标
        //坐标值取自汉字坐标值列表
        for (BallForCH b : points) {
            ScreenPoint point = screenlist.get(index);

            b.setTargetX(convertX(point.getX()));
            b.setTargetY(convertY(point.getY()));
            b.setReadyToForm(true);
//    		Log.i(TAG, " ready to stop " + b.getTargetX() + "," + b.getTargetY());

            index++;
            index = index % size;
        }

        //开启一个定时任务 几秒后 结束画字
        stopFormChineseTimerTask(5000);
    }

    private void stopFormChineseTimerTask(long when) {
        /*对timer进行处理
    	 * 一面上一次画字还没有画完
    	 * 又被要求重开，多个timer混在一起就乱了。。。
    	 */
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        //重新创建一个新的timer
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                formChineseComplete();
            }
        }, when);


    }

    private void formChineseComplete() {

        if(myCompleteListener != null) {
            myCompleteListener.onComplete();
        }

        for (BallForCH b : points) {
            b.setReadyToForm(false);//
        }
    }


    //外部监听 绘制完成的接口
    private OnCompleteListener myCompleteListener;

    /**
     * 轨迹球画完成事件
     */
    public interface OnCompleteListener {
        /**
         * 画完了
         */
        public void onComplete();
    }

    /**
     * 外部监听使用
     * @param myCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener myCompleteListener) {
        this.myCompleteListener = myCompleteListener;
    }
}