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
	/* ���ֵ�·�� */
	private static final String MUSIC_PATH = new String("/sdcard/");
	/* �����б� */
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
			Toast.makeText(ListViewDemo.this, "SD����û���ҵ��ļ�", Toast.LENGTH_SHORT).show();
	}
	//���������ѡ���ת׼�����ţ���Ҫ·��
	private class OnItemClickListenerImpl implements OnItemClickListener{

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//Log.i(ALARM_SERVICE, "�����Ŀ");
			Toast.makeText(ListViewDemo.this, "ѡ���˵�"+position+"��", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent();
			intent.putExtra("filename", mMusicList.get(position));//filename������.mp3???
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
