package com.example.bonusball.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class CircleES2 {
    int mProgram;

	int muMVPMatrixHandle;

	int maPositionHandle;
	int maColorHandle;

	String mVertexShader;
	String mFragmentShader;

	FloatBuffer mVertexBuffer;
	FloatBuffer mColorBuffer;

	private ByteBuffer mIndexBuffer;

	int vCount = 0;
	int iCount = 0;

	public CircleES2(){
		initVertexData();
		initShader();
	}

	public void initVertexData(){
		int n = 10;
		//参考Belt.java文件终于知道为什么这里是加2不是加1了！
		//原因如下：作为条带，分成6份，需要在第七份上加一组边，
		//Circle也是这样啊，表面上第一条边和最后一条边重合了,
		//实际上在for循环时，重合的第一条边和最后一天都会画了一次~
		vCount = n + 2;
		float angdegSpan = 360.0f / n;
		float[] vertices = new float[vCount * 3];

		int count = 0;
		vertices[count++] = 0;
		vertices[count++] = 0;
		vertices[count++] = 0;
		for(float angdeg = 0;Math.ceil(angdeg) <= 360;angdeg+=angdegSpan){
			double angrad = Math.toRadians(angdeg);
			vertices[count++] = (float) (-Constant.UNIT_SIZE * Math.sin(angrad));
			vertices[count++] = (float) ( Constant.UNIT_SIZE * Math.cos(angrad));
			vertices[count++] = 0;
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		iCount = vCount;
		byte indices[] = new byte[iCount];
		for(int i = 0;i<iCount;i++){
			indices[i] = (byte)i;
		}

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);

		count = 0;
		//r g b alpha
		float colors[] = new float[vCount*4];
		colors[count++] = 1;
		colors[count++] = 1;
		colors[count++] = 1;
		colors[count++] = 0;
		//因为这里是一次上色四个值！
		for(int i = 4;i<colors.length; i+=4){
			colors[count++] = 0;
			colors[count++] = 1;
			colors[count++] = 0;
			colors[count++] = 0;
		}
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);


	}



	public void initShader(){
//		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
//		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());

        mVertexShader = "precision mediump float;"+
                "varying  vec4 vColor;"+ //接收从顶点着色器过来的参数
                "varying vec3 vPosition;"+//接收从顶点着色器过来的顶点位置
                "void main() {  "+
                "   gl_FragColor = vColor;"+//给此片元颜色值
                "}";

        mFragmentShader = "uniform mat4 uMVPMatrix;" + //总变换矩阵
                "attribute vec3 aPosition;" +  //顶点位置\n" +
                "attribute vec4 aColor;" +    //顶点颜色\n" +
                "varying  vec4 vColor;" +  //用于传递给片元着色器的变量\n" +
                "void main()  {" +
                "   gl_Position = uMVPMatrix * vec4(aPosition,1);" + //根据总变换矩阵计算此次绘制此顶点位置\n" +
                "   vColor = aColor;" +//将接收的颜色传递给片元着色器\n" +
                "}";


		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maColorHandle = GLES20.glGetAttribLocation(mProgram,"aColor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
	}

	public void drawSelf(){
		GLES20.glUseProgram(mProgram);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(),0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,false,
				3*4 , mVertexBuffer);

		GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
				4*4, mColorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maColorHandle);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, iCount,GLES20.GL_UNSIGNED_BYTE,
				mIndexBuffer);
	}

}