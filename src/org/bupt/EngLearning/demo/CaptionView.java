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
	private float mX;		//��ĻX����е㣬��ֵ�̶������ָ����X�м���ʾ
	private float offsetY;		//�����Y���ϵ�ƫ��������ֵ����ݸ�ʵĹ�����С
	private static boolean LrcExist=false;
	private float touchY;	//���������Viewʱ������Ϊ��ǰ�����Y������
	private float touchX;
	private boolean blScrollView=false;
	private int lrcIndex=0;	//������TreeMap���±�
	private int SIZEWORD=0;//��ʾ������ֵĴ�Сֵ
	private int INTERVAL=45;//���ÿ�еļ��
	Paint paint=new Paint();//���ʣ����ڻ����Ǹ����ĸ��
	Paint paintHL=new Paint();	//���ʣ����ڻ������ĸ�ʣ�����ǰ���������
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
			//����ǰ���
			CaptionObject temp=lrc_map.get(lrcIndex);
			
			for(pos=temp.lrc.length()/2;pos!=0&&(temp.lrc.charAt(pos)!=' ');pos--);//��pos�Ƶ������м�
			canvas.drawText(temp.lrc.substring(0, pos), mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex, paintHL);
			canvas.drawText(temp.lrc.substring(pos), mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex+INTERVAL/3, paintHL);
			// ����ǰ���֮ǰ�ĸ��
			for(int i=lrcIndex-1;i>=0;i--){
				temp=lrc_map.get(i);
				if(offsetY+(SIZEWORD+INTERVAL)*i<0){
					break;
				}
				for(pos=temp.lrc.length()/2;pos!=0&&(temp.lrc.charAt(pos)!=' ');pos--);
				canvas.drawText(temp.lrc.substring(0, pos), mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);
				canvas.drawText(temp.lrc.substring(pos), mX, offsetY+(SIZEWORD+INTERVAL)*i+INTERVAL/3, paint);
			}
			// ����ǰ���֮��ĸ��
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
			canvas.drawText("�Ҳ�����Ӧ�ı�", mX, 310, paint);
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
		paint.setTextAlign(Paint.Align.CENTER);//�������
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
	 * ���ݸ����������Ǿ���ȷ���������Ĵ�С
	 */
	
	public void SetTextSize(){
		if(!LrcExist){
			return;
		}
		int max=lrc_map.get(0).lrc.length();//max���������������һ������һ���ַ�
		for(int i=1;i<lrc_map.size();i++){
			CaptionObject lrcStrLength=lrc_map.get(i);
			if(max<lrcStrLength.lrc.length()){
				max=lrcStrLength.lrc.length();
			}
		}
		SIZEWORD=2000/max;//ȡ�̣�����
		//SIZEWORD=20/max;
		System.out.println("++in settextsize>>"+SIZEWORD);
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mX = w * 0.5f;
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	/**
	 *  ��ʹ������ٶ�
	 * 
	 * @return ���ظ�ʹ������ٶ�
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
	 * ����ǰ�ĸ����Ĳ���ʱ�䣬�Ӹ����������һ�䣨����û��һ������ͬʱ��ģ�
	 * @param time ��ǰ�����Ĳ���ʱ��
	 * @return ���ص�ǰ��ʵ�����ֵ
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
	 * ��ȡ����ļ�����ÿ���ʽṹ�����໯LyricObject��������TreeMap�ṹ��
	 * @param file ��ʵ�·��
	 * 
	 */
	public static void read(String file) {
		TreeMap<Integer, CaptionObject> lrc_read =new TreeMap<Integer, CaptionObject>();//�±�Ϊ��ʿ�ʼʱ��
		String data = "";
		try {
		  File saveFile=new File(file);//���ļ�������

		  if(!saveFile.isFile()){
			  LrcExist=false;
			  return;
		  }
		  LrcExist=true;
		  

		  FileInputStream stream = new FileInputStream(saveFile);//  context.openFileInput(file);
		  		  
		  BufferedReader br = new BufferedReader(new InputStreamReader(stream,"GB2312"));   
		  int i = 0;
		  Pattern pattern = Pattern.compile("\\d{2}");//ƥ����������
		  while ((data = br.readLine()) != null) {   
			  System.out.println("++++++++++++>>"+data);
			    data = data.replace("[","");//��ǰ����滻�ɺ����
			    data = data.replace("]","#");
			    String splitdata[] =data.split("#");//�ָ�
			    if(data.endsWith("#")){//����ֻ��ʱ��û���ı��ľ���
			    	for(int k=0;k<splitdata.length;k++){
				    	String str=splitdata[k];
				    	
				    	str = str.replace(":",".");
				    	str = str.replace(".","#");
					    String timedata[] =str.split("#");
					    Matcher matcher = pattern.matcher(timedata[0]);
					    if(timedata.length==3 && matcher.matches()){
						    int m = Integer.parseInt(timedata[0]);  //��
						    int s = Integer.parseInt(timedata[1]);  //��
						    int ms = Integer.parseInt(timedata[2]); //����
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
			
				    for (int j=0;j<splitdata.length-1;j++)//���ʱ���������splitdata������
				    {
					    String tmpstr = splitdata[j];
					    
					    tmpstr = tmpstr.replace(":",".");
					    tmpstr = tmpstr.replace(".","#");
					    String timedata[] =tmpstr.split("#");//timedata�����ηֱ��Ƿ֣��룬����
					    Matcher matcher = pattern.matcher(timedata[0]);
					    if(timedata.length==3 && matcher.matches()){//ƥ�䲢ת����������
						    int m = Integer.parseInt(timedata[0]);  //��
						    int s = Integer.parseInt(timedata[1]);  //��
						    int ms = Integer.parseInt(timedata[2]); //����
					    	int currTime = (m*60+s)*1000+ms*10;//��ǰʱ��ĺ���ֵ
					    	CaptionObject item1= new CaptionObject();
							item1.begintime = currTime;
							item1.lrc       = lrcContenet;//��ȡ�����ʵĵ�ǰʱ��
							lrc_read.put(currTime,item1);// ��currTime����ǩ  item1������ ����TreeMap��
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
		 * ����hashmap ����ÿ��������Ҫ��ʱ��
		*/
		lrc_map.clear();//ÿ�����������ʽ��˳����룬�±���˳��
		data ="";
		Iterator<Integer> iterator = lrc_read.keySet().iterator();//��ʱ��װ������
		CaptionObject oldval  = null;
		int i =0;
		while(iterator.hasNext()) {
			Object ob =iterator.next();
			
			CaptionObject val = (CaptionObject)lrc_read.get(ob);
			
			if (oldval==null)//��ʼ״̬
				oldval = val;
			else
			{
				CaptionObject item1= new CaptionObject();
				item1  = oldval;
				item1.timeline = val.begintime-oldval.begintime;//��һ�俪ʼʱ��-���俪ʼʱ��=������ʱ
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
	 * @return ���ظ�����ֵĴ�С
	 */
	public int getSIZEWORD() {
		return SIZEWORD;
	}

	/**
	 * ���ø�����ֵĴ�С
	 * @param sIZEWORD the sIZEWORD to set
	 */
	public void setSIZEWORD(int sIZEWORD) {//����Ҫ��Сд��ĸ��ͷ
		SIZEWORD = sIZEWORD;
	}
}
