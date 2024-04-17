package com.example.quizapp_aitmbark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Quiz1 extends AppCompatActivity {
    RadioGroup rg;
    RadioButton rb, rb1, rb2;
    Button bNext;
    TextView textView,quiz1;
    int score, num_qst;
    String RepCorrect;
    ImageView imageView;
    private static final String TAG = "quiz1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);
        rg=(RadioGroup) findViewById(R.id.radioGroup1);
        bNext=(Button) findViewById(R.id.next1);
        rb1 = findViewById(R.id.oui1);
        rb2 = findViewById(R.id.non1);
        textView = findViewById(R.id.qst1);
        quiz1=findViewById(R.id.quiz1);
        imageView = findViewById(R.id.image);
        score = getIntent().getIntExtra("score", 0);
        num_qst = getIntent().getIntExtra("num_qst", 1);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("q" + num_qst + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Merci de choisir une réponse S.V.P !", Toast.LENGTH_SHORT).show();
                } else {
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    //Toast.makeText(getApplicationContext(),rb.getText().toString(),Toast.LENGTH_SHORT).show();
                    if (rb.getText().toString().equals(RepCorrect)) {
                        score += 1;
                        //Toast.makeText(getApplicationContext(),score+"",Toast.LENGTH_SHORT).show();
                    }
                    if (num_qst < 5){
                        num_qst++;
                        Intent intent = new Intent(Quiz1.this, Quiz1.class);
                        intent.putExtra("score", score);
                        intent.putExtra("num_qst", num_qst);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Quiz1.this, com.example.quizapp_aitmbark.Score.class);
                        intent.putExtra("score", score);
                        startActivity(intent);
                    }


                    finish();

                }
            }
        });

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // Retrieve the correct answer from Firestore
        db.collection("QuizApp_Aitmbark")
                .document("Question")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            RepCorrect = document.getString("Quiz" + num_qst + ".answer");
                            List<String> choices = (List<String>) document.get("Quiz" + num_qst + ".choices");
                            String question = document.getString("Quiz" + num_qst + ".question");

                            textView.setText(question);

                            rb1.setText(choices.get(0));
                            rb2.setText(choices.get(1));

                            Log.d(TAG, "Correct answer retrieved from Firestore: " + RepCorrect);
                        } else {
                            Log.e(TAG, "Document does not exist");
                        }
                    } else {
                        Log.e(TAG, "Error getting document", task.getException());
                    }
                });

    }  }