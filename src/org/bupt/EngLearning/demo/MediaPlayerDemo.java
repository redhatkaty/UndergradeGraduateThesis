package org.bupt.EngLearning.demo;


import java.io.IOException;
import org.bupt.EngLearning.demo.CaptionView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MediaPlayerDemo extends Activity {
    /** Called when the activity is first created. */
	protected static final int PROGRESS_CHANGED = 0x101;
	private ImageButton zoomin=null;
	private ImageButton zoomout=null;
	private ImageButton play=null;
	private ImageButton pause=null;
	private ImageButton stop=null;
	private TextView info=null;
	private MediaPlayer myMediaPlayer=null;
	private SeekBar seekbar=null;
	private org.bupt.EngLearning.demo.SeekBar volseekbar=null;
	private AudioManager mAudioManager=null;	
	private CaptionView lyricView;
	private int INTERVAL=45;//���ÿ�еļ��

	/* ���ֵ�·�� */
	private static final String MUSIC_PATH = new String("/sdcard/");
	private String filename=new String();//�ļ�����intent������
	//�������� �߳�
    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case PROGRESS_CHANGED:  
                setVolum();  
                break;
           }
        }
    };
  //���Ž��ȿ��� �߳�
    Handler progressHandler = new Handler(){
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case PROGRESS_CHANGED:  
            	if ((myMediaPlayer!=null)&&(myMediaPlayer.isPlaying())) {
					lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
					lyricView.SelectIndex(myMediaPlayer.getCurrentPosition());
					seekbar.setProgress(myMediaPlayer.getCurrentPosition());
					lyricView.invalidate(); // ������ͼ����ondraw()
				} 
                break;
           }
        }
    };
	
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);
        Intent intent=getIntent();
        filename=intent.getStringExtra("filename");
        Toast.makeText(MediaPlayerDemo.this, "filename:"+MUSIC_PATH+filename, Toast.LENGTH_SHORT).show();
        myMediaPlayer=new MediaPlayer();//ʵ����������
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//��������
        //ȡ�����
        this.info=(TextView)super.findViewById(R.id.info);
        this.lyricView = (CaptionView) findViewById(R.id.mylrc);
        this.pause=(ImageButton)super.findViewById(R.id.pause);
        this.play=(ImageButton)super.findViewById(R.id.play);     
        this.stop=(ImageButton)super.findViewById(R.id.stop);
        this.zoomin=(ImageButton)super.findViewById(R.id.zoomin);
        this.zoomout=(ImageButton)super.findViewById(R.id.zoomout);      
        this.seekbar=(SeekBar)super.findViewById(R.id.seekbar);
        this.volseekbar=(org.bupt.EngLearning.demo.SeekBar)super.findViewById(R.id.volseekBar);        
        //���ü���
        this.play.setOnClickListener(new PlayOnClickListener());
        this.pause.setOnClickListener(new PauseOnClickListener());
        this.stop.setOnClickListener(new StopOnClickListener());
        this.zoomin.setOnClickListener(new zoominOnClickListener());
        this.zoomout.setOnClickListener(new zoomoutOnClickListener());
        
        this.seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImpl());
        this.volseekbar.setOnSeekBarChangeListener(new OnVolChangeListener());
        //�������
        SerchLrc();//�������
		lyricView.SetTextSize();
		//�������Ƶ��߳�
        new Thread(new myVolThread()).start();
        //��Ƶ&��Ļ���ſ��Ƶ��߳�
        new Thread(new runable()).start();
    }
    /**
     * ��������
     */
    private void setVolum()
    {
        this.volseekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        this.volseekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        //mVolume.setText(currentVolume*100/maxVolume + " ");
    } 
    /**
     * �������õ��߳�����
     * @author KATYLI
     *
     */
    class myVolThread implements Runnable {   
        public void run() {  
             while (!Thread.currentThread().isInterrupted()) {    
                      
                  Message message = new Message();   
                  message.what =PROGRESS_CHANGED;   
                  MediaPlayerDemo.this.myHandler.sendMessage(message);   
                  try {   
                       Thread.sleep(100);    
                  } catch (InterruptedException e) {   
                       Thread.currentThread().interrupt();   
                  }   
             }   
        }   
   } 
    /**
     * ���Ž��Ⱥ���Ļ���Ƶ��߳�����
     * @author KATYLI
     *
     */ 
    class runable implements Runnable {   
        public void run() {  
             while (!Thread.currentThread().isInterrupted()) {    
                      
                  Message message = new Message();   
                  message.what =PROGRESS_CHANGED;   
                  MediaPlayerDemo.this.progressHandler.sendMessage(message);   
                  try {   
                       Thread.sleep(100);    
                  } catch (InterruptedException e) {   
                       Thread.currentThread().interrupt();   
                  }   
             }   
        }   
   } 
    /**
     *  ���·��ذ�ťʱֹͣ���ţ��ͷ���Դ
     * (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ( keyCode ==  KeyEvent.KEYCODE_BACK)
		{
			myMediaPlayer.stop();
			myMediaPlayer.release();
			myMediaPlayer=null;
			this.finish();//Call this when your activity is done and should be closed
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}   
    /**
     * ����ʼ��ť
     * @author KATYLI
     *
     */
    private class PlayOnClickListener implements OnClickListener{
		public void onClick(View v) {
			// TODO Auto-generated method stub		
			try {
				/* ����MediaPlayer */
				myMediaPlayer.reset();
				/* ����Ҫ���ŵ��ļ���·�� */
				myMediaPlayer.setDataSource(MUSIC_PATH + filename);//transfers a MediaPlayer object in the Idle state to the Initialized state
				/* ׼������ */
				myMediaPlayer.prepare();
				/* ��ʼ���� */
				myMediaPlayer.start();
				MediaPlayerDemo.this.info.setText("���ڲ�����Ƶ�ļ�...");
				//������ʼʱ���ֵ�λ��
				lyricView.setOffsetY(100 - lyricView.SelectIndex(myMediaPlayer.getCurrentPosition())*(lyricView.getSIZEWORD() + INTERVAL-1)); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				MediaPlayerDemo.this.info.setText("�ļ����ų����쳣��"+e1);
			}	
			//������Ƶ������
			MediaPlayerDemo.this.seekbar.setMax(MediaPlayerDemo.this.myMediaPlayer.getDuration());//������ק���ĳ��ȣ�Ҫmediaplayerʵ�������������
			//�������ڽ�����,Ӧ���ǲ��ſ�ʼ������������ֵ
			MediaPlayerDemo.this.myMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
				//���Ž����ͷ���Դ
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					Log.v("useMediaPlayer", "�������");
					MediaPlayerDemo.this.info.setText("�������");
					MediaPlayerDemo.this.myMediaPlayer.stop();//�ĳ�.pause�Ϳ���ʵ�ֲ�����Ϻ����һ�㲥��
				}
			});
		}
    }
    
    
    /**
     * ��ֹͣ��ť
     * @author KATYLI
     *
     */
    private class StopOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(MediaPlayerDemo.this.myMediaPlayer!=null){
				MediaPlayerDemo.this.myMediaPlayer.stop();
				MediaPlayerDemo.this.info.setText("ֹͣ������Ƶ�ļ�...");
			}
		}
    }
    /**
     * ����ͣ��ť
     * @author KATYLI
     *
     */
    private class PauseOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(MediaPlayerDemo.this.myMediaPlayer!=null&&MediaPlayerDemo.this.myMediaPlayer.isPlaying()){
				MediaPlayerDemo.this.myMediaPlayer.pause();
				MediaPlayerDemo.this.info.setText("��ͣ������Ƶ�ļ�...");
			}
			else if(MediaPlayerDemo.this.myMediaPlayer!=null&&!MediaPlayerDemo.this.myMediaPlayer.isPlaying()){
				MediaPlayerDemo.this.myMediaPlayer.start();
				MediaPlayerDemo.this.info.setText("���ڲ�����Ƶ�ļ�...");
			}
			else 
				MediaPlayerDemo.this.info.setText("��㿪ʼ����ʼ����");
		}
    }
    /**
     * ���Ž�����
     * @author KATYLI
     *
     */
    private class OnSeekBarChangeListenerImpl implements OnSeekBarChangeListener{

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				myMediaPlayer.seekTo(progress);
				lyricView.setOffsetY(100 - lyricView.SelectIndex(progress) //220��ʲô��
						* (lyricView.getSIZEWORD() + INTERVAL-1));

			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
    }
    /**
     * �������ƽ�����
     * @author KATYLI
     *
     */
    private  class OnVolChangeListener implements org.bupt.EngLearning.demo.SeekBar.OnSeekBarChangeListener{

		public void onProgressChanged(
				org.bupt.EngLearning.demo.SeekBar VerticalSeekBar,
				int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
		}

		public void onStartTrackingTouch(
				org.bupt.EngLearning.demo.SeekBar VerticalSeekBar) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(
				org.bupt.EngLearning.demo.SeekBar VerticalSeekBar) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    /**
     * ����ļ���ȡ&����
     */
    public void SerchLrc() {
		String lrc = MUSIC_PATH+filename.substring(0, filename.length() - 4).trim()+".lrc".trim();
		//lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
		CaptionView.read(lrc);
		lyricView.SetTextSize();
		lyricView.setOffsetY(350);//��ʼֵ��Yƫ�����
	}
    
    private class zoominOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			lyricView.setSIZEWORD(lyricView.getSIZEWORD()+2);
		}
    }
    
    private class zoomoutOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			lyricView.setSIZEWORD(lyricView.getSIZEWORD()-2);
		}
    }
}