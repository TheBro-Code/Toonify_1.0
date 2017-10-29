package com.example.syamantak.toonify_10;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity {

    private static final String TAG = "syamantak debug";
    static
    {
        if(!OpenCVLoader.initDebug())
        {
            Log.d(TAG,"OpenCV not loaded");
        }
        else
        {
            Log.d(TAG,"OpenCV loaded");
        }
    }
    // The variables REQUEST_CAMERA, SELECT_FILE, INTERFACE are Request Codes for the corresponding user-actions
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, INTERFACE = 2;
    // The Button take when pressed is used for taking picture from camera
    private Button take;
    // The image view ivImage is used to set the image on the screen
    private ImageView ivImage;
    private Bitmap image;
    private String userChoosenTask;
    private Intent CropIntent;
    private Uri uri;

    /**
     * The onCreate function is called when the activity first starts
     * The savedInstanceState is a Bundle in which the state of the application is saved in case the activity needs to be re-created such as in the case of a change in the orientation of the screen
     * setContentView is used for setting the view of the Activity using the xml file acitivity_filters
     * The ImageView ivImage is set by using the findViewById method on the resource file for the activity i.e activity_filters with the id iv_image and setting the bitmap on the ImageView
     * The Button take is set to the button by using the findViewById method on the resource file for the activity i.e activity_filters with the id ivImage
     * On clicking the button take, the method cameraIntent() is called
     * The gallery image thumbnail is set in the ImageView using the findViewById with the id thumbnail
     * On clicking the thumbnail the galleryIntent() method is called
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        take = (Button) findViewById(R.id.take);
        take.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });
        final ImageView imageView = (ImageView) findViewById(R.id.thumbnail);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
            }
        });
        // Find the last picture
    }

    /**
     * The method checks if the relevant permissions for the Camera and Gallery access have been provided
     * If the relevant permissions have been provided then the cameraIntent and galleryIntent functions are called when required
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    /**
     * A new intent is created which takes image from the the gallery
     * The startActivityForResult method is called to start the intent to select the image from the gallery
     */
    private void galleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    /**
     * The String pictureImagePath is used to store the path of the image
     */
    private String pictureImagePath = "";

    /**
     * The String timeStamp stores a string based on the Format provided which is used to generate a unique name for each image
     * The imagefileName contains the name of the image which is appended with ".jpg"
     * The storageDir contains the path of the Directory where the image will be saved
     * The pictureImagePath is the path of the image which is generated by appending the path of the storage directory with "/" and the imagefileName
     * A file is created with the path of the pictureImagePath
     * The URI (Unique Resource Identifier) of the file is extracted into the variable outputFileUri
     * The URI is also stored into the variable uri which is used in the crop function
     * A new Intent is created to take Image from the camera and it is stored in the the file created at the path of the pictureImagePath
     * The startActivityForResult is used to start the cameraIntent
     */
    private void cameraIntent() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = FileProvider.getUriForFile(MainActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        uri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    /**
     * The onActivityResult method is called after the activity for each result is concluded
     * The resultCode is verified to be equal to Activity.RESULT_OK which is a constant to specify that the acitvity concluded correctly
     * If the requestCode was SELECT_FILE then the method onSelectFromGallery(data) is called
     * If the requestCode was REQUEST_CAMERA then the method onCaptureImageResult(data) is called
     * If the requestCode was INTERFACE then the method dummy() is called
     * If the requestCode was 4 then the method onCrop(data) is called
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
            else if (requestCode == INTERFACE){
                dummy();
            }
            else if(requestCode == 4){
                onCrop(data);
            }
        }
    }

    /**
     * This is a dummy function since we do not have to perform any operation after the interface activity concludes
     */
    private void dummy(){}

    /**
     * The onCaptureImageResult method is called to process the saved image. If the image is saved then it decodes the file and saves it into a Bitmap image.
     * After saving the Bitmap, the method CropImage() is called which allows the user to crop the input image
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        File imgFile = new File(pictureImagePath);
        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            image = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
            CropImage();
        }
    }

    /**
     * The onSelectFromGalleryResult method is called after the conclusion of the galleryIntent and it calls the CropImage() function to allow the user to crop the image
     */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        uri = data.getData();
        CropImage();
    }

    /**
     * The filepath variable stores the path of the variable
     * The file is decoded and stored in the Bitmap image
     * The method Interface() is called
     * @param data
     */
    private void onCrop(Intent data){
        String filePath = Environment.getExternalStorageDirectory()
                + "/temporary_holder.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeFile(filePath, options);
        Interface();
    }

    /**
     * The string filename stores "bitmap.png"
     * A FileOutputStream stream is created with the filename
     * The variables height and width stores dimensions of the original image
     * The variable scale is greater than one when the file size than a certain size
     * A new Mat inputImage is created and the Bitmap is stored into the Mat
     * The Mat inputImage is resized and downsampled by a scale factor of the variable scale
     * The resized Mat is converted to a Bitmap toSend which is to be sent to the Interface activity
     * Before sending the Bitmap to the Interface Avtivity it is compressed to make the process of sending data between intents faster
     * A new intent is created which opens the Interface class
     * The orginal height, width and the filename are passed to the intent since they will be used to again up-scale the downsampled image before further processing
     * The StartActivityForResult method is called
     */
    private void Interface(){
        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            int height = image.getHeight();
            int width = image.getWidth();
            double scale = 1;
            if(image.getAllocationByteCount()>1250000){
                scale = Math.sqrt(image.getAllocationByteCount()/1250000);
            }
            Mat inputImage = new Mat(height, width, CvType.CV_8UC4);
            Utils.bitmapToMat(image,inputImage);
            Size sz = new Size((int) (width/scale),(int) (height/scale));
            Mat fin = new Mat((int) (height/scale),(int) (width/scale),CvType.CV_8UC4);
            Imgproc.resize(inputImage,fin,sz);
            Bitmap tosend = Bitmap.createBitmap((int) (width/scale),(int) (height/scale), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(fin,tosend);
            tosend.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent(this, Interface.class);
            Bundle b = new Bundle();
            b.putInt("height", height);
            b.putInt("width",width);
            b.putString("image", filename);
            intent.putExtras(b);
            startActivityForResult(intent,INTERFACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The CropImage method is used to provide the user with the choice to crop the given image
     * A new Intent is opened which calls the the default android image cropping application of the phone
     * The Data and type of the Intent are set as uri of the image file and image/*
     * The Intent is provided the parameters "crop" as "true" and "scaleupifNeeded" as "true" to scale up the bitmap if needed to fit the specified size of the Bitmap
     * A new file is created at the path the path of the external storage Directory of the device with the name "/temporary_holder.jpg"
     * The uri of the file created is also provided to the CropIntent and then the startActivityForResult method is called for the the CropIntent with the Request Code 4
     */
    private void CropImage()
    {
        try
        {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri,"image/*");
            CropIntent.putExtra("crop","true");
            CropIntent.putExtra("scaleupIfNeeded",true);
            File f = new File(Environment.getExternalStorageDirectory(),
                    "/temporary_holder.jpg");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            uri = Uri.fromFile(f);
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(CropIntent,4);
        }
        catch (ActivityNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}
