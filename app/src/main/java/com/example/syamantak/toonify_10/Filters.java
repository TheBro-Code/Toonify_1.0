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
    // ImageView fliter_iv is used for setting the thumbnail of the the input image in the linear layout
    private ImageView filter_iv;
    // Button filters_done is used after the desired action is performed to return to the previous activity
    private Button filters_done;
    // Bitmap bmp is the global Bitmap of the input image
    private Bitmap bmp;
    // Progressbar progressBar is the progress bar which is used to show the progress between operations
    private ProgressBar progressBar;

    /**
     * The onCreate function is called when the activity first starts
     * The savedInstanceState is a Bundle in which the state of the application is saved in case the activity needs to be re-created such as in the case of a change in the orientation of the screen
     * setContentView is used for setting the view of the Activity using the xml file acitivity_filters
     * The Bundle b is used for extracting the data from the previous intent using the function getIntent()
     * The variable filename stores the name of the file with the key "image" among the data from the previous intent
     * The variables height and width store the value of the variables with the key values "height" and "width" in the previous intent
     * FileInputStream filestream is used for reading the file which was stored in the variable filename
     * The filestream is converted to a Bitmap inpBmp using the function BitmapFactory.decodeStream
     * The Bitmap is converted into a Mat inp using the Utils.bitmapToMat
     * To make the transition between different activities smoother and transfer the image faster we had compressed the bitmap while sending the data from one intent to the other therefore we have to resize the Mat inp to the original height and width
     * The Mat inp is resized and stored in the global variable bmp
     * The filestream is closed
     * The ImageView is set by using the findViewById method on the resource file for the activity i.e activity_filters with the id filter_iv and setting the bitmap on the ImageView
     * The Button filters_done is set to the button by using the findViewById method on the resource file for the activity i.e activity_filters with the id filters_done
     * On clicking the button the filtersDone() method is called.
     * A ProgressBar is initialised with the findViewById method through the id progressBar
     * The LinearLayout is initialised with the findViewById method through the id linear
     * The methods
     * addCartoonify(layout)
     * addPencilSketch(layout)
     * addSepia(layout)
     * addVignette(layout)
     * addGrayscale(layout)
     * addColorEmboss(layout)
     * addGrayscaleEmboss(layout)
     * addReflection(layout) are called to display the miniature versions of the filters applied to the input image in the linear layout
     * @param savedInstanceState
     * @return nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Bundle b = getIntent().getExtras();
        String filename = b.getString("image");
        int height = b.getInt("height");
        int width = b.getInt("width");
        try {
            FileInputStream filestream = this.openFileInput(filename);
            Bitmap inpBmp = BitmapFactory.decodeStream(filestream);
            Mat inp = new Mat(inpBmp.getHeight(),inpBmp.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(inpBmp,inp);
            Size sz = new Size(width,height);
            Mat fin = new Mat(height,width,CvType.CV_8UC4);
            Imgproc.resize(inp,fin,sz);
            bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin,bmp);
            filestream.close();
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
        addInvert(layout);
    }

    /**
     * creates a new Image vies sets it to a smaller cartoonified version image of
     * the current image. Also when the user clicks on this ImageView, the app starts
     * performing a background process of cartoonify using AsyncTask class.
     * @param layout - the layout on which the created ImageView will be added
     * @return void
     */
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

    /**
     * This class allows you to perform background operations and publish
     * results on the UI thread without having to manipulate threads and/or handlers.
     */
    private class AsyncTaskCartoonify extends AsyncTask<Void, Void, Bitmap> {

        /**
         * This method calls the doCartoonify process in the background.
         * @param args
         * @return returns a bitmap of the cartoonified image
         */

        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doCartoonify(bmp);
            return help;
        }

        /**
         * This method set the visibility of the progress bar to invisible on post Execution
         * of AsynTask
         * @param result - the bitmap of cartoonified version of the current image
         * @return void
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            filter_iv.setImageBitmap(result);
        }

        /**
         * This method sets the visibilty of progress bar to visible on preExecutiion of AsyncTask
         * @return void
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * The addPencilSketch method is used for adding the thumbnail of the pencil sketch applied to the input image to the linear layout
     * The ImageView is created with a size of 350 by 350 pixels and the Input image is resized
     * To resize the image the bitmap is first read into the Mat help1 then it is resized using Imgproc.resize() into help2
     * The Mat help2 is converted into a Bitmap bmpPrime and the method doPencilSketch() is called on it
     * The imageview is added to the linear layout
     * On clicking the imageView the function AsyncTaskPencilSketch().execute() is called to generate the pencilsketch of the image.
     * @param layout
     */
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

    /**
     * The class AsyncTaskPencilSketch is used to run the ProgressBar and the doPencilSketch asynchronously
     * In the doInBackground method the doPencilSketch method is called on the global bitmap bmp
     * In the onPreExecute method the Progress Bar becomes visible
     * In the onPostExecute method the Progress Bar becomes invisible
     */
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

    /**
     * The addSepia method is used for adding the thumbnail of the sepia filter applied to the input image to the linear layout
     * The ImageView is created with a size of 350 by 350 pixels and the Input image is resized
     * To resize the image the bitmap is first read into the Mat help1 then it is resized using Imgproc.resize() into help2
     * The Mat help2 is converted into a Bitmap bmpPrime and the method doSepia() is called on it
     * The imageview is added to the linear layout
     * On clicking the imageView the function AsyncTaskPencilSketch().execute() is called to generate the sepia filtered output of the image.
     * @param layout
     */
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

    /**
     * The class AsyncTaskAddSepia is used to run the ProgressBar and the doSepia asynchronously
     * In the doInBackground method the doSepia method is called on the global bitmap bmp
     * In the onPreExecute method the Progress Bar becomes visible
     * In the onPostExecute method the Progress Bar becomes invisible
     */
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

    /**
     * The addGrayscale is used for adding the thumbnail of the gray scale applied to the input image to the linear layout
     * The ImageView is created with a size of 350 by 350 pixels and the Input image is resized
     * To resize the image the bitmap is first read into the Mat help1 then it is resized using Imgproc.resize() into help2
     * The Mat help2 is converted into a Bitmap bmpPrime and the method doGrayScale() is called on it
     * The imageview is added to the linear layout
     * On clicking the imageView the function AsyncTaskPencilSketch().execute() is called to generate the gray scaled output of the image.
     * @param layout
     */
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

    /**
     * The class AsyncTaskGrayScale is used to run the ProgressBar and the doGrayScale asynchronously
     * In the doInBackground method the doGrayScale method is called on the global bitmap bmp
     * In the onPreExecute method the Progress Bar becomes visible
     * In the onPostExecute method the Progress Bar becomes invisible
     */
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

    /**
     * The addGrayscale is used for adding the thumbnail of the inverted-gray scale applied to the input image to the linear layout
     * The ImageView is created with a size of 350 by 350 pixels and the Input image is resized
     * To resize the image the bitmap is first read into the Mat help1 then it is resized using Imgproc.resize() into help2
     * The Mat help2 is converted into a Bitmap bmpPrime and the method doInvert() is called on it
     * The imageview is added to the linear layout
     * On clicking the imageView the function AsyncTaskPencilSketch().execute() is called to generate the inverted-gray scaled output of the image.
     * @param layout
     */
    private void addInvert(LinearLayout layout){
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
        imageView.setImageBitmap(doInvert(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTaskInvert().execute();
            }
        });
        layout.addView(imageView);
    }

    /**
     * The class AsyncTaskGrayScale is used to run the ProgressBar and the doInvert asynchronously
     * In the doInBackground method the doInvert method is called on the global bitmap bmp
     * In the onPreExecute method the Progress Bar becomes visible
     * In the onPostExecute method the Progress Bar becomes invisible
     */
    private class AsyncTaskInvert extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap help = doInvert(bmp);
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
    /**
     * The addReflection is used for adding the thumbnail of the applyReflection applied to the input image to the linear layout
     * The ImageView is created with a size of 350 by 350 pixels and the Input image is resized
     * To resize the image the bitmap is first read into the Mat help1 then it is resized using Imgproc.resize() into help2
     * The Mat help2 is converted into a Bitmap bmpPrime and the method applyReflection() is called on it
     * The imageview is added to the linear layout
     * On clicking the imageView the function AsyncTaskPencilSketch().execute() is called to generate the Reflected output of the image.
     * @param layout
     */
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
    /**
     * The class AsyncTaskAddReflection is used to run the ProgressBar and the applyReflection asynchronously
     * In the doInBackground method the applyReflection method is called on the global bitmap bmp
     * In the onPreExecute method the Progress Bar becomes visible
     * In the onPostExecute method the Progress Bar becomes invisible
     */
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

    /**
     * This method is used to Cartonify the Image whose bitmap is given to it.
     * First the bitmap is copied to another bitmap
     * then a number of Mat objects are declared which will be used to hold images Mat during intermediate steps
     * Then the input bitmap is converted to Mat object and stores in InputImage
     * After which InputImage is resized if it's size is larger than a specified threshold for speed improvement for larger images.
     * The input image is then grayscaled and adaptive threshold is applied on it to create an image with just the edges present which is stored in edgeImage
     * Again on the InputImage repetitive bilateral filter is applied to smoothen out the image which is stored in dstMat
     * Finally both of them are merged by performing bitwise_and which gives us the final cartoonified image which is FinalImage
     * This FinalImage is converted to bitmap which is then returned
     * @param src - the bitmap of the image to be cartoonified
     * @return the bitmap of the cartoonifies Image
     */
    private Bitmap doCartoonify(Bitmap src){
        //Creating the copy of the original bitmap so that the original one is not changed
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

    /**
     * The method colordodge is used to perform color-dodge on the corresponding pixels of the image and mask
     * The Color Dodge blend mode divides the bottom layer by the inverted top layer. Here the top layer is the mask and bottom layer is the image
     * Inversion of a pixel value corresponds to subtraction of it's value from 255
     * Division is performed by first shifting the bits of the pixel from the top layer i.e the mask pixel by 8 bits to the left which is equivalent to multiplying the pixel value by 256, then it divided by (255 - image) to get the final value after color dodge which is returned by the function
     * If the value of the pixel in the bottom layer is 255 or after division the value exceeds 255 then the function returns 255
     * @param in1
     * @param in2
     * @return
     */
    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }

    /**
     * The Color Dodge Blend function is used in the Pencil Sketch method. After the gray-scaled image and the inverted gray-scaled image has been obtained, the bitmaps for the two images are passed to the function as parameters
     * The Bitmap of the Inverted Gray-Scaled image is Source and the Bitmap of the Gray-scaled image is Layer.
     * The Bitmaps are copied and stored in IntBuffers buffBase and buffBlend respectively. An output Buffer is also created.
     * The RGB channel values corresponding to each pixel of the Source and the Layer are taken and the colordodge function is applied to each of the them. The returned value is stored in the output Buffer.
     * After processing each value the output buffer is copied back to a bitmap which is returned by the function.
     * @param Source
     * @param Layer
     * @return
     */
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

    /**
     * @author The Bro-Code
     * The doPencilSketch function converts the input image into a pencil sketch.
     * The Bitmap src is copied into a Bitmap bitmap and is read into a Mat InputImage
     * InputImage is converted into a gray-scaled image imgGray using Imgproc.cvtColor with the parameter Imgproc.COLOR_RGB2GRAY
     * The inverted gray-scaled image is also saved in Inverted_Gray with the function Imgproc.bitwise_not
     * A Gaussian Blur is applied on the Inverted_Gray image using the function Imgproc.GaussianBlur
     * If the size of the input image is greater than 200 Kb then the gray-scaled and inverted gray-scaled images are resized into a smaller Mat to perform subsequent operations
     * The inverted gray-scaled and gray-scaled images are then converted into bitmaps and the function ColorDodgeBlend is applied on them to blend the bitmaps and return a bitmap
     * If the image was downscaled earlier then it is again upscaled to give the final image stored in the variable fin
     * The Bitmap atLast contains the final image converted to a bitmap which is then returned by the function
     * @param src
     * @return bmp32
     * @version 1.0
     */

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

    /**
     * @author The Bro-Code
     * The doSepia function applies the sepia filter to the input Image
     * The Bitmap src is read into a Bitmap bitmap and is converted into a Mat InputImage
     * The input image is splitted into it's three channels Red, Green and Blue
     * The new three channels are made by mutiplying each channel with appropriate constants and taking the linear combination
     * The new channels are merged together to form a new Mat dst which is further converted to a bitmap and returned
     * @param src
     * @return bmp32
     * @version 1.0
     */

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

    /**
     * @author The Bro-Code
     * The doGrayscale function converts a given input image into a gray-scaled image
     * The Bitmap src is first copied into the Bitmap bitmap
     * The Input Bitmap is read into the Mat help1 using the function Utils.bitmaptoMat()
     * The Mat help1 is transformed into the Mat help2 using the function Imgproc.cvtColor with the parameter Imgproc.COLOR_RGB2GRAY
     * The Mat help2 is again converted into a Bitmap and returned
     * @param src
     * @return bitmap
     * @version 1.0
     */

    public Bitmap doGrayscale(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        Mat help1 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Mat help2 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,help1);
        cvtColor(help1,help2,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(help2,bitmap);
        return bitmap;
    }

    /**
     * @author The Bro-Code
     * The doGrayscale function converts a given input image into an inverted gray-scaled image
     * The Bitmap src is first copied into the Bitmap bitmap
     * The Input Bitmap is read into the Mat help1 using the function Utils.bitmaptoMat()
     * The Mat help1 is transformed into the Mat help2 using the function Imgproc.cvtColor with the parameter Imgproc.COLOR_RGB2GRAY
     * Then a bitwise_not is performed on the Mat help2 to generate the inverted gray-scaled image which is stored into a Mat help3
     * The Mat help3 is again converted into a Bitmap and returned
     * @param src
     * @return bitmap
     * @version 1.0
     */

    public Bitmap doInvert(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        Mat help1 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Mat help2 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,help1);
        cvtColor(help1,help2,Imgproc.COLOR_RGB2GRAY);
        Mat help3 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Core.bitwise_not(help2,help3);
        Utils.matToBitmap(help3,bitmap);
        return bitmap;
    }

    /**
     * Applies the Emboss filter on the bitmap of the image
     * Initially the original bitmap is copied into a bitmap then converted into Mat ImageInput
     * Also a convolution matrix by the name ConvMat is create which is filled with specified values to apply the emboss filter
     * Then the filter2D function matrix is used to apply the filter using the created convMat on InputMat mat with bias and anchor set as default.
     * The filtered image is saves to Output_Image Mat object and then converted to bitmap which is then returned
     * @param src - the bitmap of the image on which the emboss effect is to be implemented
     * @return returs the bitmap of embossed image
     */
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

    /**
     * Applies the Emboss filter on the bitmap of the image
     * Initially the original bitmap is copied into a bitmap
     * Then the bitmap is converted to grayscale using doGrayscale method after which it is converted into Mat ImageInput
     * Also a convolution matrix by the name ConvMat is create which is filled with specified values so as to apply the emboss filter
     * Then the filter2D function is used to apply the filter using the created convMat on InputMat mat with bias and anchor set as default.
     * The filtered image is saves to Output_Image Mat object and then converted to bitmap which is then returned
     * @param src - the bitmap of the image on which the emboss effect is to be implemented
     * @return returs the bitmap of embossed image
     */
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


    /**
     * The method applyReflection is used to create a Reflection of the Input Image and display the final image with the the Reflection of the image appearing below the original image
     * The src Bitmap is copied into the Bitmap originalImage
     * The variable reflectionGap signifies the gap space between original and reflected image
     * An instance of the Matrix class is created and preConcatenated with the scale values (1,-1) which in effect flips the matrix on the Y axis
     * A new Bitmap is created with only the bottom half of the image and the matrix created previously is applied to rotate it about the Y-axis
     * Another Bitmap bitmapwithReflection is created to fit the original image along with the reflection with the same width but slightly taller
     * A new Canvas is created with the Bitmap bitmapwithreflection to accomodate the image along with the reflection
     * The original image is drawn into the canvas followed by the gap and the reflected image
     * A shader is created which is a linear gradient that covers the reflection to cause the effect of transparency for the reflecton
     * The LinearGradient begins at the originalImage height and ends at the bitmapwithreflection height + reflectionGap
     * The Canvas is drawn into the Bitmap bitmapwithreflection and it is returned by the funciton
     * @param src
     * @return
     */
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

    /**
     * The method rotate is used to rotate a given bitmap by given number of degrees
     * An instance of the Matrix is created and is with the matrix which transforms by the given degrees of rotation. The function postRotate is used for this purpose.
     * The function returns a Bitmap after applying the transformation prescribed by the matrix which is rotation by the given number of degrees
     * @param src
     * @param degree
     * @return bitmap
     */
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

    /**
     * Applies the Vignette effect on the bitmap of the input image.
     * The source bitmap is first copied into another bitmap.
     * a gradient is created that draws a radial gradient given the center and radius with center colour transparent and edge coloured black.
     * Then a canvas object is created with the originalBitmap to draw into and filled with R,G,B values of 0 and A(opacity) value of 1
     * Then a paint object is created with shader set to gradient and antialiasing turned on and colour black.
     * Then a rect object of size same as input bitmap is created after which a rectf object is created using rect.
     * Then the specified rect is drawn into canvas using the specified paint object created earlier.
     * This canvas is then drawn into the original Bitmap which is then returned.
     * @param src the bitmap of the image on which the filter has to applied
     * @return returns the bitmap of the image after applying the Vignette filter
     */
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

