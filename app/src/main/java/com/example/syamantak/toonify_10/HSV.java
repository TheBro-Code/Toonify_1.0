/**
 * Main project package containing all the java files of the activities.
 */
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

/**
 * The main java class of the HSV Activity. This class has all the functionality that happens in the HSV activity part of the project(which helps editing the Hue, Saturation and the Value of the image).
 */
public class HSV extends AppCompatActivity {
    // Button variable corresponding to the button which saves the changes and moves project to Edit Activity
    private Button done;
    // ImageView variable for showing the preview of the edited image
    private ImageView imageDisplay;
    // SeekBar variables for changing the values of Hue, Satutration and Value respectively
    private SeekBar hueBar, satBar, valBar;
    // TextView variables for showing the current values of Hue, Satutration and Values in the preview respectively
    private TextView hueText, satText, valText;
    // Bitmap variable for storing the bitmap coming from the Edit activity
    private Bitmap bmp;
    // ProgressBar variable which will be visible when the image is being processed
    private ProgressBar progressBar;
    @Override
    /**
     * This function sets the layout of the activity, assigns the global value to their corresponding values by linking them to the ids of the objects on it's creation.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This function call sets the layout to the on defined in activity_hsv
        setContentView(R.layout.activity_hsv);
        // imageDisplay is assigned the corresponding ImageView
        imageDisplay = (ImageView) findViewById(R.id.hsv_iv);
        // done is assigned the corresponding Button
        done = (Button) findViewById(R.id.hsv_done);
        // satText is assigned the corresponding TextView
        satText = (TextView) findViewById(R.id.textsat);
        // hueText is assigned the corresponding TextView
        hueText = (TextView) findViewById(R.id.texthue);
        // valText is assigned the corresponding TextView
        valText = (TextView) findViewById(R.id.textval);
        // hueBar is assigned the corresponding SeekBar
        hueBar = (SeekBar) findViewById(R.id.huebar);
        // satBar is assigned the corresponding SeekBar
        satBar = (SeekBar) findViewById(R.id.satbar);
        // valBar is assigned the corresponding SeekBar
        valBar = (SeekBar) findViewById(R.id.valbar);
        // progressBar is assigned the corresponding ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.hsv_pb);
        // This specifies that function done() is to be called on the click of done button
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });
        // A new OnSeekBarChangeListener is defined to assign to all the seekbars
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
        // OnSeekBarChangeListener of hueBar is assigned to be seekBarChangeListener
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        // OnSeekBarChangeListener of satBar is assigned to be seekBarChangeListener
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        // OnSeekBarChangeListener of valBar is assigned to be seekBarChangeListener
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);
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
            bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            // Getting the data from the Mat fin and storing it in Bitmap bmp
            Utils.matToBitmap(fin,bmp);
            // Closes the file input stream and releases any system resources
            is.close();
        } catch (Exception e) {
            // Error Handling
            e.printStackTrace();
        }
        // Assign the data in bmp to the ImageView
        imageDisplay.setImageBitmap(bmp);
    }

    /**
     * This function defines how to update the image given the HSV values.
     * @param src The bitmap source which is to be updated
     * @param settingHue The value of Hue to be assigned
     * @param settingSat The value of Saturation to be assigned
     * @param settingVal The value of Value to be assigned
     * @return The updated Bitmap
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
        // Integer variable to store width of source Bitmap
        int w = src.getWidth();
        // Integer variable to store height of source Bitmap
        int h = src.getHeight();
        // Integer array to store colors of source Bitmap
        int[] mapSrcColor = new int[w * h];
        // Integer variable to store colors of final Bitmap
        int[] mapDestColor = new int[w * h];
        // Float array for storing HSV for each pixel
        float[] pixelHSV = new float[3];
        // Assigning the colors of the source Bitmap to mapSrcColor
        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);
        // Initializing index of mapDestColor
        int index = 0;
        //Iteration iterating over every pixel of the Bitmap
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
                // Incrementing index so as to reach every pixel
                index++;
            }
        }
        // Creating a bitmap from the array mapDestColor and returning it
        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);
    }

    /**
     * Function defining how to process the bmp Bitmap using data from the SeekBars
     * @return Finally processed bmp Bitmap
     */
    private Bitmap hsv(){
        //Integer variable to store progress of the SeekBar hueBar - 256
        int progressHue = hueBar.getProgress() - 256;
        //Integer variable to store progress of the SeekBar satBar - 256
        int progressSat = satBar.getProgress() - 256;
        //Integer variable to store progress of the SeekBar valBar - 256
        int progressVal = valBar.getProgress() - 256;
        //Float variable to get the Hue value to be assigned (between -360 to 360)
        float hue = (float) progressHue * 360 / 256;
        //Float variable to get the Saturation value to be assigned (between -1 to -1)
        float sat = (float) progressSat / 256;
        //Float variable to get the Value value to be assigned (between -1 to -1)
        float val = (float) progressVal / 256;
        // Getting height of the bmp Bitmap
        int height = bmp.getHeight();
        // Getting width of the bmp Bitmap
        int width = bmp.getWidth();
        // Defining a new Mat having same size as that of bmp Bitmap
        Mat InputImage = new Mat(height,width,CvType.CV_8UC4);
        // Creating a copy of bmo Bitmap in Bitmap.Config.ARGB_8888 Configuration
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        // Storing the data of the bmp32 in the InputImage Mat
        Utils.bitmapToMat(bmp32, InputImage);
        // Getting the factor by which image is to be downscaled
        double scale = Math.sqrt(bmp.getAllocationByteCount()/4000000);
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
        // Creating a bitmap having same size as that of the downscaled image
        Bitmap bitmap2 =  Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        // Assigning bitmap2 the processed bitmap1
        bitmap2 = updateHSV(bitmap1,hue,sat,val);
        // New Mat variable having same size as that of the actual image
        Mat help2 = new Mat(height,width,CvType.CV_8UC4);
        //New Mat variable having same size as that of the downscaled image
        Mat help3 = new Mat(height1,width1,CvType.CV_8UC4);
        // Storing the data from the bitmap2 Bitmap to help3 Mat
        Utils.bitmapToMat(bitmap2,help3);
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

    /**
     * AsyncTask to help execution of progressBar
     */
    private class AsyncTaskHSV extends AsyncTask<Void, Void, Bitmap> {

        /**
         * Defining what to doInBackground
         * @param args Argument to the doInBackground function
         * @return Returns processed bmp Bitmap
         */
        @Override
        protected Bitmap doInBackground(Void... args) {
            // Processing the bmp Bitmap and storing it in help Bitmap
            Bitmap help = hsv();
            //Return processed Bitmap
            return help;
        }

        /**
         * Defines what to do after the execution of doInBackground function
         * @param result The result passed on by doInBackground
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            //Integer variable to store progress of the SeekBar hueBar - 256
            int progressHue = hueBar.getProgress() - 256;
            //Integer variable to store progress of the SeekBar satBar - 256
            int progressSat = satBar.getProgress() - 256;
            //Integer variable to store progress of the SeekBar valBar - 256
            int progressVal = valBar.getProgress() - 256;
            //Float variable to get the Hue value to be assigned (between -360 to 360)
            float hue = (float) progressHue * 360 / 256;
            //Float variable to get the Saturation value to be assigned (between -1 to -1)
            float sat = (float) progressSat / 256;
            //Float variable to get the Value value to be assigned (between -1 to -1)
            float val = (float) progressVal / 256;
            // Setting appropriate text of hueText TextView
            hueText.setText("Hue: " + String.valueOf(hue));
            // Setting appropriate text of satText TextView
            satText.setText("Saturation: " + String.valueOf(sat));
            // Setting appropriate text of valText TextView
            valText.setText("Value: " + String.valueOf(val));
            // Make progressBar invisible
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            // Set the image corresponding to the processed bitmap to imageDisplay ImageView
            imageDisplay.setImageBitmap(result);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Make progressBar visible
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Function which helps to pass the processed image back to the Edit activity.
     */
    private void done(){
        // Get a bitmap corresponding to the ImageView (which has processed image)
        Bitmap bmp = ((BitmapDrawable) imageDisplay.getDrawable()).getBitmap();
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
}

