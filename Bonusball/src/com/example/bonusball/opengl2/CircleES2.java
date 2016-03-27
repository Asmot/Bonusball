package com.example.bonusball.opengl2;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.bonusball.ball.BallForCH;
import com.example.bonusball.color.RandomColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CircleES2 extends BallForCH {

    public float x;
    public float y;
    public float z;

    float radius = 0.1f;
    float[] colors = new float[4];

    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maColorHandle;

    String mVertexShader;
    String mFragmentShader;

    FloatBuffer mVertexBuffer;
    ByteBuffer mIndexBuffer;


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


    private ArrayList<Float> floats = new ArrayList<Float>();
    private int indexCount;

    Random random = new Random();

    RandomColor randomColor = new RandomColor();

    public CircleES2(float x, float y, float z,float radius) {
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


        initVertexData();
        initShader();
    }


    public void initVertexData() {
        //中心点
        floats.add(0f);
        floats.add(0f);
        floats.add(0f);
        //周围的点
        for (float alpha = 0; alpha < Math.PI * 2.125; alpha += Math.PI / 8.0) {
            floats.add((float) (radius * Math.sin(alpha)));
            floats.add((float) (radius * Math.cos(alpha)));
            floats.add(0f);
        }


        ByteBuffer vbb = ByteBuffer.allocateDirect(floats.size() * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        for (Float f : floats) {
            mVertexBuffer.put(f);
        }
        mVertexBuffer.position(0);

        indexCount = floats.size() / 3;
        byte[] indexs = new byte[indexCount];
        for (int i = 0; i < indexCount; i++)
            indexs[i] = (byte) i;


        mIndexBuffer = ByteBuffer.allocateDirect(indexs.length);
        mIndexBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer.put(indexs);
        mIndexBuffer.position(0);
    }


    public void initShader() {

        mFragmentShader = "precision mediump float;" +
                "uniform  vec4 vColor;" +
                "varying vec3 vPosition;" +//接收从顶点着色器过来的顶点位置
                "void main() {  " +
                "   gl_FragColor = vColor;" +//给此片元颜色值
                "}";

        mVertexShader = "uniform mat4 uMVPMatrix;" + //总变换矩阵
                "attribute vec3 aPosition;" +  //顶点位置\n" +
                "void main()  {" +
                "   gl_Position = uMVPMatrix * vec4(aPosition,1);" + //根据总变换矩阵计算此次绘制此顶点位置\n" +

                "}";


        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(float[] mvpMatrix) {

        float[] mvp = Arrays.copyOf(mvpMatrix,mvpMatrix.length);

        Matrix.translateM(mvp, 0, x, y, z);

        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvp, 0);
        ShaderUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        ShaderUtil.checkGlError("glVertexAttribPointer");

        GLES20.glUniform4f(maColorHandle, colors[0], colors[1], colors[2], colors[3]);
        ShaderUtil.checkGlError("glUniform4f");
//        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
//                0, mColorBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        ShaderUtil.checkGlError("glEnableVertexAttribArray");

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indexCount, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        ShaderUtil.checkGlError("glDrawElements");

    }

}