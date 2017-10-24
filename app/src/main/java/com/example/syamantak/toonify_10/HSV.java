package com.example.syamantak.toonify_10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.content.Intent;
import android.view.View.OnClickListener;

import com.example.syamantak.toonify_10.R;

public class HSV extends AppCompatActivity {

    private Button done;
    private ImageView imageDisplay;
    private SeekBar hueBar, satBar, valBar;
    private TextView hueText, satText, valText;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsv);
        imageDisplay = (ImageView) findViewById(R.id.hsv_iv);
        done = (Button) findViewById(R.id.hsv_done);
        satText = (TextView) findViewById(R.id.textsat);
        hueText = (TextView) findViewById(R.id.texthue);
        valText = (TextView) findViewById(R.id.textval);
        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });
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
                hsv();
            }

        };
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageDisplay.setImageBitmap(bmp);
    }
    private void hsv(){
        int progressHue = hueBar.getProgress() - 256;
        int progressSat = satBar.getProgress() - 256;
        int progressVal = valBar.getProgress() - 256;

   /*
    * Hue (0 .. 360) Saturation (0...1) Value (0...1)
    */

        float hue = (float) progressHue * 360 / 256;
        float sat = (float) progressSat / 256;
        float val = (float) progressVal / 256;

        hueText.setText("Hue: " + String.valueOf(hue));
        satText.setText("Saturation: " + String.valueOf(sat));
        valText.setText("Value: " + String.valueOf(val));
        imageDisplay.setImageBitmap(updateHSV(bmp,hue,sat,val));
    }

    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat,
                             float settingVal) {

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
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

                index++;
            }
        }
        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);
    }
    private void done(){
        Bitmap bmp = ((BitmapDrawable) imageDisplay.getDrawable()).getBitmap();

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
