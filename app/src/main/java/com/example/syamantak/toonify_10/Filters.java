package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class Filters extends AppCompatActivity {
    private ImageView filter_iv;
    private Button filters_done;
    private Bitmap bmp;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
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
        filter_iv = (ImageView) findViewById(R.id.filter_iv);
        filter_iv.setImageBitmap(bmp);
        filters_done = (Button) findViewById(R.id.filters_done);
        filters_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersDone();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        addCartoonify(layout);
        addPencilSketch(layout);
        addSepia(layout);
        addVignette(layout);
        addGrayscale(layout);
        addColorEmboss(layout);
        addGrayscaleEmboss(layout);
        addReflection(layout);
    }
    
    private void addCartoonify(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doCartoonify(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskCartoonify().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskCartoonify extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doCartoonify(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    
    private void addPencilSketch(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doPencilSketch(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskPencilSketch().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskPencilSketch extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doPencilSketch(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addSepia(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doSepia(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskAddSepia().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskAddSepia extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doSepia(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    
    private void addGrayscale(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doGrayscale(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskGrayScale().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskGrayScale extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doGrayscale(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addColorEmboss(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(emboss(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskColorEmboss().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskColorEmboss extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = emboss(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addGrayscaleEmboss(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(gray_emboss(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskGrayscaleEmboss().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskGrayscaleEmboss extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = gray_emboss(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addVignette(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(applyVignette(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskVignette().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskVignette extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = applyVignette(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addReflection(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(applyReflection(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskAddReflection().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskAddReflection extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = applyReflection(bmp);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addRotation(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(350);
        imageView.setMaxWidth(350);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(350,350,CvType.CV_8UC4);
        Size sz = new Size(350,350);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(rotate(bmpPrime,30));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskRotate().execute();
            }
        });
        layout.addView(imageView);
    }
    
    private class AsyncTaskRotate extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = rotate(bmp,30);
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void addCrop(LinearLayout layout){
        
    }
    
    
    
    
    
    //Implemention of Filters
    
    
    private Bitmap doCartoonify(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Mat InputImage = new Mat();
        Mat imgGray;
        Mat imgCanny;
        Mat edgeImage;
        Mat dstMat;
        Mat mrgbk;
        Mat FinalImage;
        edgeImage = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);
        imgCanny = new Mat(height, width, CvType.CV_8UC4);
        FinalImage = new Mat(height,width,CvType.CV_8UC4);
        dstMat = new Mat(height, width, CvType.CV_8UC4);
        mrgbk = new Mat(height, width, CvType.CV_8UC4);
        Mat resizeimage = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        
        Utils.bitmapToMat(bmp32, InputImage);
        double scale = Math.sqrt(bitmap.getAllocationByteCount()/1350000);
        if(scale>1){
            Size sz = new Size(width/scale, height/scale);
            Imgproc.resize(InputImage,resizeimage, sz);
        }
        else{
            resizeimage = InputImage;
        }
        cvtColor(InputImage, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(imgGray,imgGray, 7);
        //Imgproc.Canny(imgGray, imgCanny, 50, 150);
        //Imgproc.cvtColor(imgCanny,imgCanny,Imgproc.COLOR_GRAY2RGB);
        if(scale > 1) {
            adaptiveThreshold(imgGray, edgeImage, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 4);
            Imgproc.cvtColor(edgeImage, edgeImage, COLOR_GRAY2RGB);
        }
        else {
            adaptiveThreshold(imgGray, edgeImage, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 9, 6);
            Imgproc.cvtColor(edgeImage, edgeImage, COLOR_GRAY2RGB);
        }
        //Core.bitwise_not(imgCanny,edgeImage);
        cvtColor(resizeimage, mrgbk, Imgproc.COLOR_BGRA2BGR);
        for(int i=0;i<14;i++)
        {
            if(i%2 == 0)Imgproc.bilateralFilter(mrgbk, dstMat, 9, 9, 7);
            
            else Imgproc.bilateralFilter(dstMat, mrgbk, 9, 9, 7);
        }
        Imgproc.bilateralFilter(mrgbk, dstMat, 9, 7, 7);
        cvtColor(dstMat, dstMat, Imgproc.COLOR_RGB2RGBA);
        cvtColor(edgeImage,edgeImage,Imgproc.COLOR_RGB2RGBA);
        if(scale>1){
            Size sz2 = new Size(width,height);
            Imgproc.resize(dstMat,dstMat,sz2);
        }
        Core.bitwise_and(dstMat,edgeImage,FinalImage);
        org.opencv.core.Size s = new Size(7,7);
        Imgproc.GaussianBlur(FinalImage, FinalImage, s, 0, 0);
        Utils.matToBitmap(FinalImage, bitmap);
        return bitmap;
    }
    
    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }
    
    public Bitmap ColorDodgeBlend(Bitmap Source,Bitmap Layer)
    {
        Bitmap base = Source.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
        Bitmap blend = Layer.copy(android.graphics.Bitmap.Config.ARGB_8888, false);
        
        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();
        
        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();
        
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();
        
        while (buffOut.position() < buffOut.limit()) {
            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();
            
            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);
            
            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);
            
            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);
            
            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);
            
            buffOut.put(pixel);
        }
        
        buffOut.rewind();
        
        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();
        
        return base;
    }
    
    public Bitmap doPencilSketch(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        Mat imgGray;
        Mat Inverted_Gray;
        Inverted_Gray = new Mat(height,width,CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC4);
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, InputImage);
        cvtColor(InputImage, imgGray, Imgproc.COLOR_RGB2GRAY);
        Core.bitwise_not(imgGray,Inverted_Gray);
        org.opencv.core.Size s1 = new Size(17,17);
        Imgproc.GaussianBlur(Inverted_Gray, Inverted_Gray, s1, 0, 0);
        double scale = Math.sqrt(bitmap.getAllocationByteCount()/4000000);
        int height1 = height;
        int width1 = width;
        if(scale>1){
            height1 = (int)(height/scale);
            width1 = (int) (width/scale);
            s1 = new Size(25,25);
        }
        Mat help1 = new Mat(height1,width1,CvType.CV_8UC4);
        Mat help2 = new Mat(height1,width1,CvType.CV_8UC4);
        Size sz = new Size(width1,height1);
        Imgproc.resize(Inverted_Gray,help1, sz);
        Imgproc.resize(imgGray,help2,sz);
        Bitmap bitmap1 = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Bitmap bitmap2 = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help1, bitmap1);
        Utils.matToBitmap(help2, bitmap2);
        Bitmap bmp = ColorDodgeBlend(bitmap1,bitmap2);
        Mat semiFin = new Mat(height1,width1,CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,semiFin);
        Mat fin = new Mat(height,width,CvType.CV_8UC4);
        Size sz2 = new Size(width,height);
        Imgproc.resize(semiFin,fin,sz2);
        Bitmap atLast = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(fin,atLast);
        return atLast;
    }
    
    public Bitmap doSepia(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Mat InputImage;
        InputImage = new Mat();
        Mat Temp1 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp2 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp3 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp4 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp5 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp6 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp7 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp8 = new Mat(height, width, CvType.CV_8UC4);
        Mat Temp9 = new Mat(height, width, CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, InputImage);
        List<Mat> Channels = new ArrayList<Mat>();
        Core.split(InputImage,Channels);
        Mat Output_Red = new Mat(height, width, CvType.CV_8UC4);
        Mat Output_Green = new Mat(height, width, CvType.CV_8UC4);
        Mat Output_Blue = new Mat(height, width, CvType.CV_8UC4);
        Mat dst = new Mat(height, width, CvType.CV_8UC4);
        Scalar alpha1 = new Scalar(.393);
        Scalar alpha2 = new Scalar(.769);
        Scalar alpha3 = new Scalar(.189);
        Scalar alpha4 = new Scalar(.349);
        Scalar alpha5 = new Scalar(.686);
        Scalar alpha6 = new Scalar(.168);
        Scalar alpha7 = new Scalar(.272);
        Scalar alpha8 = new Scalar(.534);
        Scalar alpha9 = new Scalar(.131);
        Core.multiply(Channels.get(0),alpha1,Temp1);
        Core.multiply(Channels.get(1),alpha2,Temp2);
        Core.multiply(Channels.get(2),alpha3,Temp3);
        Core.multiply(Channels.get(0),alpha4,Temp4);
        Core.multiply(Channels.get(1),alpha5,Temp5);
        Core.multiply(Channels.get(2),alpha6,Temp6);
        Core.multiply(Channels.get(0),alpha7,Temp7);
        Core.multiply(Channels.get(1),alpha8,Temp8);
        Core.multiply(Channels.get(2),alpha9,Temp9);
        Core.addWeighted(Temp1,1,Temp2,1,0,Output_Blue);
        Core.addWeighted(Output_Blue,1,Temp3,1,0,Output_Blue);
        Core.addWeighted(Temp4,1,Temp5,1,0,Output_Green);
        Core.addWeighted(Output_Green,1,Temp6,1,0,Output_Red);
        Core.addWeighted(Temp7,1,Temp8,1,0,Output_Red);
        Core.addWeighted(Output_Red,1,Temp9,1,0,Output_Red);
        List<Mat> lst = Arrays.asList(Output_Blue, Output_Green, Output_Red);
        Core.merge(lst, dst);
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        cvtColor(dst, dst, Imgproc.COLOR_BGR2BGRA);
        Utils.matToBitmap(dst, bmp32);
        return bmp32;
    }
    public Bitmap doGrayscale(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        Mat help1 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Mat help2 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,help1);
        cvtColor(help1,help2,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(help2,bitmap);
        return bitmap;
    }
    
    public Bitmap emboss(Bitmap src)
    {
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        Mat convMat = new Mat(3,3,CvType.CV_32F);
        Mat Output_Image = new Mat(height,width,CvType.CV_8UC4);
        
        InputImage = new Mat(height, width, CvType.CV_8UC4);
        
        Utils.bitmapToMat(bitmap,InputImage);
        
        convMat.put(0,0, -2);
        convMat.put(0,1, -1);
        convMat.put(0,2, 0);
        convMat.put(1,0, -1);
        convMat.put(1,1, 1);
        convMat.put(1,2, 1);
        convMat.put(2,0, 0);
        convMat.put(2,1, 1);
        convMat.put(2,2, 2);
        
        Point anchor= new Point(-1,-1);
        
        Imgproc.filter2D(InputImage, Output_Image,-1,convMat,anchor,0);
        
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(Output_Image,bmp32);
        return bmp32;
    }
    
    public Bitmap gray_emboss(Bitmap src)
    {
        Bitmap bitmap1 = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap1.getHeight();
        int width = bitmap1.getWidth();
        
        Bitmap bitmap = doGrayscale(bitmap1);
        
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        Mat convMat = new Mat(3,3,CvType.CV_32F);
        Mat Output_Image = new Mat(height,width,CvType.CV_8UC4);
        
        InputImage = new Mat(height, width, CvType.CV_8UC4);
        
        Utils.bitmapToMat(bitmap,InputImage);
        
        convMat.put(0,0, -2);
        convMat.put(0,1, -1);
        convMat.put(0,2, 0);
        convMat.put(1,0, -1);
        convMat.put(1,1, 1);
        convMat.put(1,2, 1);
        convMat.put(2,0, 0);
        convMat.put(2,1, 1);
        convMat.put(2,2, 2);
        
        Point anchor= new Point(-1,-1);
        
        Imgproc.filter2D(InputImage, Output_Image,-1,convMat,anchor,0);
        
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(Output_Image,bmp32);
        return bmp32;
    }
    
    public static Bitmap applyReflection(Bitmap src) {
        
        Bitmap originalImage = src.copy(Bitmap.Config.ARGB_8888,true);
        
        // gap space between original and reflected
        final int reflectionGap = 4;
        // get image size
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // this will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        
        // create a Bitmap with the flip matrix applied to it.
        // we only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
        
        // create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);
        
        // create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // draw in the reflection
        canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);
        
        // create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                                                   bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                                                   Shader.TileMode.CLAMP);
        // set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        
        return bitmapWithReflection;
    }
    
    
    
    public static Bitmap rotate(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
    
    
    public void filtersDone(){
        Bitmap bmp = ((BitmapDrawable) filter_iv.getDrawable()).getBitmap();
        
        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            double scale = 1;
            if(bmp.getAllocationByteCount()>1350000){
                scale = Math.sqrt(bmp.getAllocationByteCount()/1350000);
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
    
    public Bitmap applyVignette(Bitmap src) {
        Bitmap originalBitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        
        float radius = (float) (originalBitmap.getWidth()/1.5);
        RadialGradient gradient = new RadialGradient(originalBitmap.getWidth()/2, originalBitmap.getHeight()/2, radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        
        Canvas canvas = new Canvas(originalBitmap);
        canvas.drawARGB(1, 0, 0, 0);
        
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setShader(gradient);
        
        final android.graphics.Rect rect = new android.graphics.Rect(0, 0, originalBitmap.getWidth(), originalBitmap.getHeight());
        final android.graphics.RectF rectf = new android.graphics.RectF(rect);
        
        canvas.drawRect(rectf, paint);
        
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originalBitmap, rect, rect, paint);
        
        return originalBitmap;
    }
}

