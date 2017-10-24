package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filters extends AppCompatActivity {
    private ImageView filter_iv;
    private Button filters_done;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
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
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        addCartoonify(layout);
        addPencilSketch(layout);
        addSepia(layout);
        addGrayscale(layout);
    }

    private void addCartoonify(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(100);
        imageView.setMaxWidth(100);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(100,100,CvType.CV_8UC4);
        Size sz = new Size(100,100);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doCartoonify(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap help = doCartoonify(bmp);
                filter_iv.setImageBitmap(help);
            }
        });
        layout.addView(imageView);
    }
    private void addPencilSketch(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(100);
        imageView.setMaxWidth(100);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(100,100,CvType.CV_8UC4);
        Size sz = new Size(100,100);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doPencilSketch(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap help = doPencilSketch(bmp);
                filter_iv.setImageBitmap(help);
            }
        });
        layout.addView(imageView);
    }
    private void addSepia(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(100);
        imageView.setMaxWidth(100);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(100,100,CvType.CV_8UC4);
        Size sz = new Size(100,100);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doSepia(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap help = doSepia(bmp);
                filter_iv.setImageBitmap(help);
            }
        });
        layout.addView(imageView);
    }
    private void addGrayscale(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(100);
        imageView.setMaxWidth(100);
        Mat help1 = new Mat(bmp.getHeight(),bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp,help1);
        Mat help2 = new Mat(100,100,CvType.CV_8UC4);
        Size sz = new Size(100,100);
        Imgproc.resize(help1,help2,sz);
        Bitmap bmpPrime = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(help2,bmpPrime);
        imageView.setImageBitmap(doOilSketch(bmpPrime));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap help = doOilSketch(bmp);
                filter_iv.setImageBitmap(help);
            }
        });
        layout.addView(imageView);
    }
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
        double scale = Math.sqrt(bitmap.getAllocationByteCount()/1250000);
        if(scale>1){
            Size sz = new Size(width/scale, height/scale);
            Imgproc.resize(InputImage,resizeimage, sz);
        }
        else{
            resizeimage = InputImage;
        }
        Imgproc.cvtColor(InputImage, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(imgGray,imgGray, 7);
        Imgproc.Canny(imgGray, imgCanny, 50, 150);
        Imgproc.cvtColor(imgCanny,imgCanny,Imgproc.COLOR_GRAY2RGB);
        Core.bitwise_not(imgCanny,edgeImage);
        Imgproc.cvtColor(resizeimage, mrgbk, Imgproc.COLOR_BGRA2BGR);
        for(int i=0;i<14;i++)
        {
            if(i%2 == 0)Imgproc.bilateralFilter(mrgbk, dstMat, 9, 9, 7);

            else Imgproc.bilateralFilter(dstMat, mrgbk, 9, 9, 7);
        }
        Imgproc.bilateralFilter(mrgbk, dstMat, 9, 7, 7);
        Imgproc.cvtColor(dstMat, dstMat, Imgproc.COLOR_RGB2RGBA);
        Imgproc.cvtColor(edgeImage,edgeImage,Imgproc.COLOR_RGB2RGBA);
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
        Imgproc.cvtColor(InputImage, imgGray, Imgproc.COLOR_RGB2GRAY);
        Core.bitwise_not(imgGray,Inverted_Gray);
        org.opencv.core.Size s1 = new Size(21,21);
        Imgproc.GaussianBlur(Inverted_Gray, Inverted_Gray, s1, 0, 0);
        double scale = Math.sqrt(bitmap.getAllocationByteCount()/4000000);
        int height1 = height;
        int width1 = width;
        if(scale>1){
            height1 = (int)(height/scale);
            width1 = (int) (width/scale);
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
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2BGRA);
        Utils.matToBitmap(dst, bmp32);
        return bmp32;
    }
    public Bitmap doGrayscale(Bitmap src){
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        Mat help1 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Mat help2 = new Mat(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,help1);
        Imgproc.cvtColor(help1,help2,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(help2,bitmap);
        return bitmap;
    }

    public Bitmap doOilSketch(Bitmap src)
    {
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888,true);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Mat InputImage;
        Mat Oil_Image;
        Mat InputImage_1;
        Mat Oil_Image_1;
        Mat Final_Image;

        InputImage_1 = new Mat(height, width, CvType.CV_8UC4);
        Oil_Image_1 = new Mat(height, width, CvType.CV_8UC4);
        Final_Image = new Mat(height,width,CvType.CV_8UC4);

        Utils.bitmapToMat(bitmap, InputImage_1);
        Utils.bitmapToMat(bitmap, Oil_Image_1);

        double scale = Math.sqrt(bitmap.getAllocationByteCount()/1000000);
        int height1 = height;
        int width1 = width;
        if(scale>1){
            height1 = (int)(height/scale);
            width1 = (int) (width/scale);
        }

        InputImage = new Mat(height1, width1, CvType.CV_8UC4);
        Oil_Image = new Mat(height1, width1, CvType.CV_8UC4);

        Size sz = new Size(width1,height1);
        Imgproc.resize(InputImage_1,InputImage, sz);
        Imgproc.resize(Oil_Image_1,Oil_Image,sz);

        int radius = 5;
        int intensity_levels = 20;
        int size = 256/intensity_levels + 1;

        for (int i=radius;i<height1-radius;i++)
        {
            for(int j=radius;j<width1-radius;j++)
            {
                int[] intensityCount = new int[size];
                int[] averageR = new int[size];
                int[] averageG = new int[size];
                int[] averageB = new int[size];

                for(int k=0;k<size;k++)
                {
                    intensityCount[i] = 0;
                    averageB[i] = 0;
                    averageR[i] = 0;
                    averageG[i] = 0;
                }

                int currMax = -1;
                int maxindex = -1;

                for(int k = -radius;k<radius;k++)
                {
                    if(k!=0)
                    {
                        double[] bgrarray_vertical = InputImage.get(i + k, j);
                        double[] bgrarray_horizontal = InputImage.get(i, j + k);

                        int curIntensity_vertical = (int) (((bgrarray_vertical[0] + bgrarray_vertical[1] + bgrarray_vertical[2]) / 3) * intensity_levels) / 255;
                        int curIntensity_horizontal = (int) (((bgrarray_horizontal[0] + bgrarray_horizontal[1] + bgrarray_horizontal[2]) / 3) * intensity_levels) / 255;

                        intensityCount[curIntensity_horizontal]++;
                        intensityCount[curIntensity_vertical]++;

                        averageR[curIntensity_horizontal] += bgrarray_horizontal[0];
                        averageR[curIntensity_vertical] += bgrarray_vertical[0];

                        averageG[curIntensity_horizontal] += bgrarray_horizontal[1];
                        averageG[curIntensity_vertical] += bgrarray_vertical[1];

                        averageB[curIntensity_horizontal] += bgrarray_horizontal[2];
                        averageB[curIntensity_vertical] += bgrarray_vertical[2];
                    }
                }

                for(int h=0;h<size;h++)
                {
                    if(currMax < intensityCount[h])
                    {
                        currMax = intensityCount[h];
                        maxindex = h;
                    }
                }

                double []bgrarray = Oil_Image.get(i,j);

                if(currMax != 0)
                {
                    bgrarray[0] = averageR[maxindex] / currMax;
                    if(bgrarray[0] > 255)
                    {
                        bgrarray[0] = 255;
                    }
                    bgrarray[1] = averageG[maxindex] / currMax;
                    if(bgrarray[1] > 255)
                    {
                        bgrarray[1] = 255;
                    }
                    bgrarray[2] = averageB[maxindex] / currMax;
                    if(bgrarray[2] > 255)
                    {
                        bgrarray[2] = 255;
                    }
                }

                Oil_Image.put(i,j,bgrarray);
            }
        }

        Size sz2 = new Size(width,height);
        Imgproc.resize(Oil_Image,Final_Image, sz2);

        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Utils.matToBitmap(Final_Image,bmp32);
        return bmp32;
    }

    public void filtersDone(){
        Bitmap bmp = ((BitmapDrawable) filter_iv.getDrawable()).getBitmap();

        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent();
            intent.putExtra("image", filename);
            setResult(RESULT_OK,intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
