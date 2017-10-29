package com.example.syamantak.toonify_10;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;        import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Edit extends AppCompatActivity {
    private int EDIT_FILTER = 0, EDIT_CONTBRIT = 1, EDIT_HSV = 2, EDIT_ROTATE = 3;
    private ImageView edit_iv;
    private Button edit_filter, edit_contbrit, edit_hsv, edit_done, edit_rotate, edit_resize;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edit_iv = (ImageView) findViewById(R.id.edit_iv);
        edit_filter = (Button) findViewById(R.id.edit_filter);
        edit_hsv = (Button) findViewById(R.id.edit_hsv);
        edit_done = (Button) findViewById(R.id.edit_done);
        edit_contbrit = (Button) findViewById(R.id.edit_contbrit);
        edit_rotate = (Button) findViewById(R.id.edit_rotate);
        edit_resize = (Button) findViewById(R.id.edit_resize);
        edit_resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editResize();
            }
        });
        edit_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRotate();
            }
        });
        edit_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFilter();
            }
        });
        edit_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHSV();
            }
        });
        edit_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDone();
            }
        });
        edit_contbrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContbrit();
            }
        });
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
        edit_iv.setImageBitmap(bmp);
    }
    private void editFilter(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, Filters.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent, EDIT_FILTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editHSV(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, HSV.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent, EDIT_HSV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editContbrit(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, Brightness.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent, EDIT_CONTBRIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editRotate(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, Rotate.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent, EDIT_ROTATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editResize(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, Resize.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent, EDIT_ROTATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void editDone(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();
        
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
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            String filename = b.getString("image");
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
            edit_iv.setImageBitmap(bmp);
        }
    }
}

