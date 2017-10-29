/**
 * Main project package containing all the java files of the activities.
 */
package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Resize extends AppCompatActivity {
    // Button variable corresponding to the button which saves the changes and goes back to Edit activity
    private Button done,doit;
    // EditText variable corresponding to the text fields which take input of height and width respectively
    private EditText heightInput, widthInput;
    // ImageView which shows the preview of the changes
    private ImageView imageDisplay;
    // Bitmap to store the image passed on by Edit Activity
    private Bitmap bmp;
    @Override
    /**
     * The main java class of the Resize Activity. This class has all the functionality that happens in the Rotate activity part of the project(which helps Rotating the image).
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This function call sets the layout to the on defined in activity_resize
        setContentView(R.layout.activity_resize);
        // done is assigned its corresponding Button
        done = (Button) findViewById(R.id.resize_done);
        // doit is assigned its corresponding Button
        doit = (Button) findViewById(R.id.resize_do);
        // heightInput is assigned its corresponding EditText
        heightInput = (EditText) findViewById(R.id.editTextHeight);
        // widthInput is assigned its corresponding EditText
        widthInput = (EditText) findViewById(R.id.editTextWidth);
        // imageDisplay is assigned the corresponding ImageView
        imageDisplay = (ImageView) findViewById(R.id.resize_iv);
        // This specifies that done() is to be called on the click of done Button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });
        // This specifies that done() is to be called on the click of done Button
        doit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doit();
            }
        });
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
     * This function does the resizing after taking values from the editable text fields
     */
    private void doit(){
        //Integer variable assigned from width textfield
        int width = Integer.parseInt(widthInput.getText().toString());
        //Integer variable assigned from height textfield
        int height = Integer.parseInt(heightInput.getText().toString());
        // Size variable from the given parameters
        Size sz = new Size(width,height);
        // Mat of size of original image to store the input
        Mat InputImage = new Mat(bmp.getHeight(),bmp.getWidth(),CvType.CV_8UC4);
        // Data of Bitmap bmp stored in InputImage Mat
        Utils.bitmapToMat(bmp,InputImage);
        // Mat of size required by user created
        Mat Final = new Mat(height,width,CvType.CV_8UC4);
        // InputImage Mat resized to the size required and stored in Mat Final
        Imgproc.resize(InputImage,Final,sz);
        // Bitmap of size required initialized
        Bitmap fin = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        // Data of Final Mat stored in fin Bitmap
        Utils.matToBitmap(Final,fin);
        // To show preview of the final image
        imageDisplay.setImageBitmap(fin);
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
