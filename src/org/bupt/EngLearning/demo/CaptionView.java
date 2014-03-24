package org.bupt.EngLearning.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CaptionView extends View{
	
	private static TreeMap<Integer, CaptionObject> lrc_map;
	private float mX;		//屏幕X轴的中点，此值固定，保持歌词在X中间显示
	private float offsetY;		//歌词在Y轴上的偏移量，此值会根据歌词的滚动变小
	private static boolean LrcExist=false;
	private float touchY;	//当触摸歌词View时，保存为当前触点的Y轴坐标
	private float touchX;
	private boolean blScrollView=false;
	private int lrcIndex=0;	//保存歌词TreeMap的下标
	private int SIZEWORD=0;//显示歌词文字的大小值
	private int INTERVAL=45;//歌词每行的间隔
	Paint paint=new Paint();//画笔，用于画不是高亮的歌词
	Paint paintHL=new Paint();	//画笔，用于画高亮的歌词，即当前唱到这句歌词
	private int pos=0;
	public CaptionView(Context context){
		super(context);
		init();
	}
	
	public CaptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if(LrcExist){
			paintHL.setTextSize(SIZEWORD);
			paint.setTextSize(SIZEWORD);
			//画当前歌词
			CaptionObject temp=lrc_map.get(lrcIndex);
			
			for(pos=temp.lrc.length()/2;pos!=0&&(temp.lrc.charAt(pos)!=' ');pos--);//把pos移到句子中间
			canvas.drawText(temp.lrc.substring(0, pos), mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex, paintHL);
			canvas.drawText(temp.lrc.substring(pos), mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex+INTERVAL/3, paintHL);
			// 画当前歌词之前的歌词
			for(int i=lrcIndex-1;i>=0;i--){
				temp=lrc_map.get(i);
				if(offsetY+(SIZEWORD+INTERVAL)*i<0){
					break;
				}
				for(pos=temp.lrc.length()/2;pos!=0&&(temp.lrc.charAt(pos)!=' ');pos--);
				canvas.drawText(temp.lrc.substring(0, pos), mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);
				canvas.drawText(temp.lrc.substring(pos), mX, offsetY+(SIZEWORD+INTERVAL)*i+INTERVAL/3, paint);
			}
			// 画当前歌词之后的歌词
			for(int i=lrcIndex+1;i<lrc_map.size();i++){
				temp=lrc_map.get(i);
				if(offsetY+(SIZEWORD+INTERVAL)*i>600){
					break;
				}
				
				for(pos=temp.lrc.length()/2;pos!=0&&(temp.lrc.charAt(pos)!=' ');pos--);
				canvas.drawText(temp.lrc.substring(0, pos), mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);
				canvas.drawText(temp.lrc.substring(pos), mX, offsetY+(SIZEWORD+INTERVAL)*i+INTERVAL/3, paint);
			}
		}
		else{
			paint.setTextSize(25);
			canvas.drawText("找不到相应文本", mX, 310, paint);
		}
		super.onDraw(canvas);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("bllll==="+blScrollView);
		float tt=event.getY();
		if(!LrcExist){
			//return super.onTouchEvent(event);

			return super.onTouchEvent(event);
		}
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			touchX=event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			touchY=tt-touchY;			
			offsetY=offsetY+touchY;
			break;
		case MotionEvent.ACTION_UP:
			blScrollView=false;
			break;		
		}
		touchY=tt;
		return true;
	}

	public void init(){
		lrc_map = new TreeMap<Integer, CaptionObject>();
		offsetY=320;	
		
		paint=new Paint();
		paint.setTextAlign(Paint.Align.CENTER);//字体居中
		paint.setColor(Color.rgb(50, 255, 88));
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setAlpha(180);
		
		
		paintHL=new Paint();
		paintHL.setTextAlign(Paint.Align.CENTER);
		
		paintHL.setColor(Color.rgb(255, 89, 88));
		paintHL.setAntiAlias(true);
		paintHL.setAlpha(255);
	}
	
	/**
	 * 根据歌词里面最长的那句来确定歌词字体的大小
	 */
	
	public void SetTextSize(){
		if(!LrcExist){
			return;
		}
		int max=lrc_map.get(0).lrc.length();//max是最大字数，中文一个字算一个字符
		for(int i=1;i<lrc_map.size();i++){
			CaptionObject lrcStrLength=lrc_map.get(i);
			if(max<lrcStrLength.lrc.length()){
				max=lrcStrLength.lrc.length();
			}
		}
		SIZEWORD=2000/max;//取商，整数
		//SIZEWORD=20/max;
		System.out.println("++in settextsize>>"+SIZEWORD);
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mX = w * 0.5f;
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	/**
	 *  歌词滚动的速度
	 * 
	 * @return 返回歌词滚动的速度
	 */
	public Float SpeedLrc(){
		float speed=0;
		if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex>220){
			speed=((offsetY+(SIZEWORD+INTERVAL)*lrcIndex-220)/20);

		} else if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex < 120){
			Log.i("speed", "speed is too fast!!!");
			speed = 0;
		}

		return speed;
	}
	
	/**
	 * 按当前的歌曲的播放时间，从歌词里面获得那一句（假设没有一句是相同时间的）
	 * @param time 当前歌曲的播放时间
	 * @return 返回当前歌词的索引值
	 */
	public int SelectIndex(int time){
		if(!LrcExist){
			return 0;
		}
		int index=0;
		for(int i=0;i<lrc_map.size();i++){
			CaptionObject temp=lrc_map.get(i);
			if(temp.begintime<time){
				++index;
			}
		}
		lrcIndex=index-1;
		if(lrcIndex<0){
			lrcIndex=0;
		}
		return lrcIndex;
	
	}
	
	/**
	 * 读取歌词文件，把每句歌词结构化（类化LyricObject）保存在TreeMap结构中
	 * @param file 歌词的路径
	 * 
	 */
	public static void read(String file) {
		TreeMap<Integer, CaptionObject> lrc_read =new TreeMap<Integer, CaptionObject>();//下标为歌词开始时间
		String data = "";
		try {
		  File saveFile=new File(file);//读文件到缓存

		  if(!saveFile.isFile()){
			  LrcExist=false;
			  return;
		  }
		  LrcExist=true;
		  

		  FileInputStream stream = new FileInputStream(saveFile);//  context.openFileInput(file);
		  		  
		  BufferedReader br = new BufferedReader(new InputStreamReader(stream,"GB2312"));   
		  int i = 0;
		  Pattern pattern = Pattern.compile("\\d{2}");//匹配两个数字
		  while ((data = br.readLine()) != null) {   
			  System.out.println("++++++++++++>>"+data);
			    data = data.replace("[","");//将前面的替换成后面的
			    data = data.replace("]","#");
			    String splitdata[] =data.split("#");//分隔
			    if(data.endsWith("#")){//处理只有时间没有文本的句子
			    	for(int k=0;k<splitdata.length;k++){
				    	String str=splitdata[k];
				    	
				    	str = str.replace(":",".");
				    	str = str.replace(".","#");
					    String timedata[] =str.split("#");
					    Matcher matcher = pattern.matcher(timedata[0]);
					    if(timedata.length==3 && matcher.matches()){
						    int m = Integer.parseInt(timedata[0]);  //分
						    int s = Integer.parseInt(timedata[1]);  //秒
						    int ms = Integer.parseInt(timedata[2]); //毫秒
					    	int currTime = (m*60+s)*1000+ms*10;
					    	CaptionObject item1= new CaptionObject();
							item1.begintime = currTime;
							item1.lrc       = "";
							lrc_read.put(currTime,item1);
					    }
			    	}
				    
			    }
			    else{
				    String lrcContenet = splitdata[splitdata.length-1]; 
			
				    for (int j=0;j<splitdata.length-1;j++)//获得时间正常情况splitdata有两段
				    {
					    String tmpstr = splitdata[j];
					    
					    tmpstr = tmpstr.replace(":",".");
					    tmpstr = tmpstr.replace(".","#");
					    String timedata[] =tmpstr.split("#");//timedata有三段分别是分，秒，毫秒
					    Matcher matcher = pattern.matcher(timedata[0]);
					    if(timedata.length==3 && matcher.matches()){//匹配并转成整型数据
						    int m = Integer.parseInt(timedata[0]);  //分
						    int s = Integer.parseInt(timedata[1]);  //秒
						    int ms = Integer.parseInt(timedata[2]); //毫秒
					    	int currTime = (m*60+s)*1000+ms*10;//当前时间的毫秒值
					    	CaptionObject item1= new CaptionObject();
							item1.begintime = currTime;
							item1.lrc       = lrcContenet;//获取本句歌词的当前时间
							lrc_read.put(currTime,item1);// 将currTime当标签  item1当数据 插入TreeMap里
							i++;
					    }
				    }
			    }
			    
		  } 
		 stream.close();
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}
		
		/*
		 * 遍历hashmap 计算每句歌词所需要的时间
		*/
		lrc_map.clear();//每句歌词以类的形式按顺序存入，下标是顺序
		data ="";
		Iterator<Integer> iterator = lrc_read.keySet().iterator();//把时间装入容器
		CaptionObject oldval  = null;
		int i =0;
		while(iterator.hasNext()) {
			Object ob =iterator.next();
			
			CaptionObject val = (CaptionObject)lrc_read.get(ob);
			
			if (oldval==null)//起始状态
				oldval = val;
			else
			{
				CaptionObject item1= new CaptionObject();
				item1  = oldval;
				item1.timeline = val.begintime-oldval.begintime;//下一句开始时间-本句开始时间=本句用时
				lrc_map.put(new Integer(i), item1);
				i++;
				oldval = val;
			}
			if (!iterator.hasNext()) {
				lrc_map.put(new Integer(i), val);
			}
			
		}

	}	
	
	/**
	 * @return the blLrc
	 */
	public static boolean isBlLrc() {
		return LrcExist;
	}

	/**
	 * @return the offsetY
	 */
	public float getOffsetY() {
		return offsetY;
	}

	/**
	 * @param offsetY the offsetY to set
	 */
	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * @return 返回歌词文字的大小
	 */
	public int getSIZEWORD() {
		return SIZEWORD;
	}

	/**
	 * 设置歌词文字的大小
	 * @param sIZEWORD the sIZEWORD to set
	 */
	public void setSIZEWORD(int sIZEWORD) {//参数要以小写字母开头
		SIZEWORD = sIZEWORD;
	}
}
