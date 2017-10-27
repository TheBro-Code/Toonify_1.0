package com.example.syamantak.toonify_10;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class Interface extends AppCompatActivity {
    
    private Bitmap bmp;
    private ImageView interface_iv;
    private Button save_interface, edit_interface;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
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
        interface_iv = (ImageView) findViewById(R.id.interface_iv);
        interface_iv.setImageBitmap(bmp);
        save_interface = (Button) findViewById(R.id.save_interface);
        save_interface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile();
            }
        });
        edit_interface = (Button) findViewById(R.id.edit_interface);
        edit_interface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editImage();
            }
        });
    }
    
    //    private void saveFile(){
    //        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Toonify";
    //
    //        File outputDir= new File(path);
    //
    //
    //        outputDir.mkdirs();
    //        File newFile = new File(path+"/"+"test.png");
    //        FileOutputStream out = null;
    //        try {
    //            out = new FileOutputStream(nexwFile);
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        }
    //        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
    //        Intent intent = new Intent();
    //        setResult(RESULT_OK,intent);
    //        finish();
    //    }
    
    private void saveFile() {
        
        String fileName = UUID.randomUUID().toString() + ".jpg";
        
        File direct = new File(Environment.getExternalStorageDirectory() + "/Toonify");
        
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Toonify/");
            wallpaperDirectory.mkdirs();
        }
        
        File file = new File(new File("/sdcard/Toonify/"), fileName);
        
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "test.jpg" , "New Image");
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editImage(){
        Bitmap bmp = ((BitmapDrawable) interface_iv.getDrawable()).getBitmap();
        
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
            Intent intent = new Intent(this, Edit.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent,1);
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
            interface_iv.setImageBitmap(bmp);
        }
    }
}

