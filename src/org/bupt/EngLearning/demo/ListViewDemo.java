package org.bupt.EngLearning.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewDemo extends Activity {
	private ListView list=null;
	private TextView info=null;
	/* 音乐的路径 */
	private static final String MUSIC_PATH = new String("/sdcard/");
	/* 播放列表 */
	private List<String> mMusicList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.list);
		this.info=(TextView)super.findViewById(R.id.info1);
		this.list=(ListView)super.findViewById(R.id.listview);
		File home = new File(MUSIC_PATH);
		if (home.listFiles(new MusicFilter()).length > 0)
		{
			for (File file : home.listFiles(new MusicFilter()))
			{
				mMusicList.add(file.getName());
			}
		
			this.list.setAdapter(new ArrayAdapter<String>(this,R.layout.listitem,this.mMusicList));
			//super.setContentView(this.list);
			this.list.setOnItemClickListener(new OnItemClickListenerImpl());
		}
		else
			Toast.makeText(ListViewDemo.this, "SD卡中没有找到文件", Toast.LENGTH_SHORT).show();
	}
	//点击待播放选项，跳转准备播放，需要路径
	private class OnItemClickListenerImpl implements OnItemClickListener{

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//Log.i(ALARM_SERVICE, "点击项目");
			Toast.makeText(ListViewDemo.this, "选中了第"+position+"项", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent();
			intent.putExtra("filename", mMusicList.get(position));//filename包含了.mp3???
			intent.setClass(ListViewDemo.this, MediaPlayerDemo.class);
			ListViewDemo.this.startActivity(intent);			
		}}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ( (keyCode == KeyEvent.KEYCODE_BACK)||(keyCode == KeyEvent.KEYCODE_HOME))
		{			
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}   
}
