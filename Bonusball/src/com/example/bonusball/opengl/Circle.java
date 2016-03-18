package com.example.bonusball.opengl;

import android.graphics.Color;
import android.opengl.GLES10;

import com.example.bonusball.ball.BallForCH;
import com.example.bonusball.color.RandomColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by zxy94400 on 2016/3/15.
 */
public class Circle extends BallForCH {

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

    public Circle() {

    }

    Random random = new Random();

    private float[] colors = new float[4];
    private ArrayList<Float> coords = new ArrayList<Float>();
    private float radius = 0.1f;

    RandomColor randomColor = new RandomColor();
    public Circle(float x, float y, float z,float radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;

        setVX((float) (random.nextFloat() * 0.05 * (random.nextBoolean() ? 1 : -1)));
        setVY((float) (random.nextFloat() * 0.05 * (random.nextBoolean() ? 1 : -1)));

        int color = randomColor.randomColor();
        colors[0] = Color.red(color) / 255;
        colors[1] = Color.green(color) / 255;
        colors[2] = Color.blue(color) / 255;
        colors[3] = Color.alpha(color) / 255;


    }


    public void setPosition() {
        coords.clear();
        coords.add(x);
        coords.add(y);
        coords.add(z);
        for(float alpha = 0; alpha < Math.PI * 2.125; alpha += (Math.PI / 8)) {
            coords.add(x + (float) (Math.cos(alpha) * radius));
            coords.add(y + (float) (Math.sin(alpha) * radius));
            coords.add(z);
        }

        if (floatBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coords.size() * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            floatBuffer = byteBuffer.asFloatBuffer();
        } else {
            floatBuffer.clear();
        }

        for(Float f : coords) {
            floatBuffer.put(f);
        }
        floatBuffer.position(0);
    }


    public void draw() {

        setPosition();
        GLES10.glPushMatrix();
        // 设置颜色
        GLES10.glColor4f(colors[0], colors[1], colors[2], colors[3]);


        //开启顶点缓冲区
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);


        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuffer);


        GLES10.glDrawArrays(GLES10.GL_TRIANGLE_FAN, 0, coords.size() / 3);

        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glPopMatrix();
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(x).append(",").append(y).append(",").append(z).append(")");
        return buffer.toString();
    }
}
