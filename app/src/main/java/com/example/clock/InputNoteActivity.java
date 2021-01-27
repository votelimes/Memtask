package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class InputNoteActivity extends AppCompatActivity {

    String note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_note);


    }
    @Override
    protected void onStop(){
        super.onStop();

        finish();
    }
    public void onSave(View v){

    }
    public void onCancel(View v){

    }
}