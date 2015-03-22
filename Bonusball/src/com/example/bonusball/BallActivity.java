package com.example.bonusball;  
  
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bonusball.CanvasView.OnCompleteListener;
import com.example.bonusball.hzk.HZKUtils;
import com.example.bonusball.hzk.ScreenCal;
import com.example.bonusball.hzk.ScreenPoint;
public class BallActivity extends Activity {  
  
	private final static int BALL_NUM=100;//�������
	
	private GestureDetector gd;  //���Ƽ���
	
    private CanvasView myCanvas;  
    //����Ļ�ϻ�һ��16*16�ĵ��� ��¼���е�����
    private ArrayList<ScreenPoint> screenlist;
  
    EditText inputEt=null;
    
    private String str="һ����";//��ʾ����
    private int index=0;
    
    private Handler handler;
    
    //����
    AlertDialog dialog;
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        
        // ��ȡ��Ļ���
     	Display display = getWindowManager().getDefaultDisplay();
     	
     	screenlist=ScreenCal.screenCal(display.getWidth(), display.getHeight());
     	
        myCanvas=new CanvasView(this,display.getWidth(),display.getHeight());
        
        setContentView(myCanvas); 
        
        initDialog();
        
        handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case -1://��ʾ����
					
					break;

				default:
					/**
					 * ��������ƶ�2��֮����д��һ����
					 */
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
        
        
        gd=new GestureDetector(this,new OnDoubleClick());  
        
        /*
         * ��ʼ��ָ����������
         */
        for(int i=0;i<BALL_NUM;i++)
        	myCanvas.fireBall(); 
        
        
        //���ڻ�����ʱ�����
        myCanvas.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				/**
				 * ��������
				 * ��ʼ����һ��
				 * index���˵ڼ���
				 */
				if(str.length()>1&&index!=(str.length()-1))//��ֹһ����
				{
					index++;
					handler.sendEmptyMessage(index);
				}
				else
				{
					index=0;//��¼g��0
				}
				if(index == str.length()) {
					handler.sendEmptyMessage(-1);
				}
				System.out.println("������ ��"+index+"�� �ܹ� "+str.length());
				
				
			}
		});
    }  
    private void initDialog() {
    	
    	 //��ʼ�������
        inputEt=new EditText(this);
        inputEt.setHint("������");
    	
    	dialog = new AlertDialog.Builder(this)
		.setTitle("��ʾ")
		.setMessage("��������Ҫ��ʾ���֣�������Ҫ̫��")
		.setView(inputEt)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				String tem=inputEt.getText().toString();
				
				//û�����뺺��ʱ  ��ʾһ��
				if(tem.length()<1)
				{
					Toast.makeText(BallActivity.this, "û�������κκ��֣�", Toast.LENGTH_SHORT).show();
				}
				else
				{
					str=tem;//�޸ı��ش洢��ֵ
					startGame(str,0);
					index=0;
				}
			}

		}).show();
		dialog.dismiss();
	}

	/**
     * ��ʼ��ʾ��
     * @param string ��Ҫ��ʾ����
     */
	protected void startGame(String str,int position) {
		// TODO Auto-generated method stub
//		System.out.println("���ڻ���"+index+"�� �ܹ� "+str.length());
		//���ݺ��ֵõ���Ӧ�ĵ���
    	int[][] data=HZKUtils.readChinese(this,str.charAt(position));
    	
    	//���data�޸� screenlist�еĵ��flag
    	for(int i=0,length=data.length;i<length;i++)
        {
        	for (int j = 0; j < length; j++) {
        		//����Ĭ��ֵ
        		screenlist.get(i*length+j).setFalg(false);
        		//�õ�Ϊ1 ���״̬��Ϊtrue
        		//��ʾ��Ҫ�����ƶ�����Ӧ�ĵط�
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
    	myCanvas.formChinese(screenlist);
		
	}
    
    @Override  
    public boolean onTouchEvent(MotionEvent event)  
    {  
    	
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			myCanvas.isMouseDown=true;
			Log.i("BallActivity", "MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			
			myCanvas.isMouseDown=false;
			
			float x=event.getX();
	    	float y=event.getY();
	    	
			myCanvas.mouseX=(int)x;
	    	myCanvas.mouseY=(int)y;
	    	break;
		case MotionEvent.ACTION_UP:
			myCanvas.isMouseDown=false;
			Log.i("BallActivity", "MotionEvent.ACTION_UP");
			break;
		default:
			break;
		}
        return gd.onTouchEvent(event);   //����˫�� ����
    }  
    class OnDoubleClick extends GestureDetector.SimpleOnGestureListener{  
    	
    
		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return false;
		}

		@Override  
        public boolean onDoubleTap(MotionEvent e) {  //˫��
			if(str.length()>0)
			{
				//������Ѿ�д��һ���� ��ͷ��ʼ
//				if(index != 0) {
//					//ֱ�Ӵ��¿�ʼд�ּ���
//					//�����ٴ��γ���������
//					myCanvas.reStart();
//				} else {
					startGame(str,0);//��ʼд��
//				}
				//��ͷ��ʼ index ��0
				index=0;
				
			}
			Log.i("BallActivity", "onDoubleTap");
            return false;  
        }

		@Override
		public void onLongPress(MotionEvent event) {//����
			// TODO Auto-generated method stub
			super.onLongPress(event);
		}  
    }  
    



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		myCanvas.clear();
		Log.i("BallActivity", "onDestroy");
	}  
    
    
    
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        getMenuInflater().inflate(R.menu.main, menu);  
        
        return true;  
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		showInputDialog();//����������ʾ
		return super.onOptionsItemSelected(item);
	}  
    
	public void showInputDialog() {
		if(dialog != null) {
			dialog.show();
		}
	}
	
}  