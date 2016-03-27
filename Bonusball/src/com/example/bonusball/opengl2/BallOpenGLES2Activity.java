package com.example.bonusball.opengl2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bonusball.hzk.HZKUtils;
import com.example.bonusball.hzk.ScreenCal;
import com.example.bonusball.hzk.ScreenPoint;

import java.util.ArrayList;

public class BallOpenGLES2Activity extends Activity implements View.OnClickListener {

    private final static int BALL_NUM=100;//球的数量

    private GestureDetector gd;  //手势监听


    //在屏幕上画一个16*16的点阵 记录所有的坐标
    private ArrayList<ScreenPoint> screenlist;

    EditText inputEt=null;

    private String str="一二三";//显示的字
    private int index=0;

    private Handler handler;

    private GLSurfaceView mGLView;
    private MyGLES2Renderer mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 获取屏幕宽高
        Display display = getWindowManager().getDefaultDisplay();

        screenlist=ScreenCal.screenCal(display.getWidth(), display.getHeight());

        mGLView = new GLSurfaceView(this);
        //使用opengles2.0
        mGLView.setEGLContextClientVersion(2);

        setContentView(mGLView);

        Button button = new Button(this);
        button.setText("点击替换文字");
        button.setOnClickListener(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(button, params);

        initDialog();


        mRenderer = new MyGLES2Renderer();

        mGLView.setRenderer(mRenderer);

        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);



        gd=new GestureDetector(this,new OnDoubleClick());


        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case -1://表示结束

                        break;

                    default:
                        // 让球随机移动2秒之后再写下一个字
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        startGame(str, msg.what);
                        break;
                }
            }

        };


        //字在绘制完时会调用
        mRenderer.setOnCompleteListener(new MyGLES2Renderer.OnCompleteListener() {

            @Override
            public void onComplete() {
                // TODO Auto-generated method stub
                    /*
                 * 画完了字
                 * 开始画下一个
                 * index画了第几个
                 */

                if(str.length()>1&&index!=(str.length()-1))//不止一个字
                {
                    index++;
                    handler.sendEmptyMessage(index);
                }
                else
                {
                    index=0;//记录g归0
                }
                if(index == str.length()) {
                    handler.sendEmptyMessage(-1);
                }
                System.out.println("画完了 第"+index+"个 总共 "+str.length());


            }
        });


    }


    //弹框
    AlertDialog dialog;


    private void initDialog() {

        //初始化输入框
        inputEt=new EditText(this);
        inputEt.setHint("请输入");

        dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请输入想要显示的字，，，不要太多")
                .setView(inputEt)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int which) {
                        String tem=inputEt.getText().toString();

                        //没有输入汉字时  提示一下
                        if(tem.length()<1)
                        {
                            Toast.makeText(BallOpenGLES2Activity.this, "没有输入任何汉字！", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            str=tem;//修改本地存储的值
                            startGame(str,0);
                            index=0;
                        }
                    }

                }).show();
        dialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        showInputDialog();//弹出输入提示
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        showInputDialog();//弹出输入提示


    }

    public void showInputDialog() {
        if(dialog != null) {
            dialog.show();
        }
    }




    public boolean onTouchEvent(MotionEvent event) {
        mRenderer.onRenderTouch(event.getAction(), event.getX(),event.getY());
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