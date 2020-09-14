package com.mukesh.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okio.Utf8;

import static android.util.Base64.CRLF;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myRef, buttonState, imageRef,captureImageRef,captureButton,liveButton;
    Button buttonClick, buttonPush;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonPush = (Button) findViewById(R.id.buttonPush);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        buttonState = database.getReference("buttonState");
        myRef = database.getReference("led");
        imageRef = database.getReference("esp32-cam/photo");
        captureImageRef = database.getReference("esp32-capture/capture");
        captureButton = database.getReference("captureButton");
        liveButton = database.getReference("liveview");
        liveButton.setValue("OFF");



        //-------------------------
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                String imageStr = value.split(",")[1];



                try {
                    imageStr = URLDecoder.decode(imageStr, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                byte[] imageBytes = Base64.decode(imageStr.getBytes(), Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                ImageView clickableImage = findViewById(R.id.clickableImage);
                clickableImage.setRotation(90);
                clickableImage.setImageBitmap(decodedImage);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Failed to Read", Toast.LENGTH_SHORT).show();
            }

        });

        //-----------------------------

        buttonPush.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    myRef.setValue("1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    myRef.setValue("0");
                }
                return true;
            }
        });


    }

    public void switchButton(View view) {
        buttonState.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                assert value != null;
                if (value.equals("low")) {
                    myRef.setValue("1");
                    buttonState.setValue("high");
                } else if (value.equals("high")) {
                    myRef.setValue("0");
                    buttonState.setValue("low");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Failed to Read", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void imageButton(View view) {
        liveButton.setValue("OFF");
        captureButton.setValue("clicked");
        captureImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
             
                String imageStr = value.split(",")[1];



                try {
                    imageStr = URLDecoder.decode(imageStr, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                byte[] imageBytes = Base64.decode(imageStr.getBytes(), Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                ImageView clickableImage = findViewById(R.id.clickableImage);
                clickableImage.setRotation(90);
                clickableImage.setImageBitmap(decodedImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Failed to Read", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void liveCamera(View view) {

        liveButton.setValue("ON");
    }
}