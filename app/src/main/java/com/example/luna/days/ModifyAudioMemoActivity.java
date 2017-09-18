package com.example.luna.days;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ModifyAudioMemoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    //뷰 객체
    Button memoDoneBtn, Button, deleteRecFileBtn, playRecFileBtn;
    EditText ed_memo_title, ed_memo_note;
    TextView mTvPlayMaxPoint;
    SeekBar mPlayProgressBar;

    //어댑터
    MemoRcvAdapter memoRcvAdapter;

    //인덱스
    int arrayIndex;

    //미디어 플레이어 관련
    private static final int REC_STOP = 0;
    private static final int RECORDING = 1;
    private static final int PLAY_STOP = 0;
    private static final int PLAYING = 1;
    private static final int PLAY_PAUSE = 2;


    private int mRecState = REC_STOP;
    private int mPlayerState = PLAY_STOP;

    private MediaPlayer mPlayer = null;
    private int mCurRecTimeMs = 0;
    private int mCurProgressTimeDisplay = 0;

    private static String RECORDED_FILE;
    File file;

    // 재생시 SeekBar 처리
    Handler mProgressHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            //Message; extends Object implements Parcelable
            if (mPlayer == null)
                return;

            try {
                if (mPlayer.isPlaying()) {
                    mPlayProgressBar.setProgress(mPlayer.getCurrentPosition());
                    mProgressHandler2.sendEmptyMessageDelayed(0, 100);

                } else {
                    mPlayer.release();
                    mPlayer = null;
                    updateUI();
                }
            } catch (IllegalStateException e) {
            } catch (Exception e) {
            }
        }//handleMessage
    };//handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_audio_memo);

        //변수 선언
        memoDoneBtn = (Button) findViewById(R.id.memoDoneBtn);
        deleteRecFileBtn = (Button) findViewById(R.id.deleteRecFileBtn);
        playRecFileBtn = (Button) findViewById(R.id.playRecFileBtn);

        mTvPlayMaxPoint = (TextView) findViewById(R.id.tvPlayMaxPoint);
        mPlayProgressBar = (SeekBar) findViewById(R.id.mPlayProgressBar);

        ed_memo_title = (EditText) findViewById(R.id.ed_memo_title);
        ed_memo_note = (EditText) findViewById(R.id.ed_memo_note);

        //원래 있던 자료 가져오기
        Intent getDataIntent = getIntent();

        String title = getDataIntent.getExtras().getString("title");
        String note = getDataIntent.getExtras().getString("note");
        String audioUri = getDataIntent.getExtras().getString("audioUri");
        arrayIndex = getDataIntent.getExtras().getInt("arrayIndex", 0);

        //자료 세팅
        ed_memo_title.setText(title);
        ed_memo_note.setText(note);
        RECORDED_FILE = audioUri;


        //기존 파일 재생
        playRecFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnStartPlayOnClick();
            }
        });


        //파일 삭제
        deleteRecFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //삭제 확인 여부 다이얼로그
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ModifyAudioMemoActivity.this);

                //제목 세팅
                alertDialogBuilder.setTitle("<녹음 파일 삭제여부 확인>");

                //알림창 내용 세팅
                alertDialogBuilder
                        .setMessage("파일을 정말 삭제하시겠어요?")
                        .setCancelable(false) //뒤로 버튼 클릭 시 취소가능설정 여부
                        .setPositiveButton("삭제 할래요", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //녹음 파일을 삭제한다
                                if( file.exists())
                                    Log.e("파일삭제", "파일삭제"+file.exists());
                                {
                                    file.delete();
                                    mTvPlayMaxPoint.setText("00:00");
                                }

                            }

                        })
                        .setNegativeButton("삭제 안 해요", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //알림창을 벗어난다
                                dialog.cancel();
                            }
                        });
                //다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                //다이얼로그 보여주기
                alertDialog.show();

            }// onclick
        });//setOnClickListener



        //수정해서 다시 메인으로 보내주기
        Intent modifiedIntent = new Intent(getApplicationContext(), MainActivity.class);

        modifiedIntent.putExtra("arrayIndex", arrayIndex);
        modifiedIntent.putExtra("mtitle", ed_memo_title.getText());
        modifiedIntent.putExtra("mnote", ed_memo_note.getText());
        modifiedIntent.putExtra("maudioUri", RECORDED_FILE);

    }//onCreate

    //재생시작 버튼 클릭
    private void mBtnStartPlayOnClick()
    {
        if (mPlayerState == PLAY_STOP)
        {
            mPlayerState = PLAYING;
            startPlay();
            updateUI();
        } else if (mPlayerState == PLAYING)
        {
            mPlayerState = PLAY_STOP;
            stopPlay();
            updateUI();
        }
    }//mBtnStartPlayOnClick


    //재생시작
    private void startPlay()
    {
        //미디어 플레이어 생성
        if(mPlayer == null)
        {
            mPlayer = new MediaPlayer();
        }
        else
        {
            mPlayer.reset();
        }

        mPlayer.setOnCompletionListener(ModifyAudioMemoActivity.this);
        //Register a callback to be invoked when the end of a media source has been reached during playback.



        try {
            mPlayer.setDataSource(RECORDED_FILE);

            mPlayer.prepare();
            /*{Idle, Prepared, Started, Paused, PlaybackCompleted, Error}*/
            int point = mPlayer.getDuration();
            mPlayProgressBar.setMax(point);

            int maxMinPoint = point / 1000 / 60;
            int maxSecPoint = (point / 1000) % 60;
            String maxMinPointStr = "";
            String maxSecPointStr = "";

            if (maxMinPoint < 10)
                maxMinPointStr = "0" + maxMinPoint + ":";
            else
                maxMinPointStr = maxMinPoint + ":";

            if (maxSecPoint < 10)
                maxSecPointStr = "0" + maxSecPoint;
            else
                maxSecPointStr = String.valueOf(maxSecPoint);

            mTvPlayMaxPoint.setText(maxMinPointStr + maxSecPointStr);

        } catch (Exception e) {
            Log.v("ProgressRecorder", "미디어 플레이어 Prepare Error ==========> " + e);
        }

        if (mPlayerState == PLAYING) {
            mPlayProgressBar.setProgress(0);

            try {
                // SeekBar의 상태를 0.1초마다 체크
                mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                mPlayer.start();
            } catch (Exception e) {
                Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }//startPlay


    //재생중지
    private void stopPlay()
    {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mPlayProgressBar.setProgress(0);

        // 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }//stopPlay


    public void onCompletion(MediaPlayer mp)
    {
        mPlayerState = PLAY_STOP; // 재생이 종료됨

        // 재생이 종료되면 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);

    }//onCompletion


    //UI업데이트
    private void updateUI()
    {
        if (mPlayerState == PLAY_STOP) {
            mPlayProgressBar.setProgress(0);
        } else if (mPlayerState == PLAYING);

    }//updateUI



}
