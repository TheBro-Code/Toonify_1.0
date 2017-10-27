package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.R.attr.angle;
import static com.example.syamantak.toonify_10.R.drawable.rotate;

public class Rotate extends AppCompatActivity {
    private int angle = 0;
    private Button done, antickcwise, ckcwise;
    private ImageView imageDisplay;
    private SeekBar rotateBar;
    private TextView rotateText;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        imageDisplay = (ImageView) findViewById(R.id.rotate_iv);
        done = (Button) findViewById(R.id.rotate_button);
        antickcwise = (Button) findViewById(R.id.antickcwise_button);
        ckcwise = (Button) findViewById(R.id.ckcwise_button);
        antickcwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAntiCkcwise();
            }
        });
        ckcwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateCkcwise();
            }
        });
        rotateText = (TextView) findViewById(R.id.rotate_tv);
        rotateBar = (SeekBar) findViewById(R.id.rotate_sb);
        done.setOnClickListener(new View.OnClickListener() {
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
                rotate();
            }
            
        };
        rotateBar.setOnSeekBarChangeListener(seekBarChangeListener);
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
    
    private void rotate() {
        angle = rotateBar.getProgress() - 180;
        rotateText.setText("Angle :"+angle);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                                             matrix,false);
        
        imageDisplay.setImageBitmap(rotated);
    }
    
    private void rotateAntiCkcwise(){
        angle = angle-90;
        if(angle<-180){
            angle = angle+360;
        }
        rotateBar.setProgress(angle+180);
        rotateText.setText("Angle :"+angle);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                                             matrix,false);
        
        imageDisplay.setImageBitmap(rotated);
    }
    
    private void rotateCkcwise(){
        angle = angle+90;
        if(angle>179){
            angle = angle-360;
        }
        rotateText.setText("Angle :"+angle);
        rotateBar.setProgress(angle+180);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                                             matrix,false);
        
        imageDisplay.setImageBitmap(rotated);
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

