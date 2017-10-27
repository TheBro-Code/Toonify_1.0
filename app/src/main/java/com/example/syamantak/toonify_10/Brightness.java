package com.example.syamantak.toonify_10;

import android.content.Context;
import java.io.FileOutputStream;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Brightness extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    
    private static final String TAG = "MainActivity";
    
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }
    
    private ImageView iv_image;
    private SeekBar sb_brightness, sb_contrast;
    private Button done;
    private Bitmap image;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness);
        iv_image = (ImageView) findViewById(R.id.bright);
        sb_brightness = (SeekBar) findViewById(R.id.sb_brightness);
        sb_brightness.setOnSeekBarChangeListener(this);
        sb_contrast = (SeekBar) findViewById(R.id.sb_contrast);
        done = (Button) findViewById(R.id.bright_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightDone();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.bright_pb);
        sb_contrast.setOnSeekBarChangeListener(this);
        Bundle b = getIntent().getExtras();
        String filename = b.getString("image");
        String TAG = "syamantak debug";
        Log.d(TAG, filename);
        int height = b.getInt("height");
        int width = b.getInt("width");
        try {
            FileInputStream is = this.openFileInput(filename);
            Bitmap inpBmp = BitmapFactory.decodeStream(is);
            Mat inp = new Mat(inpBmp.getHeight(), inpBmp.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(inpBmp, inp);
            Size sz = new Size(width, height);
            Mat fin = new Mat(height, width, CvType.CV_8UC4);
            Imgproc.resize(inp, fin, sz);
            image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin, image);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        iv_image.setImageBitmap(image);
    }
    
    private Bitmap increaseBrightness(Bitmap bitmap, int value) {
        
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        src.convertTo(src, -1, 1, value);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;
    }
    
    private Bitmap adjustedContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        
        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        
        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);
        
        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));
        
        
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);
        
        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }
                
                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }
                
                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }
                
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
    
    private Bitmap brightness() {
        int progressBrightness = sb_brightness.getProgress();
        int progressContrast = sb_contrast.getProgress();
        int height = image.getHeight();
        int width = image.getWidth();
        Mat InputImage = new Mat(height, width, CvType.CV_8UC4);
        Bitmap bmp32 = image.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, InputImage);
        double scale = Math.sqrt(image.getAllocationByteCount() / 4000000);
        int height1 = height;
        int width1 = width;
        if (scale > 1) {
            height1 = (int) (height / scale);
            width1 = (int) (width / scale);
        }
        Mat help1 = new Mat(height1, width1, CvType.CV_8UC4);
        Size sz = new Size(width1, height1);
        Imgproc.resize(InputImage, help1, sz);
        Bitmap bitmap1 = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help1, bitmap1);
        Bitmap editedBrightness = increaseBrightness(bitmap1, progressBrightness);
        Bitmap fin = adjustedContrast(editedBrightness, progressContrast);
        Mat help2 = new Mat(height, width, CvType.CV_8UC4);
        Mat help3 = new Mat(height1, width1, CvType.CV_8UC4);
        Utils.bitmapToMat(fin, help3);
        Size sz2 = new Size(width, height);
        Imgproc.resize(help3, help2, sz2);
        Bitmap bitmap3 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2, bitmap3);
        return bitmap3;
    }
    
    
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        
        
    }
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        
    }
    
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        new AsyncTaskBrightness().execute();
    }
    
    public void brightDone() {
        Bitmap bmp = ((BitmapDrawable) iv_image.getDrawable()).getBitmap();
        
        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            double scale = 1;
            if (bmp.getAllocationByteCount() > 1250000) {
                scale = Math.sqrt(bmp.getAllocationByteCount() / 1250000);
            }
            Mat inputImage = new Mat(height, width, CvType.CV_8UC4);
            Utils.bitmapToMat(bmp, inputImage);
            Size sz = new Size((int) (width / scale), (int) (height / scale));
            Mat fin = new Mat((int) (height / scale), (int) (width / scale), CvType.CV_8UC4);
            Imgproc.resize(inputImage, fin, sz);
            Bitmap tosend = Bitmap.createBitmap((int) (width / scale), (int) (height / scale), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin, tosend);
            tosend.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width", width);
            b.putString("image", filename);
            intent.putExtras(b);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class AsyncTaskBrightness extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help =  brightness();
            return help;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            iv_image.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}

