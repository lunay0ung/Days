package com.example.luna.days;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ModifyTxtMemoActivity extends AppCompatActivity {
    private Button memoDoneBtn;
    private EditText ed_memo_title, ed_memo_note;
    ArrayList<Item_memo> item_memoList;
    MemoRcvAdapter memoRcvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_txt_memo);

        //변수 선언
        memoDoneBtn = (Button) findViewById(R.id.memoDoneBtn);

        ed_memo_title = (EditText) findViewById(R.id.ed_memo_title);
        ed_memo_note = (EditText) findViewById(R.id.ed_memo_note);

        //원래 있던 자료 가져오기
        final Intent getDataIntent = getIntent();
        //final Item_memo item_memo = (Item_memo) getDataIntent.getSerializableExtra("memo");

        String title = getDataIntent.getExtras().getString("title");
        String note = getDataIntent.getExtras().getString("note");

        //자료 세팅
        ed_memo_title.setText(title);
        ed_memo_note.setText(note);
       // ed_memo_title.setText(item_memo.getMemo1());
        //ed_memo_note.setText(item_memo.getMemo2());


        //수정해서 다시 메인으로 보내주기
        memoDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent modifiedIntent = new Intent(getApplicationContext(), MainActivity.class);
                modifiedIntent.putExtra("m_Title", ed_memo_title.getText().toString() );
                modifiedIntent.putExtra("m_Note", ed_memo_note.getText().toString() );
                setResult(RESULT_OK, modifiedIntent);
                finish();
            }//onClick
        });//setOnClickListener

    }
}
