package com.example.qrcodedatastreamingmodified;


import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.example.qrcodedatastreamingmodified.R.id;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
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
	public Bitmap b;
	public QRCodeReader qrr=new QRCodeReader();
	public QRCodeWriter qw=new QRCodeWriter();
	public boolean isin=false;
	public String decoderesult="This is ";
	public Queue<String> queue;
	public Queue<BinaryBitmap> queue1;
	private static final int WHITE = 0xFFFFFFFF;
	  private static final int BLACK = 0xFF000000;
	  public BinaryBitmap bmtobedecoded;
	  public boolean setup=false;
	  public int[] current;
    public void surfaceCreated(SurfaceHolder holder)
   
    {
    	mCamera = Camera.open(0);
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
        	int imageWidth = camera.getParameters().getPreviewSize().width  ;
            int imageHeight =camera.getParameters().getPreviewSize().height ;
            //int RGBData[] = new int[imageWidth* imageHeight]; 
            
                       // byte[] mYUVData = new byte[data.length];    
            //System.arraycopy(data, 0, mYUVData, 0, data.length);
            //decodeYUV420SP(RGBData, data, imageWidth, imageHeight);

            LuminanceSource source = new PlanarYUVLuminanceSource(data,imageWidth,imageHeight,0,0,400,400,false);
      	    bmtobedecoded = new BinaryBitmap(new HybridBinarizer(source));
      	  if(count == 0){

              startTime = System.nanoTime();

             count ++;

           //  Log.i("time1", "StartTime:"+startTime);

             }

             else if (count == 100){

              endTime = System.nanoTime();

             count=0;
              total=endTime-startTime;
            // Log.i("time1", "EndTime:"+endTime);
             double seconds = total/1.0E09;
             Log.i("time1", "TotalTime:"+seconds);
             }

             else{

             count++;

             }
      	  try {
    		  Result result;
			result=qrr.decode(bmtobedecoded);
			Log.i("time1","The result is "+result.toString());
			queue.add(result.toString()+"piers");
			
		} catch (NotFoundException e) {
			//Log.i("time1","qr not found");
		} catch (ChecksumException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }

    };
    
 
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
        queue=new LinkedList<String>();
        queue1=new LinkedList<BinaryBitmap>();
        queue.add("This is");
        // FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSurfaceView();
        iv=(ImageView) this.findViewById(id.qrcode_image);
        iv=(ImageView) this.findViewById(com.example.qrcodedatastreamingmodified.R.id.qrcode_image);
        boolean a=iv.post(new Runnable() {  
     	        
     	   @Override   
     	   public void run() {

     		   if(queue.isEmpty()==false){
     		   b=encode(queue.poll());
     		   iv.setImageBitmap(b);
     		   }
     		  iv.postDelayed(this, 0);
     	   }
     	});
        boolean bb=iv.post(new Runnable() {  
 	        
      	   @Override   
      	   public void run() {
      		   if(queue.isEmpty()==false){
      		   b=encode(queue.poll());
      		   iv.setImageBitmap(b);
      		   }
      		  iv.postDelayed(this, 0);
      	   }
      	});
        boolean bbb=iv.post(new Runnable() {  
 	        
       	   @Override   
       	   public void run() {
       		 /* if(count == 0){

                    startTime = System.nanoTime();

                   count ++;

                 //  Log.i("time1", "StartTime:"+startTime);

                   }

                   else if (count == 100){

                    endTime = System.nanoTime();

                   count=0;
                    total=endTime-startTime;
                  // Log.i("time1", "EndTime:"+endTime);
                   double seconds = total/1.0E09;
                   Log.i("time1", "TotalTime:"+seconds);
                   }

                   else{

                   count++;

                   } */
       		   if(queue.isEmpty()==false){
       		   b=encode(queue.poll());
       		   iv.setImageBitmap(b);
       		   }
       		  iv.postDelayed(this, 0);
       	   }
       	});
        
       
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