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
import android.widget.ImageView;

import com.example.syamantak.toonify_10.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Edit extends AppCompatActivity {
    private int EDIT_FILTER = 0, EDIT_CONTBRIT = 1, EDIT_HSV = 2;
    private ImageView edit_iv;
    private Button edit_filter, edit_contbrit, edit_hsv, edit_done;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edit_iv = (ImageView) findViewById(R.id.edit_iv);
        edit_filter = (Button) findViewById(R.id.edit_filter);
        edit_hsv = (Button) findViewById(R.id.edit_hsv);
        edit_done = (Button) findViewById(R.id.edit_done);
        edit_contbrit = (Button) findViewById(R.id.edit_contbrit);
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
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        edit_iv.setImageBitmap(bmp);
    }
    private void editFilter(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();

        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent(this, Filters.class);
            intent.putExtra("image", filename);
            startActivityForResult(intent, EDIT_FILTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editHSV(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();

        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent(this, HSV.class);
            intent.putExtra("image", filename);
            startActivityForResult(intent, EDIT_HSV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editContbrit(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();

        try {
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            stream.close();
            Intent intent = new Intent(this, Brightness.class);
            intent.putExtra("image", filename);
            startActivityForResult(intent, EDIT_CONTBRIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editDone(){
        Bitmap bmp = ((BitmapDrawable) edit_iv.getDrawable()).getBitmap();

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
            edit_iv.setImageBitmap(bmp);
        }
    }
}
