package com.example.qrcodedatastreamingmodified;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.qrcodedatastreamingmodified.R.id;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.ImageView;


@SuppressLint("NewApi")
public class MainActivity extends Activity implements SurfaceHolder.Callback{
	public String[] s=new String[10];
	public ImageView iv;
	public Bitmap[] b=new Bitmap[10];
	public QRCodeReader qrr=new QRCodeReader();
	public QRCodeWriter qw=new QRCodeWriter();
	public boolean isin=false;
	private static final int WHITE = 0xFFFFFFFF;
	  private static final int BLACK = 0xFF000000;
	  public BinaryBitmap bmtobedecoded;
    public void surfaceCreated(SurfaceHolder holder)
   
    {
    	mCamera = Camera.open(1);
        try {
            Log.i(TAG, "SurfaceHolder.Callback：surface Created");
            
            mCamera.setPreviewDisplay(mSurfaceHolder);// set the surface to be
                                                        // used for live preview

        } catch (Exception ex) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            Log.i(TAG, "initCamera" + ex.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    
    {
       
        Log.i(TAG, "SurfaceHolder.Callback：Surface Changed");
        initCamera();
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
        if (null != mCamera) {
            mCamera.setPreviewCallback(null); 
            mCamera.stopPreview();
            bIfPreview = false;
            mCamera.release();
            mCamera = null;
        }
    }
     
    private void initCamera()
    {
        Log.i(TAG, "going into initCamera");
        if (bIfPreview) {
            mCamera.stopPreview();
        }

        if (null != mCamera) {
            try {
               
                Camera.Parameters parameters = mCamera.getParameters();
              
                parameters.setPictureFormat(PixelFormat.JPEG); 
                parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP); 
                
                List<Size> pictureSizes = mCamera.getParameters()
                        .getSupportedPictureSizes();
                List<Size> previewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                List<Integer> previewFormats = mCamera.getParameters()
                        .getSupportedPreviewFormats();
                List<Integer> previewFrameRates = mCamera.getParameters()
                        .getSupportedPreviewFrameRates();
                Log.i(TAG, "initCamera cyy support parameters is ");
                Size psize = null;
                for (int i = 0; i < pictureSizes.size(); i++) {
                    psize = pictureSizes.get(i);
                    Log.i(TAG, "initCamera  PictrueSize,width: "
                            + psize.width + " height" + psize.height);
                }
                for (int i = 0; i < previewSizes.size(); i++) {
                    psize = previewSizes.get(i);
                    Log.i(TAG, "initCamera PreviewSize,width: "
                            + psize.width + " height" + psize.height);
                }
                Integer pf = null;
                for (int i = 0; i < previewFormats.size(); i++) {
                    pf = previewFormats.get(i);
                    Log.i(TAG, "initCamera previewformates:" + pf);
                }

                
              
                parameters.setPictureSize(800, 800);
                parameters.setPreviewSize(mPreviewWidth, mPreviewHeight); 
                

                if (this.getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait"); 
                    parameters.set("rotation", 90); 
                    mCamera.setDisplayOrientation(90); 
                } else
                {
                    parameters.set("orientation", "landscape");
                    mCamera.setDisplayOrientation(0); 
                }

               
                mCamera.setPreviewCallback(mJpegPreviewCallback);

                
                mCamera.setParameters(parameters); 
                mCamera.startPreview(); 
                
                bIfPreview = true;

               
                Camera.Size csize = mCamera.getParameters().getPreviewSize();
                mPreviewHeight = csize.height; //
                mPreviewWidth = csize.width;
                Log.i(TAG, "initCamera after setting, previewSize:width: "
                        + csize.width + " height: " + csize.height);
                csize = mCamera.getParameters().getPictureSize();
                Log.i(TAG, "initCamera after setting, pictruesize:width: "
                        + csize.width + " height: " + csize.height);
                Log.i(TAG, "initCamera after setting, previewformate is "
                        + mCamera.getParameters().getPreviewFormat());
                Log.i(TAG, "initCamera after setting, previewFrameRate is "
                        + mCamera.getParameters().getPreviewFrameRate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
     
     
   
    PreviewCallback mJpegPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        	
          //  ImageView iv = (ImageView)findViewById(R.id.imageView1);
          //  ByteArrayOutputStream out = new ByteArrayOutputStream();
          //  YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
          //  yuvImage.compressToJpeg(new Rect(0, 0, width, height), 50, out);
          //  byte[] imageBytes = out.toByteArray();
          //  Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
          //  iv.setImageBitmap(image);
            
        	int imageWidth = camera.getParameters().getPreviewSize().width  ;
            int imageHeight =camera.getParameters().getPreviewSize().height ;
            int RGBData[] = new int[imageWidth* imageHeight]; 
            byte[] mYUVData = new byte[data.length];    
            System.arraycopy(data, 0, mYUVData, 0, data.length);
            decodeYUV420SP(RGBData, mYUVData, imageWidth, imageHeight);

            /*Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(RGBData, 0, imageWidth, 0, 0, imageWidth, imageHeight);
            if(bitmap==null)
            	Log.i("time1","null");
            bmtobedecoded=bitmap;*/
            LuminanceSource source = new RGBLuminanceSource(imageWidth,imageHeight,RGBData);
      	    bmtobedecoded = new BinaryBitmap(new HybridBinarizer(source));
      	  try {
    		  Result result;
			result=qrr.decode(bmtobedecoded);
			Log.i("time1","The result is "+result.toString());
		} catch (NotFoundException e) {
			Log.i("time1","qr not found");
		} catch (ChecksumException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }

    };
    
    // print byte[] for test only
    public static void printHexString( byte[] b) { 
    	for (int i = 0; i < b.length; i++) { 
    	String hex = Integer.toHexString(b[i] & 0xFF); 
    	if (hex.length() == 1) { 
    	hex = '0' + hex; 
    	} 
    	Log.i("HEX::",hex.toUpperCase());
    	//System.out.print(hex.toUpperCase() ); 
    	} 

    	} 

    
    // Change YUV420SP to RGB
    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        for (int i = 0; i < width; i++, yp++) {
            int y = (0xff & ((int) yuv420sp[yp])) - 16;
            if (y < 0) y = 0;
            if ((i & 1) == 0) {
                v = (0xff & yuv420sp[uvp++]) - 128;
                u = (0xff & yuv420sp[uvp++]) - 128;
            }

            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        }
    }
    }
    
    
 // InitSurfaceView
    private void initSurfaceView() {
        mSurfaceview = (SurfaceView) this.findViewById(R.id.Surfaceview);
        mSurfaceHolder = mSurfaceview.getHolder();
        mSurfaceHolder.addCallback(MainActivity.this); 
        // mSurfaceHolder.setFixedSize(176, 144);
     
    }
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSurfaceView();
        iv=(ImageView) this.findViewById(id.qrcode_image);
s[0]="Money causes teenagers to feel stress. It makes them feel bad about themselves and envy other people. My friend, for instance, lives with her family and has to share a room with her sister, who is very cute and intelligent. This girl wishes she could have her own room and have a lot of stuff, but she can’t have these things because her family doesn’t have much money. Her family’s income is pretty low because her father is old and doesn’t go to work. Her sister is the only one who works. Because her family can’t buy her the things she wants, she feels a lot of stress and gets angry sometimes. Once, she wanted a beautiful dress to wear to a sweetheart dance. She asked her sister for some money to buy the dress. She was disappointed because her sister didn’t have money to give her. She sat in silence for a little while and then started yelling out loud. She said her friends got anything they wanted but she didn’t. Then she felt sorry for herself and asked why she was born into a poor family. Not having money has caused this girl to think negatively about herself and her family. It has caused a lot of stress in her life.";
        
        s[1]="Note how the first sentence, My hometown, Wheaton, is famous for several amazing geographical features,is the most general statement. This sentence is different from the two sentences that follow it, since the second and third sentences mention specific details about the town's geography, and are not general statements.Money causes teenagers to feel stress. It makes them feel bad about themselves and envy other people. My friend, for instance, lives with her family and has to share a room with her sister, who is very cute and intelligent. This girl wishes she could have her own room and have a lot of stuff, but she can’t have these things because her family doesn’t have much money. Her family’s income is pretty low because her father is old and doesn’t go to work. Her sister is the only one who works. Because her family can’t buy her the things she wants, she feels a lot of stress and gets angry sometimes. Once, she wanted a beautiful dress to wear to a sweetheart dance. She asked her sister for some money to buy the dress. She was disappointed because her sister didn’t have money to give her. She sat in silence for a little while and then started yelling out loud. She said her friendsMoney causes teenagers to feel stress. It makes them feel bad about themselves and envy other people. My friend, for instance, lives with her family and has to share a room with her sister, who is very cute and intelligent. This girl wishes she could have her own room and have a lot of stuff, but she can’t have these things because her family doesn’t have much money. Her family’s income is pretty low because her father is old and doesn’t go to work. Her sister is the only one who works. Because her family can’t buy her the things she wants, she feels a lot of stress and gets angry sometimes. Once, she wanted a beautiful dress to wear to a sweetheart dance. She asked her sister for some money to buy the dress. She was disappointed because her sister didn’t have money to give her. She sat in silence for a little while and then started yelling out loud. She said her friends";
        
        s[2]="Newspapers in India are classified into two categories according to the amount and completeness of information in them. Newspapers in the first category have more information and truth. Those in the second category do not have much information and sometimes they hide the truth. Newspapers in the first category have news collected from different parts of the country and also from different countries. They also have a lot of sports and business news and classified ads. The information they give is clear and complete and it is supported by showing pictures. The best know example of this category is the Indian Express. Important news goes on the first page with big headlines, photographs from different angles, and complete information. For example, in 1989-90, the Indian prime minister, Rajive Ghandi, was killed by a terrorist using a bomb. This newspaper investigated the situation and gave information that helped the CBI to get more support. They also showed diagrams of the area where the prime minister was killed and the positions of the bodies after the attack. This helped the reader understand what happened. Unlike newspaper in the first category, newspapers in the second category do not give as much information. They do not have international news, sports, or business news and they do not have classified ads. Also, the news they give is not complete. For example, the newspaper Hindi gave news on the death of the prime minister, but the news was not complete. The newspaper didn’t investigate the terrorist group or try to find out why this happened. Also, it did not show any pictures from the attack or give any news the next day. It just gave the news when it happened, but it didn’t follow up. Therefore, newspapers in the first group are more popular than those in the second group.";
        
        s[3]="Most students like the freedom they have in college. Usually college students live on their own, in the dormitory or in an apartment. This means they are free to come and go as they like. Their parents can’t tell them when to get up, when to go to school, and when to come home. It also means that they are free to wear what they want. There are no parents to comment about their hair styles or their dirty jeans. Finally, they are free to listen to their favorite music without interference from parents.";
        s[4]=" California is the most wonderful place to visit because of its variety of weather and its beautiful nature. (subject development) Visitors to California can find any weather they like. They can find cool temperatures in the summer; also they can find warm weather in the winter. They can find places that are difficult for humans to live in the summer because they are so hot. Or they can find places closed in the winter because of the snow. On the other hand, visitors can find the nature they like. They can find high mountains and low valleys. Visitors can find a huge forest, a dead desert, and a beautiful coast.(summary sentence) So California is the most wonderful place to visit because of its weather and natureOn the other hand, visitors can find the nature they like. They can find high mountains and low valleys. Visitors can find a huge forest, a dead desert, and a beautiful coast.(summary sentence) So California is the most wonderful place to visit because of its weather and natur.";
        s[5]="The first thing we did as soon as we came to the U.S.A. about two years ago was to search for an apartment in order not to live with one of our relatives. After looking for one month to find a suitable apartment, I finally found the apartment where we have been living. It includes a living room three bedrooms, and a kitchen. Probably the living room is my favorite room of all because we often gather together there after we come home from work or school. It is a comfortable room for our family. Entering the living room from the front door, we can we a new piano in the corner, with a vase of colorful flowers on it. In the opposite corner stands a Sony television, which I bought for my children to watch cartoons and for us to see films and get the daily news. Besides, there is a sofa next to the piano, a loveseat beside the TV, and also a low table between them. This is a comfortable place to sit while we watch TV or talk. On one of the light blue walls is a tranquil picture of the sea. The floor is covered with a dark red carpet, which my children like to play on. They also like to sit on it when they watch TV. The large window is shaded by a light colored curtain, giving the room a soft, bright feeling. A ceiling fan with small lights is hanging from the ceiling, whenever the fan and lights are on, we can see dangling images, which are reflected from the furniture in the room. Generally, our living room is a place where we receive our guest, gather together to discuss any topic and enjoy our leisure time.";
        s[6]="Three important Swiss customs for tourists to know deal with religion, greeting, and punctuality. (subject development) The Swiss people are very religious, and Sunday is their holy day. On Sunday, people rarely work in the garden, in the house, or even on the car. Foreign tourists should know that the most drugstores, supermarkets, and banks are closed on Sunday. The Swiss are also a formal people. For example, they seldom call acquaintances by their first names; the German “Herr” and French “Monsieur” are much more frequently used in Switzerland than the English “Mister” is used in the United States. A tourist should therefore say either “Herr” or “Monsieur” when greeting an acquaintance, and only use the person’s first name if he is a close friend. In addition, Switzerland is the land of watches and exactness. It is important to be on time to parties, business, meetings, and churches because Swiss hosts, factory bosses, and ministers all love punctuality. It is especially important for tourists to be on time for trains: Swiss train conductors never wait for late arrivers. (summary sentence) In summary, Swiss customs are very easy to follow and very important to remember!Money causes teenagers to feel stress. It makes them feel bad about themselves and envy other people. My friend, for instance, lives with her family and has to share a room with her sister, who is very cute and intelligent. This girl wishes she could have her own room and have a lot of stuff, but she can’t have these things because her family doesn’t have much money. Her family’s income is pretty low because her father is old and doesn’t go to work. Her sister is the only one who works. Because her family can’t buy her the things she wants, she feels a lot of stress and gets angry sometimes. Once, she wanted a beautiful dress to wear to a sweetheart dance. She asked her sister for some money to buy the dress. She was disappointed because her sister didn’t have money to give her. She sat in silence for a little while and then started yelling out loud. She said her friends";
        s[7]="The battles of Marathon and Tours are examples of how war has often determined the development of Western civilization. (subject development) The basis of Western civilization was probably decided at the Battle of Marathon about 2,500 years ago. In this battle, a small number of Greek soldiers led by a famous Greek general defeated 100,000 invading Persians under the Persian king. Because the Greeks won, Greek ideas about many subjects matured and became the foundation of Western society. Whereas Marathon laid the basis of Western civilization, its structure remained the same as a result of the Battle of Tours in A.D. 732. Before this battle, Muslim armies had taken control of a large number of countries, but they were stopped by a group of soldiers led by Charles Martel in France. If the Muslims had won at Tours, Islam might have become the major religion of Western societyMoney causes teenagers to feel stress. It makes them feel bad about themselves and envy other people. My friend, for instance, lives with her family and has to share a room with her sister, who is very cute and intelligent. This girl wishes she could have her own room and have a lot of stuff, but she can’t have these things because her family doesn’t have much money. Her family’s income is pretty low because her father is old and doesn’t go to work. Her sister is the only one who works. Because her family can’t buy her the things she wants, she feels a lot of stress and gets angry sometimes. Once, she wanted a beautiful dress to wear to a sweetheart dance. She asked her sister for some money to buy the dress. She was disappointed because her sister didn’t have money to give her. She sat in silence for a little while and then started yelling out loud. She said her friends.";
        s[8]="The battles of Marathon and Tours are examples of how war has often determined the development of Western civilization. (subject development) The basis of Western civilization was probably decided in Greece at the Battle of Marathon in 490 B.C. In this battle, 10,000 Greek soldiers led by Miltiades defeated 100,000 invading Persians under Darius I. Because the Greeks won, Greek ideas about philosophy, science, literature, and politics (such as democracy) matured and became the foundation of Western society. Whereas Marathon laid the basis of Western civilization, its structure remained the same as a result of the Battle of Tours in A.D. 732. Before this battle, Muslim armies had taken control of countries from India to the Atlantic Ocean, but they were stopped by a European army under Charles Martel at this battle in southwest France. If the Muslims had won at Tours, Islam might have become the major religion of Western society.";
        s[9]="A topic sentence usually comes at the beginning of a paragraph; that is, it is usually the first sentence in a formal academic paragraph.  (Sometimes this is not true, but as you practice writing with this online lesson site, please keep to this rule unless you are instructed otherwise.)  Not only is a topic sentence the first sentence of a paragraph, but, more importantly, it is the most general sentence in a paragraph.  What does most general mean?  It means that there are not many details in the sentence, but that the sentence introduces an overall idea that you want to discuss later in the paragraph.";
        for(int i=0;i<=9;i++){
     	   s[i]=s[i].substring(0, 200);}
        for(int a=0;a<=9;a++)
         b[a]= encode(s[a]);
        iv=(ImageView) this.findViewById(com.example.qrcodedatastreamingmodified.R.id.qrcode_image);
        boolean a=iv.post(new Runnable() {  
     	   
     	   int j = 0;      
     	   @Override   
     	   public void run() {
     		  if(j<=999999)
     	      iv.setImageBitmap(b[j%10]);
     	      if(j++ <= 999999){
     	         iv.postDelayed(this, 100);
     	      }
     	   }
     	});
       
       /* new Thread(new Runnable() {
            public void run() {
              while(true){
            	  if(bmtobedecoded!=null)
            		  Log.i("time1","null");
            	  
            	  try {
            		  Result result;
					result=qrr.decode(bmtobedecoded);
					Log.i("time1","The result is "+result.toString());
				} catch (NotFoundException e) {
					Log.i("time1","qr not found");
				} catch (ChecksumException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	  
              }
            }
           }).start();*/
    }
    public Bitmap encode(String s){
    	
        BitMatrix result=null;
        
        try {
          try {
 			result = qw.encode(s, BarcodeFormat.QR_CODE, 500, 500);
 		} catch (WriterException e) {
 			// TODO Auto-generated catch block
 			//e.printStackTrace();
 		}
        } catch (IllegalArgumentException iae) {
          // Unsupported format
          
        }
        Log.i("123","good5");
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
          int offset = y * width;
          for (int x = 0; x < width; x++) {
            pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
          }
        }
        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Log.i("123","should change image");
        return bitmap;
      
   }


    private SurfaceView mSurfaceview = null;  
    private SurfaceHolder mSurfaceHolder = null;  
    private Camera mCamera =null;    
    
    public static String TAG = "cn";
    boolean bIfPreview = false;
    int mPreviewHeight = 480;
    int mPreviewWidth = 640;
    public int count=0;
	public long startTime;
	public long endTime;
	public long total;
}