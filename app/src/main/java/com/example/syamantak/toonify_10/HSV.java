package com.example.syamantak.toonify_10;

import android.content.Context;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.FileInputStream;

import android.content.Intent;
import android.view.View.OnClickListener;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static com.example.syamantak.toonify_10.R.id.hsv_iv;
import static com.example.syamantak.toonify_10.R.id.progressBar;

public class HSV extends AppCompatActivity {
    
    private Button done;
    private ImageView imageDisplay;
    private SeekBar hueBar, satBar, valBar;
    private TextView hueText, satText, valText;
    private Bitmap bmp;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsv);
        imageDisplay = (ImageView) findViewById(R.id.hsv_iv);
        done = (Button) findViewById(R.id.hsv_done);
        satText = (TextView) findViewById(R.id.textsat);
        hueText = (TextView) findViewById(R.id.texthue);
        valText = (TextView) findViewById(R.id.textval);
        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        progressBar = (ProgressBar) findViewById(R.id.hsv_pb);
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new AsyncTaskHSV().execute();
            }
            
        };
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);
        Bundle b = getIntent().getExtras();
        String filename = b.getString("image");
        String TAG = "syamantak debug";
        Log.d(TAG, filename);
        int height = b.getInt("height");
        int width = b.getInt("width");
        try {
            FileInputStream is = this.openFileInput(filename);
            Bitmap inpBmp = BitmapFactory.decodeStream(is);
            Mat inp = new Mat(inpBmp.getHeight(),inpBmp.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(inpBmp,inp);
            Size sz = new Size(width,height);
            Mat fin = new Mat(height,width,CvType.CV_8UC4);
            Imgproc.resize(inp,fin,sz);
            bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin,bmp);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        imageDisplay.setImageBitmap(bmp);
    }
    
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat,
                             float settingVal) {
        
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];
        
        float[] pixelHSV = new float[3];
        
        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);
        
        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                
                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);
                
                // Adjust HSV
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] = 0.0f;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] = 360.0f;
                }
                
                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }
                
                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }
                
                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(pixelHSV);
                
                index++;
            }
        }
        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);
    }
    
    
    private Bitmap hsv(){
        int progressHue = hueBar.getProgress() - 256;
        int progressSat = satBar.getProgress() - 256;
        int progressVal = valBar.getProgress() - 256;
        
        /*
         * Hue (0 .. 360) Saturation (0...1) Value (0...1)
         */
        
        float hue = (float) progressHue * 360 / 256;
        float sat = (float) progressSat / 256;
        float val = (float) progressVal / 256;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, InputImage);
        double scale = Math.sqrt(bmp.getAllocationByteCount()/4000000);
        int height1 = height;
        int width1 = width;
        if(scale>1){
            height1 = (int)(height/scale);
            width1 = (int) (width/scale);
        }
        Mat help1 = new Mat(height1,width1,CvType.CV_8UC4);
        Size sz = new Size(width1,height1);
        Imgproc.resize(InputImage,help1, sz);
        Bitmap bitmap1 = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help1,bitmap1);
        Bitmap bitmap2 =  Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        bitmap2 = updateHSV(bitmap1,hue,sat,val);
        Mat help2 = new Mat(height,width,CvType.CV_8UC4);
        Mat help3 = new Mat(height1,width1,CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap2,help3);
        Size sz2 = new Size(width,height);
        Imgproc.resize(help3,help2,sz2);
        Bitmap bitmap3 =  Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bitmap3);
        return bitmap3;
    }
    
    private class AsyncTaskHSV extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = hsv();
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            int progressHue = hueBar.getProgress() - 256;
            int progressSat = satBar.getProgress() - 256;
            int progressVal = valBar.getProgress() - 256;
            
            /*
             * Hue (0 .. 360) Saturation (0...1) Value (0...1)
             */
            
            float hue = (float) progressHue * 360 / 256;
            float sat = (float) progressSat / 256;
            float val = (float) progressVal / 256;
            
            hueText.setText("Hue: " + String.valueOf(hue));
            satText.setText("Saturation: " + String.valueOf(sat));
            valText.setText("Value: " + String.valueOf(val));
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            imageDisplay.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void done(){
        Bitmap bmp = ((BitmapDrawable) imageDisplay.getDrawable()).getBitmap();
        
        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            double scale = 1;
            if(bmp.getAllocationByteCount()>1250000){
                scale = Math.sqrt(bmp.getAllocationByteCount()/1250000);
            }
            Mat inputImage = new Mat(height, width, CvType.CV_8UC4);
            Utils.bitmapToMat(bmp,inputImage);
            Size sz = new Size((int) (width/scale),(int) (height/scale));
            Mat fin = new Mat((int) (height/scale),(int) (width/scale),CvType.CV_8UC4);
            Imgproc.resize(inputImage,fin,sz);
            Bitmap tosend = Bitmap.createBitmap((int) (width/scale),(int) (height/scale), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin,tosend);
            tosend.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            setResult(RESULT_OK,intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

