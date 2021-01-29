package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InputNoteActivity extends AppCompatActivity {

    String note;
    EditText noteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_note);

        noteEditText = (EditText) findViewById(R.id.input_input_note);

        note = (String) getIntent().getStringExtra("note");

        if (note.length() != 0){
            noteEditText.setText(note);
        }
    }
    public void onSave(View v){
        note = (String) noteEditText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("note", note);
        setResult(1, resultIntent);
        finish();
    }
    public void onCancel(View v){
        note = (String) noteEditText.getText().toString();

        Intent resultIntent = new Intent();
        setResult(0, resultIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(0, resultIntent);
        finish();
    }
}