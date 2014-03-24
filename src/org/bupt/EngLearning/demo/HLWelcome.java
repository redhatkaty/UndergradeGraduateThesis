package org.bupt.EngLearning.demo;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HLWelcome extends Activity {
	private ImageButton start=null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.welcom);
        this.start=(ImageButton)super.findViewById(R.id.stbtn);
        this.start.setOnClickListener(new startOnClickListener());
	}
    private class startOnClickListener implements OnClickListener{

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			
			intent.setClass(HLWelcome.this, ListViewDemo.class);
			HLWelcome.this.startActivity(intent);
		}
    
    }
}
