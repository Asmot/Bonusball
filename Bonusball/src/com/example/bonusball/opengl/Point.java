package com.example.bonusball.opengl;

import android.graphics.Color;
import android.opengl.GLES10;
import android.opengl.GLU;

import com.example.bonusball.ball.Ball;
import com.example.bonusball.ball.BallForCH;
import com.example.bonusball.color.RandomColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zxy94400 on 2016/3/15.
 */
public class Point extends BallForCH {

    public float x;
    public float y;
    public float z;

    private float velocityX;
    private float velocityY;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVX() {
        return velocityX;
    }

    public float getVY() {
        return velocityY;
    }

  /*  public void setColor(int color)
    {
        this.color=color;
    }*/

    public void setPosX(float newX) {
        this.x = newX;
    }

    public void setPosY(float newY) {
        this.y = newY;
    }

    public void setVX(float newVX) {
        this.velocityX = newVX;
    }

    public void setVY(float newVY) {
        this.velocityY = newVY;
    }


    FloatBuffer floatBuffer = null;

    public Point() {

    }

    Random random = new Random();

    float[] colors = new float[4];

    RandomColor randomColor = new RandomColor();
    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        setVX((float) (random.nextFloat() * 0.05 * (random.nextBoolean() ? 1 : -1)));
        setVY((float) (random.nextFloat() * 0.05 * (random.nextBoolean() ? 1 : -1)));

        int color = randomColor.randomColor();
        colors[0] = Color.red(color) / 255;
        colors[1] = Color.green(color) / 255;
        colors[2] = Color.blue(color) / 255;
        colors[3] = Color.alpha(color) / 255;

    }


    public void setPosition() {
        if (floatBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            floatBuffer = byteBuffer.asFloatBuffer();
        } else {
            floatBuffer.clear();
        }
        floatBuffer.put(x);
        floatBuffer.put(y);
        floatBuffer.put(z);
        floatBuffer.put(0);
        floatBuffer.position(0);
    }


    public void draw() {

        setPosition();
        GLES10.glPushMatrix();
        // 设置颜色
        GLES10.glColor4f(colors[0], colors[1], colors[2], colors[3]);

        //打开深度检测
        GLES10.glEnableClientState(GLES10.GL_DEPTH_TEST);
        //打开背面剪裁
        GLES10.glEnableClientState(GLES10.GL_CULL_FACE);


        //开启顶点缓冲区
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glPointSize(20);


        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuffer);


        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, 1);

        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisableClientState(GLES10.GL_DEPTH_TEST);
        GLES10.glDisableClientState(GLES10.GL_CULL_FACE);

        GLES10.glPopMatrix();
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(x).append(",").append(y).append(",").append(z).append(")");
        return buffer.toString();
    }
}
