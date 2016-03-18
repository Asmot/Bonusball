package com.example.bonusball.opengl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bonusball.CanvasView;
import com.example.bonusball.CanvasView.OnCompleteListener;
import com.example.bonusball.R;
import com.example.bonusball.hzk.HZKUtils;
import com.example.bonusball.hzk.ScreenCal;
import com.example.bonusball.hzk.ScreenPoint;

import java.util.ArrayList;

public class BallOpenGLActivity extends Activity{

    private final static int BALL_NUM=100;//球的数量

    private GestureDetector gd;  //手势监听


    //在屏幕上画一个16*16的点阵 记录所有的坐标
    private ArrayList<ScreenPoint> screenlist;

    EditText inputEt=null;

    private String str="郑";//显示的字
    private int index=0;

    private Handler handler;

    private GLSurfaceView mGLView;
    private MyGLRenderer mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 获取屏幕宽高
        Display display = getWindowManager().getDefaultDisplay();

        screenlist=ScreenCal.screenCal(display.getWidth(), display.getHeight());

        mGLView = new GLSurfaceView(this);
//        mGLView.setEGLContextClientVersion(2);

        setContentView(mGLView);


        mRenderer = new MyGLRenderer();

        mGLView.setRenderer(mRenderer);

        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        gd=new GestureDetector(this,new OnDoubleClick());

    }



    public boolean onTouchEvent(MotionEvent event) {
        mRenderer.onRenderTouch(event.getAction(),event.getX(),event.getY());
        return gd.onTouchEvent(event);
    }



    class OnDoubleClick extends GestureDetector.SimpleOnGestureListener{


        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {  //双击
            if(str.length()>0)
            {
                //如果字已经写到一半了 从头开始
//				if(index != 0) {
//					//直接从新开始写字即可
//					//不用再次形成文字坐标
//					myCanvas.reStart();
//				} else {
                startGame(str,0);//开始写字
//				}
                //从头开始 index 置0
                index=0;

            }
            Log.i("BallActivity", "onDoubleTap");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent event) {//长按
            // TODO Auto-generated method stub
            super.onLongPress(event);
        }
    }


    /**
     * 开始显示字
     * @param str 需要显示的字
     */
    protected void startGame(String str,int position) {
        // TODO Auto-generated method stub
//		System.out.println("正在画第"+index+"个 总共 "+str.length());
        //根据汉字得到对应的点阵
        int[][] data=HZKUtils.readChinese(this,str.charAt(position));

        //结合data修改 screenlist中的点的flag
        for(int i=0,length=data.length;i<length;i++)
        {
            for (int j = 0; j < length; j++) {
                //设置默认值
                screenlist.get(i*length+j).setFalg(false);
                //该点为1 点的状态设为true
                //表示需要将点移动到对应的地方
                if(data[i][j]==1)
                {
                    screenlist.get(i*length+j).setFalg(true);
                }
                else
                {
                    screenlist.get(i*length+j).setFalg(false);
                }
            }
        }
        mRenderer.formChinese(screenlist);

    }


    private boolean running = false;
    private int period = 1000/85;
    /**
     * Maximum gameloops that can happen without a sleep or yield
     */
    public static final int MAX_CONSECUTIVE_LOOPS = 17;

    /**
     * Maximium number of gameUpdates that can occur without gameRenders
     */
    private static final int MAX_SKIP_FRAMES = 5;

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        running = false;

        new Thread(new Runnable() {
            public void run() {
                running = true;
                long beforeTime, afterTime, elapsed, timeToSleep;
                long overSleepTime = 0;
                long excess = 0;
                int noDelays = 0;

                beforeTime = getMillis();
                //lastStatsTime = beforeTime;
                while (running) {

                    mGLView.requestRender(); //请求刷新

                    afterTime = getMillis();
                    elapsed = afterTime - beforeTime; //how long did we take to hander?
                    timeToSleep = (period - elapsed) - overSleepTime; //sleep excess time if we were too fase
                    if (timeToSleep > 0) {
                        try {
                            Thread.sleep(timeToSleep);
                        } catch(InterruptedException ex) {
                        }
                        //did we sleep too much? deduct from next loop
                        overSleepTime = (getMillis() - afterTime) - timeToSleep;
                    } else {
                        excess -= timeToSleep;//how much did we miss the target?
                        //rendering took more than it should! run next loop right away
                        overSleepTime = 0;
                        if (++noDelays >= MAX_CONSECUTIVE_LOOPS) { //consective loops. Better give other thrads a chance to run!
                            Thread.yield();
                            noDelays = 0;
                        }

                    }
                    beforeTime = getMillis();

                    //rendering took to much time! Skip some rendering frames to achieve desired FPU
                    int gameUpdatesWithoutRender = 0;
                    while (excess > period && gameUpdatesWithoutRender < MAX_SKIP_FRAMES) {

                        excess -= period;
                        gameUpdatesWithoutRender++;
                    }
                }
            }
        }).start();
    }


    public static long getMillis() {
        return System.nanoTime() / 1000000;
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        mGLView.onPause();
    }

}