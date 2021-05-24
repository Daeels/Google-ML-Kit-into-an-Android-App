package com.example.mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextToSpeech t1;
    private EditText write;
    TextView tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tb = findViewById(R.id.tb);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                t1.setLanguage(Locale.FRANCE);
            }
        });
    }

    Intent myIntent;
    Uri uri;



    public void load_image(View view){

        myIntent = new Intent();
        myIntent.setType("image/*");
        myIntent.setAction(Intent.ACTION_GET_CONTENT);
//        myIntent.setData(uri);

        startActivityForResult(myIntent,1);

    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_CANCELED) {
            // action cancelled
        }
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            InputImage image;
            try {
                image = InputImage.fromFilePath(getApplicationContext(), uri);

                TextRecognizer recognizer = TextRecognition.getClient();
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        String resultText = visionText.getText();
                                        String speech = "";
                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            String blockText = block.getText();
                                            speech += blockText;
                                        }

                                        tb.setText(speech);

                                        if(speech.isEmpty()){
                                            t1.speak("Il y a pas de text!", TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                        else {
                                            t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                                        }





                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });



            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }


    }




}