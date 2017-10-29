/**
 * Main project package containing all the java files of the activities.
 */
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.FileInputStream;

public class Brightness extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    // ImageView variable for showing the preview of the edited image
    private ImageView iv_image;
    // SeekBar variables for changing the values of Brightness and Contrast respectively
    private SeekBar sb_brightness, sb_contrast;
    // Button variable corresponding to the button which saves the changes and moves project to Edit Activity
    private Button done;
    // Bitmap variable for storing the bitmap coming from the Edit activity
    private Bitmap image;
    // ProgressBar variable which will be visible when the image is being processed
    private ProgressBar progressBar;
    
    @Override
    /**
     * This function sets the layout of the activity, assigns the global value to their corresponding values by linking them to the ids of the objects on it's creation.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This function call sets the layout to the on defined in activity_brightness
        setContentView(R.layout.activity_brightness);
        // // iv_image is assigned the corresponding ImageView
        iv_image = (ImageView) findViewById(R.id.bright);
        // sb_brightness is assigned the corresponding SeekBar
        sb_brightness = (SeekBar) findViewById(R.id.sb_brightness);
        // Defining what to do on the change of values of sb_brightness SeekBar
        sb_brightness.setOnSeekBarChangeListener(this);
        // sb_contrast is assigned the corresponding SeekBar
        sb_contrast = (SeekBar) findViewById(R.id.sb_contrast);
        // Defining what to do on the change of values of sb_contrast SeekBar
        sb_contrast.setOnSeekBarChangeListener(this);
        // done is assigned the corresponding Button
        done = (Button) findViewById(R.id.bright_done);
        // This specifies that function brightDone() is to be called on the click of done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightDone();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.bright_pb);
        // Extracting the extra information of the data in thr the intent passed on from Edit class and storing it in a Bundle variable b
        Bundle b = getIntent().getExtras();
        // Getting the pathname of the path in which the image is stored temporarily while passed on from the Edit activity
        String filename = b.getString("image");
        // Getting the actual height of the bitmap of the image passed on from the Edit activity
        int height = b.getInt("height");
        // Getting the actual width of the bitmap of the image passed on from the Edit activity
        int width = b.getInt("width");
        // try-catch statement for the extraction of image
        try {
            // Getting the FileInputStream corresponding to the path of the image
            FileInputStream is = this.openFileInput(filename);
            // Decoding the passed image in a Bitmap
            Bitmap inpBmp = BitmapFactory.decodeStream(is);
            // Defining a new Mat for inpBmp
            Mat inp = new Mat(inpBmp.getHeight(),inpBmp.getWidth(), CvType.CV_8UC4);
            // Storing the information of inpBmp in the inp Mat
            Utils.bitmapToMat(inpBmp,inp);
            // Defining a new size variable having same size as the original image
            Size sz = new Size(width,height);
            // Defining a new Mat for the upscaled image
            Mat fin = new Mat(height,width,CvType.CV_8UC4);
            // Upscaling the inp Mat to actual size of the image and storing it in Mat fin
            Imgproc.resize(inp,fin,sz);
            // Assigning bmp the a new Bitmap of same size as that of the image
            image = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            // Getting the data from the Mat fin and storing it in Bitmap bmp
            Utils.matToBitmap(fin,image);
            // Closes the file input stream and releases any system resources
            is.close();
        } catch (Exception e) {
            // Error Handling
            e.printStackTrace();
        }
        // Assign the data in bmp to the ImageView
        iv_image.setImageBitmap(image);
    }

    /**
     * This function is to change the brightness of the source Bitmap
     * @param bitmap The Bitmap which is to be processed
     * @param value Value by which image is to brightened
     * @return The processed Bitmap
     */
    private Bitmap increaseBrightness(Bitmap bitmap, int value) {
        // New Mat of the same size of the source Bitmap
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        // Store the data of the bitmap Bitmap to src Mat
        Utils.bitmapToMat(bitmap, src);
        // Updating the brightness of the image corresponding to the src Mat
        src.convertTo(src, -1, 1, value);
        // Creating the result Bitmap having same size as that of src Mat
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        // Store the data of the src Mat to result Bitmap
        Utils.matToBitmap(src, result);
        // Return the processed bitmap
        return result;
    }

    /**
     * This function is to change the contrast of the source Bitmap
     * @param src The Bitmap which is to be processed
     * @param value Value by which contrast is to be changed
     * @return The processed Bitmap
     */
    private Bitmap adjustedContrast(Bitmap src, double value) {
        // Integer variable storing width of source Bitmap
        int width = src.getWidth();
        // Integer variable storing height of source Bitmap
        int height = src.getHeight();
        // Create output bitmap of the same size of the source Bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // Create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);
        // Draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));
        // Color information
        int A, R, G, B;
        int pixel;
        // Get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);
        // Scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // Get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // Apply filter contrast for every channel R, G, B
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
                // Set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        // Return the output Bitmap
        return bmOut;
    }

    /**
     * Function defining how to process the image Bitmap using data from the SeekBars
     * @return The processed Bitmap
     */
    private Bitmap brightness() {
        //Integer variable to store progress of the SeekBar progressBrightness
        int progressBrightness = sb_brightness.getProgress();
        //Integer variable to store progress of the SeekBar progressContrast
        int progressContrast = sb_contrast.getProgress();
        // Getting height of the image Bitmap
        int height = image.getHeight();
        // Getting width of the image Bitmap
        int width = image.getWidth();
        // Defining a new Mat having same size as that of bmp Bitmap
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        // Creating a copy of bmo Bitmap in Bitmap.Config.ARGB_8888 Configuration
        Bitmap bmp32 = image.copy(Bitmap.Config.ARGB_8888, true);
        // Storing the data of the bmp32 in the InputImage Mat
        Utils.bitmapToMat(bmp32, InputImage);
        // Getting the factor by which image is to be downscaled
        double scale = Math.sqrt(image.getAllocationByteCount()/4000000);
        // Integer variable to store the height of the downscaled image
        int height1 = height;
        // Integer variable to store the width of the downscaled image
        int width1 = width;
        // To check whether image has to be downscaled or not
        if(scale>1){
            height1 = (int)(height/scale);
            width1 = (int) (width/scale);
        }
        // New Mat variable having same size as that of the downscaled image
        Mat help1 = new Mat(height1,width1,CvType.CV_8UC4);
        // New Size variable having same size as that of the downscaled image
        Size sz = new Size(width1,height1);
        // Downscaling the InputImage Mat and storing it in help1 Mat
        Imgproc.resize(InputImage,help1, sz);
        // Creating a bitmap having same size as that of the downscaled image
        Bitmap bitmap1 = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        // Storing the data from the help1 Mat to bitmap1 Bitmap
        Utils.matToBitmap(help1,bitmap1);
        // Editing the brightness of the bitmap1 and storing it in editedBrightness Bitmap
        Bitmap editedBrightness = increaseBrightness(bitmap1, progressBrightness);
        // Editing the brightness of the editedBrightness and storing it in fin Bitmap
        Bitmap fin = adjustedContrast(editedBrightness, progressContrast);
        // New Mat variable having same size as that of the actual image
        Mat help2 = new Mat(height,width,CvType.CV_8UC4);
        //New Mat variable having same size as that of the downscaled image
        Mat help3 = new Mat(height1,width1,CvType.CV_8UC4);
        // Storing the data from the fin Bitmap to help3 Mat
        Utils.bitmapToMat(fin,help3);
        // New Size variable having same size as that of the actual image
        Size sz2 = new Size(width,height);
        // Upscaling the the processed Mat to the actual size
        Imgproc.resize(help3,help2,sz2);
        // Creating a new bitmap having same size as that of the actual image
        Bitmap bitmap3 =  Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //Storing the data from the help2 Mat to bitmap3 Bitmap (having the finally processed Bitmap)
        Utils.matToBitmap(help2,bitmap3);
        // Returning processed Bitmap
        return bitmap3;
    }
    
    
    
    @Override
    /**
     * Define how to preform when SeekBar progress is being changed
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    
    @Override
    /**
     * Define how to preform when SeekBar starts tracking touch
     */
    public void onStartTrackingTouch(SeekBar seekBar) {
        
    }
    
    @Override
    /**
     * Define how to preform when SeekBar stops tracking touch
     */
    public void onStopTrackingTouch(SeekBar seekBar) {
        new AsyncTaskBrightness().execute();
    }

    /**
     * Function which helps to pass the processed image back to the Edit activity.
     */
    public void brightDone() {
        // Get a bitmap corresponding to the ImageView (which has processed image)
        Bitmap bmp = ((BitmapDrawable) iv_image.getDrawable()).getBitmap();
        //try-catch block for sending data back to the Edit activity
        try {
            // Creating a filename bitmap.png
            String filename = "bitmap.png";
            // Creating a new FileOutputStream with file filename
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            // Integer value to store height of the bmp Bitmap
            int height = bmp.getHeight();
            // Integer value to store height of the bmp Bitmap
            int width = bmp.getWidth();
            // Defining a double variable to store the factor by which image it to be downscaled
            double scale = 1;
            // To check whether image is to downscaled or not
            if(bmp.getAllocationByteCount()>1250000){
                scale = Math.sqrt(bmp.getAllocationByteCount()/1250000);
            }
            // Defining a new Mat having same size as that of bmp Bitmap
            Mat inputImage = new Mat(height, width, CvType.CV_8UC4);
            // Storing the data of the bmp in the InputImage Mat
            Utils.bitmapToMat(bmp,inputImage);
            // New Size variable having same size as that of the downscaled image
            Size sz = new Size((int) (width/scale),(int) (height/scale));
            // New Mat variable having same size as that of the downscaled image
            Mat fin = new Mat((int) (height/scale),(int) (width/scale),CvType.CV_8UC4);
            // Downscaling the InputImage Mat and storing it in fin Mat
            Imgproc.resize(inputImage,fin,sz);
            // Creating a bitmap having same size as that of the downscaled image
            Bitmap tosend = Bitmap.createBitmap((int) (width/scale),(int) (height/scale), Bitmap.Config.ARGB_8888);
            // Storing the data from the fin Mat to tosend Bitmap
            Utils.matToBitmap(fin,tosend);
            // Store the data in fin Bitmap to stream
            tosend.compress(Bitmap.CompressFormat.PNG, 0, stream);
            // Closes this file output stream and releases any system resources associated with this stream.
            stream.close();
            // Initializes a new intent variable
            Intent intent = new Intent();
            // Initializes a new bundle variable
            Bundle b = new Bundle();
            // Storing the actual height of the bitmap in the "height" key
            b.putInt("height", height);
            // Storing the actual width of the bitmap in the "width" key
            b.putInt("width",width);
            // Storing the path of the file in the "image" key
            b.putString("image", filename);
            // Putting this extra information in the intent Intent
            intent.putExtras(b);
            // To go back to Edit Activity with result RESULT_OK
            setResult(RESULT_OK,intent);
            // End this activity
            finish();
        } catch (Exception e) {
            // Error Handling
            e.printStackTrace();
        }
    }

    /**
     * AsyncTask to help execution of progressBar
     */
    private class AsyncTaskBrightness extends AsyncTask<Void, Void, Bitmap> {
        
        @Override
        /**
         * Defining what to doInBackground
         * @param args Argument to the doInBackground function
         * @return Returns processed bmp Bitmap
         */
        protected Bitmap doInBackground(Void... args) {
            // Processing the bmp Bitmap and storing it in help Bitmap
            Bitmap help =  brightness();
            //Return processed Bitmap
            return help;
        }
        
        @Override
        /**
         * Defines what to do after the execution of doInBackground function
         * @param result The result passed on by doInBackground
         */
        protected void onPostExecute(Bitmap result) {
            // Make progressBar invisible
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            // Set the image corresponding to the processed bitmap to iv_image ImageView
            iv_image.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Make progressBar visible
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}

