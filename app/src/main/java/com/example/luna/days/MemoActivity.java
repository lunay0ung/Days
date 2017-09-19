package com.example.luna.days;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MemoActivity extends AppCompatActivity {


    private Button memoDoneBtn;
    private EditText ed_memo_title, ed_memo_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        ed_memo_note = (EditText) findViewById(R.id.ed_memo_note);
        ed_memo_title = (EditText) findViewById(R.id.ed_memo_title);
        memoDoneBtn = (Button) findViewById(R.id.memoDoneBtn);

        memoDoneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra("memotitle", ed_memo_title.getText().toString());
                Log.d("memotitle", "memotitle"+ed_memo_title.getText().toString());
                intent.putExtra("memonote", ed_memo_note.getText().toString());
                Log.d("memonote", "memonote"+ed_memo_note.getText().toString());
                setResult(RESULT_OK, intent);
                finish();

            }//onClick
        });//setOnClickListener

    }//onCreate

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }
}
