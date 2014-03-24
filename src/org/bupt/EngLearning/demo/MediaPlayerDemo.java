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
	private int INTERVAL=45;//歌词每行的间隔

	/* 音乐的路径 */
	private static final String MUSIC_PATH = new String("/sdcard/");
	private String filename=new String();//文件名由intent传过来
	//音量控制 线程
    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case PROGRESS_CHANGED:  
                setVolum();  
                break;
           }
        }
    };
  //播放进度控制 线程
    Handler progressHandler = new Handler(){
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case PROGRESS_CHANGED:  
            	if ((myMediaPlayer!=null)&&(myMediaPlayer.isPlaying())) {
					lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
					lyricView.SelectIndex(myMediaPlayer.getCurrentPosition());
					seekbar.setProgress(myMediaPlayer.getCurrentPosition());
					lyricView.invalidate(); // 更新视图调用ondraw()
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
        myMediaPlayer=new MediaPlayer();//实例化！！！
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//音量控制
        //取得组件
        this.info=(TextView)super.findViewById(R.id.info);
        this.lyricView = (CaptionView) findViewById(R.id.mylrc);
        this.pause=(ImageButton)super.findViewById(R.id.pause);
        this.play=(ImageButton)super.findViewById(R.id.play);     
        this.stop=(ImageButton)super.findViewById(R.id.stop);
        this.zoomin=(ImageButton)super.findViewById(R.id.zoomin);
        this.zoomout=(ImageButton)super.findViewById(R.id.zoomout);      
        this.seekbar=(SeekBar)super.findViewById(R.id.seekbar);
        this.volseekbar=(org.bupt.EngLearning.demo.SeekBar)super.findViewById(R.id.volseekBar);        
        //设置监听
        this.play.setOnClickListener(new PlayOnClickListener());
        this.pause.setOnClickListener(new PauseOnClickListener());
        this.stop.setOnClickListener(new StopOnClickListener());
        this.zoomin.setOnClickListener(new zoominOnClickListener());
        this.zoomout.setOnClickListener(new zoomoutOnClickListener());
        
        this.seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImpl());
        this.volseekbar.setOnSeekBarChangeListener(new OnVolChangeListener());
        //歌词设置
        SerchLrc();//搜索歌词
		lyricView.SetTextSize();
		//音量控制的线程
        new Thread(new myVolThread()).start();
        //音频&字幕播放控制的线程
        new Thread(new runable()).start();
    }
    /**
     * 音量设置
     */
    private void setVolum()
    {
        this.volseekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        this.volseekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        //mVolume.setText(currentVolume*100/maxVolume + " ");
    } 
    /**
     * 音量设置的线程声明
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
     * 播放进度和字幕控制的线程声明
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
     *  按下返回按钮时停止播放，释放资源
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
     * 按开始按钮
     * @author KATYLI
     *
     */
    private class PlayOnClickListener implements OnClickListener{
		public void onClick(View v) {
			// TODO Auto-generated method stub		
			try {
				/* 重置MediaPlayer */
				myMediaPlayer.reset();
				/* 设置要播放的文件的路径 */
				myMediaPlayer.setDataSource(MUSIC_PATH + filename);//transfers a MediaPlayer object in the Idle state to the Initialized state
				/* 准备播放 */
				myMediaPlayer.prepare();
				/* 开始播放 */
				myMediaPlayer.start();
				MediaPlayerDemo.this.info.setText("正在播放音频文件...");
				//设置起始时文字的位置
				lyricView.setOffsetY(100 - lyricView.SelectIndex(myMediaPlayer.getCurrentPosition())*(lyricView.getSIZEWORD() + INTERVAL-1)); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				MediaPlayerDemo.this.info.setText("文件播放出现异常，"+e1);
			}	
			//播放音频进度条
			MediaPlayerDemo.this.seekbar.setMax(MediaPlayerDemo.this.myMediaPlayer.getDuration());//设置拖拽条的长度，要mediaplayer实例化后才能设置
			//音量调节进度条,应该是播放开始后才能设置最大值
			MediaPlayerDemo.this.myMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
				//播放结束释放资源
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					Log.v("useMediaPlayer", "播放完毕");
					MediaPlayerDemo.this.info.setText("播放完毕");
					MediaPlayerDemo.this.myMediaPlayer.stop();//改成.pause就可以实现播放完毕后后到任一点播放
				}
			});
		}
    }
    
    
    /**
     * 按停止按钮
     * @author KATYLI
     *
     */
    private class StopOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(MediaPlayerDemo.this.myMediaPlayer!=null){
				MediaPlayerDemo.this.myMediaPlayer.stop();
				MediaPlayerDemo.this.info.setText("停止播放音频文件...");
			}
		}
    }
    /**
     * 按暂停按钮
     * @author KATYLI
     *
     */
    private class PauseOnClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(MediaPlayerDemo.this.myMediaPlayer!=null&&MediaPlayerDemo.this.myMediaPlayer.isPlaying()){
				MediaPlayerDemo.this.myMediaPlayer.pause();
				MediaPlayerDemo.this.info.setText("暂停播放音频文件...");
			}
			else if(MediaPlayerDemo.this.myMediaPlayer!=null&&!MediaPlayerDemo.this.myMediaPlayer.isPlaying()){
				MediaPlayerDemo.this.myMediaPlayer.start();
				MediaPlayerDemo.this.info.setText("正在播放音频文件...");
			}
			else 
				MediaPlayerDemo.this.info.setText("请点开始键开始播放");
		}
    }
    /**
     * 播放进度条
     * @author KATYLI
     *
     */
    private class OnSeekBarChangeListenerImpl implements OnSeekBarChangeListener{

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				myMediaPlayer.seekTo(progress);
				lyricView.setOffsetY(100 - lyricView.SelectIndex(progress) //220是什么？
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
     * 音量控制进度条
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
     * 歌词文件读取&设置
     */
    public void SerchLrc() {
		String lrc = MUSIC_PATH+filename.substring(0, filename.length() - 4).trim()+".lrc".trim();
		//lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
		CaptionView.read(lrc);
		lyricView.SetTextSize();
		lyricView.setOffsetY(350);//起始值，Y偏移最大
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