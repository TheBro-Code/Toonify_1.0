/**
 * Main project package containing all the java files of the activities.
 */
package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

/**
 * The main java class of the Rotate Activity. This class has all the functionality that happens in the Rotate activity part of the project(which helps Rotating the image).
 */
public class Rotate extends AppCompatActivity {
    // Integer variable to store the angle by which the image is to be rotated
    private int angle = 0;
    // Button variable corresponding to the button which saves the changes and moves project to Edit Activity
    private Button done;
    // Button variables corresponding to the button which rotate the image anticlockwise and clockwise by 90 degrees respectively
    private Button antickcwise, ckcwise;
    // ImageView variable for showing the preview of the edited image
    private ImageView imageDisplay;
    // SeekBar for changing the value of angle by which image is to be rotated
    private SeekBar rotateBar;
    // TextView for showing the angle by which the image is currently rotated
    private TextView rotateText;
    // Bitmap variable for storing the bitmap coming from the Edit activity
    private Bitmap bmp;
    @Override
    /**
     * This function sets the layout of the activity, assigns the global value to their corresponding values by linking them to the ids of the objects on it's creation.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This function call sets the layout to the on defined in activity_rotate
        setContentView(R.layout.activity_rotate);
        // imageDisplay is assigned the corresponding ImageView
        imageDisplay = (ImageView) findViewById(R.id.hsv_iv);
        // done is assigned the corresponding Button
        done = (Button) findViewById(R.id.rotate_button);
        // antickcwise is assigned the corresponding Button
        antickcwise = (Button) findViewById(R.id.antickcwise_button);
        // ckcwise is assigned the corresponding Button
        ckcwise = (Button) findViewById(R.id.ckcwise_button);
        // This specifies that function rotateAntiCkcwise() is to be called on the click of antickcwise button
        antickcwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAntiCkcwise();
            }
        });
        // This specifies that function rotateCkcwise() is to be called on the click of ckcwise button
        ckcwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateCkcwise();
            }
        });
        // rotateText is assigned the corresponding TextView
        rotateText = (TextView) findViewById(R.id.rotate_tv);
        // rotateText is assigned the corresponding SeekBar
        rotateBar = (SeekBar) findViewById(R.id.rotate_sb);
        // This specifies that function done() is to be called on the click of done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });
        // A new OnSeekBarChangeListener is defined to assign to the SeekBar
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
        // OnSeekBarChangeListener of rotateBar is assigned to be seekBarChangeListener
        rotateBar.setOnSeekBarChangeListener(seekBarChangeListener);
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
     * Function specifying what to do on release of SeekBar rotateBar
     */
    private void rotate() {
        // Sets the value of angle to the new value obtained by SeekBar(to be between -180 to 179)
        angle = rotateBar.getProgress() - 180;
        // Setting appropriate text of rotateText TextView
        rotateText.setText("Angle :"+angle);
        // Creating a new Matrix
        Matrix matrix = new Matrix();
        // Setup rotation angle
        matrix.postRotate(angle);
        // Create a rotated Bitmap from Matrix matrix
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                                             matrix,false);
        // Set imageDisplay to the image corresponding to the Bitmap rotated
        imageDisplay.setImageBitmap(rotated);
    }

    /**
     * Function to rotate image 90 degrees anticlockwise
     */
    private void rotateAntiCkcwise(){
        // Reducing the value stored in angle by 90
        angle = angle-90;
        // Making sure that angle value is between -180 to 179
        if(angle<-180){
            angle = angle+360;
        }
        // Setting rotateBar appropriate value
        rotateBar.setProgress(angle+180);
        // Setting appropriate text of rotateText TextView
        rotateText.setText("Angle :"+angle);
        // Creating a new Matrix
        Matrix matrix = new Matrix();
        // Setup rotation angle
        matrix.postRotate(angle);
        // Create a rotated Bitmap from Matrix matrix
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix,false);
        // Set imageDisplay to the image corresponding to the Bitmap rotated
        imageDisplay.setImageBitmap(rotated);
    }
    
    private void rotateCkcwise(){
        // Increasing the value stored in angle by 90
        angle = angle+90;
        // Making sure that angle value is between -180 to 179
        if(angle>179){
            angle = angle-360;
        }
        // Setting rotateBar appropriate value
        rotateBar.setProgress(angle+180);
        // Setting appropriate text of rotateText TextView
        rotateText.setText("Angle :"+angle);
        // Creating a new Matrix
        Matrix matrix = new Matrix();
        // Setup rotation angle
        matrix.postRotate(angle);
        // Create a rotated Bitmap from Matrix matrix
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix,false);
        // Set imageDisplay to the image corresponding to the Bitmap rotated
        imageDisplay.setImageBitmap(rotated);
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

