package com.example.luna.days;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class STTActivity extends AppCompatActivity {

    private Button sttDoneBtn, sttBtn;
    private EditText ed_memo_title, ed_memo_note;

    private final int REQUEST_VOICE_SAVE = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);

        ed_memo_note = (EditText) findViewById(R.id.ed_memo_note);
        ed_memo_title = (EditText) findViewById(R.id.ed_memo_title);

        sttBtn = (Button) findViewById(R.id.sttBtn);
        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);

            }//sttBtn.onClick
        });//sttBtn.setOnClickListener

        sttDoneBtn = (Button) findViewById(R.id.sttDoneBtn);
        sttDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);

                sendIntent.putExtra("sttTitle", ed_memo_title.getText().toString());
                Log.d("sttTitle", "sttTitle"+ed_memo_title.getText().toString());
                sendIntent.putExtra("sttNote", ed_memo_note.getText().toString());
                Log.d("note", "note"+ed_memo_note.getText().toString());
                setResult(RESULT_OK, sendIntent);
                finish();
            }//sttDoneBtn.onClick
        });//sttDoneBtn.setOnClickListener


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);


    }//onCreate

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle results) {


            // TODO: read audio file from inputstream*/

            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResults = results.getStringArrayList(key);
            String [] said = new String[mResults.size()];
            mResults.toArray(said);
           /* ed_memo_note.setText(""+said[0]);*/

            for(int i=0; i<mResults.size(); i++)
            {
                ed_memo_note.setText(""+said[i]);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };//RecognitionListener

}//STTActivity
