package com.example.human_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    File cascFile;
    ImageView inputView;
    ImageView outputView;
    HumanDetect humanDetect;
    static boolean basedCanny = true;

    static{
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        inputView = findViewById(R.id.imageView1);
        outputView = findViewById(R.id.imageView2);


        try {
            loadCascade();

            humanDetect = new HumanDetect(cascFile.getAbsolutePath());

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.human_detecion_process,
                    android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            loadImages();

        } catch (IOException e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1){
                    MainActivity.basedCanny = false;
                }else{
                    MainActivity.basedCanny = true;
                }
                loadImages();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void loadImages(){

        Bitmap bitmap =  BitmapFactory.decodeResource(getResources(),R.drawable.adam);

        this.inputView.setImageBitmap(bitmap);
        this.outputView.setImageBitmap(humanDetect.detect(bitmap,basedCanny));

    }

    public void loadCascade() throws IOException {

        InputStream is = getResources().openRawResource(R.raw.haarcascade_fullbody);

        File cascadeDir = getDir("cascade",Context.MODE_PRIVATE);

        cascFile = new File(cascadeDir,"haarcascade_fullbody.xml");

        FileOutputStream fos = new FileOutputStream(cascFile);

        byte[] buffer = new byte[4096];

        int bytesRead;

        while((bytesRead = is.read(buffer)) != -1){

            fos.write(buffer,0,bytesRead);
        }

        is.close();
        fos.close();
    }

}

