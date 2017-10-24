package com.example.syamantak.toonify_10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Interface extends AppCompatActivity {

    private Bitmap bmp;
    private ImageView interface_iv;
    private Button save_interface, edit_interface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
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

    private void saveFile(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Toonify";

        File outputDir= new File(path);


        outputDir.mkdirs();
        File newFile = new File(path+"/"+"test.png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }
    private void editImage(){
        Bitmap bmp = ((BitmapDrawable) interface_iv.getDrawable()).getBitmap();

        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent(this, Edit.class);
            intent.putExtra("image", filename);
            startActivityForResult(intent,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String filename = data.getStringExtra("image");
            try {
                FileInputStream is = this.openFileInput(filename);
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            interface_iv.setImageBitmap(bmp);
        }
    }
}
