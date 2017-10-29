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

public class    Edit extends AppCompatActivity {
    private int EDIT_FILTER = 0, EDIT_CONTBRIT = 1, EDIT_HSV = 2, EDIT_ROTATE = 3;
    private ImageView edit_iv;
    private Button edit_filter, edit_contbrit, edit_hsv, edit_done, edit_rotate, edit_resize;
    private Bitmap bmp;
    @Override

    /**
     * In the Oncreate method various layout elements(textviews,buttons,imageviews) of the layout are declared and initialised.
     * The onclick method of the various buttons on this activity are also specified.
     * Also the imageView edit_iv is set equal to the image whose filename has been passed thought intent by its privious activity Interface.class
     */
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


    /**
     * This method is called when the edit-filter button is tapped by the user.
     * It first gets the image from the imageview edit_iv in bitmap format and then compresses it (for speed gains) by scaling widht and height
     * Then it saves the image bitmap into a FileOutputStream object which will later be retrieved in the activity this method starts.
     * After that it creates an Intent object which carries height,width and name of the compressed bitmap.
     * This intent is then used to start the filter_java activity expecting a result.(startActivityForResult)
     * @return void
     */
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

    /**
     * This method is called when the edit-hsv button is tapped by the user.
     * It first gets the image from the imageview edit_iv in bitmap format and then compresses it (for speed gains) by scaling widht and height
     * Then it saves the image bitmap into a FileOutputStream object which will later be retrieved in the activity this method starts.
     * After that it creates an Intent object which carries height,width and name of the compressed bitmap.
     * This intent is then used to start the hsv activity expecting a result.(startActivityForResult)
     * @return void
     */
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

    /**
     * This method is called when the edit-contbrit button is tapped by the user.
     * It first gets the image from the imageview edit_iv in bitmap format and then compresses it (for speed gains) by scaling widht and height
     * Then it saves the image bitmap into a FileOutputStream object which will later be retrieved in the activity this method starts.
     * After that it creates an Intent object which carries height,width and name of the compressed bitmap.
     * This intent is then used to start the Brightness activity expecting a result.(startActivityForResult)
     * @return void
     */
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

    /**
     * This method is called when the edit-rotate button is tapped by the user.
     * It first gets the image from the imageview edit_iv in bitmap format and then compresses it (for speed gains) by scaling widht and height
     * Then it saves the image bitmap into a FileOutputStream object which will later be retrieved in the activity this method starts.
     * After that it creates an Intent object which carries height,width and name of the compressed bitmap.
     * This intent is then used to start the Rotate activity expecting a result.(startActivityForResult)
     * @return void
     */
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
    


    /**
     * This function is called when the user taps on done button(@id/edit_done)
     * The bitmap is first compressed for speed gains by scaling width and height.
     * Then the compressed form of the image bitmap is saved into a FileOutputStream Object which will later be retrieved in the paernt activity
     * Then it creates a new intent object carrying information about height,width and name of the saved image
     * This intent is passed back to the parent activity of this activity using setResult function.
     * Basically when this method is called the current activity is stopped and we go back to its parent activity
     * @return void
     */
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

    /**
     * This method recieves the result intent from its subsequent activites.
     * If the resultCode passed by the subsequent activity is RESULT_OK, this method loads the image saved into FileOutputStream object into a bitmap
     * The bitmap is then converted to a Mat object which is scaled back to it's original size and then converted back to bitmap.
     * The converted bitmap is then set to to edit_iv imageView.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
