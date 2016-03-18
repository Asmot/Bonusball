package com.example.bonusball.opengl;

import java.nio.ByteBuffer;

import android.opengl.Matrix;

public class MatrixState {
    private static float[] mProjMatrix = new float[16];
	private static float[] mVMatrix = new float[16];
	private static float[] currMatrix ;
	
	static float[][] mStack = new float[10][16];
	static int stackTop = -1;
	
	public static void setInitStack(){
		currMatrix = new float[16];
		Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);		
	}
	
	public static void pushMatrix(){
		stackTop++;
		for(int i=0;i<16;i++){
			mStack[stackTop][i] = currMatrix[i];
		}
	}
	
	public static void popMatrix(){
		for(int i=0;i<16;i++){
			currMatrix[i] = mStack[stackTop][i];
		}
		stackTop--;
	}

	public static void translate(float x, float y, float z){
		Matrix.translateM(currMatrix, 0, x, y, z);
	}
	
	static ByteBuffer llbb = ByteBuffer.allocateDirect( 3 * 4);
	static float[] cameraLocation = new float[3];
	public static void setCamera(
			float cx, float cy, float cz,
			float tx, float ty, float tz,
			float upx, float upy, float upz){
		Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
	}
	
	public static void setProjectFrustum(
			float left, float right,float bottom,
			float top, float near, float far){
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}
	
	public static void setProjectOrtho(
			float left, float right,float bottom,
			float top, float near, float far){
		Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}
	
	static float[] mMVPMatrix = new float[16];
	public static float[] getFinalMatrix(){
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
	
	public static float[] getMMatrix(){
		return currMatrix;
	}
	
	
}