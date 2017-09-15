package com.example.luna.days;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener{


    private static final int REC_STOP = 0;
    private static final int RECORDING = 1;
    private static final int PLAY_STOP = 0;
    private static final int PLAYING = 1;
    private static final int PLAY_PAUSE = 2;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private int mRecState = REC_STOP;
    private int mPlayerState = PLAY_STOP;
    private SeekBar /*mRecProgressBar,*/ mPlayProgressBar;
    /*private Button mBtnStartRec, mBtnStartPlay;*/
    private String mFilePath, mFileName = "test.amr";
    public String  fullFilePath;
    private TextView mTvPlayMaxPoint, mtvRecMaxPoint;

    private Button memoDoneBtn, playRecFileBtn, deleteRecFileBtn, /*recordBtn,*/ stopRecBtn;
    private ToggleButton  recordBtn;
    private EditText ed_memo_title, ed_memo_note;
    private Layout recFileFormatLayout;

    private int mCurRecTimeMs = 0;
    private int mCurProgressTimeDisplay = 0;


    // 재생시 SeekBar 처리
    Handler mProgressHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            //Message; extends Object implements Parcelable
            /*Defines a message containing a description and arbitrary data object that can be sent to a Handler.
            This object contains two extra int fields and an extra object field that allow you to not do allocations in many cases.
            UI등에서 실제로 처리를 담당하는  데이터를 전송하거나 작업을 요청하기 위해서 전달하는 객체이다.
            전달된 메시지는 메시지 Queue를 통해서 핸들러가  사용한다.
            메시지를 전달하기 위해선 핸들러의 obtainMessage()호출해서 메시지 풀의 메시지를  전달해야한다.
            메시지를 전달하기 위해서는 sendMessage() 등을 사용한다.
            메시지 전달 방식의 종류
            sendMessage() - 큐의  메시지를 삽입한다.
            sendMessageAtFrontQueue() - 큐의 맨앞에 메시지를 삽입한다.(우선처리)
             - 장비기동시간을 기준으로 삽입한다.(SystemClock.uptimeMillis()참고)
            sendMessageDelayed() - 특정시간만큼 지연 삽입한다.
            작업스레드가 메인스레드와 완전히 분리되어 있어서 메인스레드에서 생성한 핸들러를 작업스레드에서
            직접 참조 할수 없을때, Message 생성자 대신 obtain() 메소드 메세지를 생성하여 보내줄수도 있다. */


            if (mPlayer == null)
                return;

            try {
                if (mPlayer.isPlaying()) {
                    mPlayProgressBar.setProgress(mPlayer.getCurrentPosition());
                    mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                    /*final boolean	sendEmptyMessageDelayed(int what, long delayMillis)
                    Sends a Message containing only the what value, to be delivered after the specified amount of time elapses.

                    예시
                        Handler mHandler = new Handler(){
                        public void handleMessage(Message msg){
                        times++;
                        mText.setText("time = " + times);
                        mHandler.sendEmptyMessageDelayed(0, 1000);       // 타이머에따라 위의 과정을 반복함  cf) 1000=1초
                            };
                         };

                     타이머 처리를 위해 Handler클래스를 사용한다.
                     핸들러는 스레드간의 메세지 통신을 위한 장치로 sendMessage메서드나 유사 메서드로 특정 핸드러에게 메세지를 보낼수있다.
                     sendEmptyMessageDelayed는 1000(1초)시간의 딜레이를 가지고 지속적으로 핸들러는 호출하게됨으로써 1초에 숫자가 1씩 증가되게 만들어 타이머의 효과를 만들어주었다.

                    */
                } else {
                    mPlayer.release();
                    mPlayer = null;
                    /*A MediaPlayer can consume valuable system resources.
                    Therefore, you should always take extra precautions to
                    make sure you are not hanging on to a MediaPlayer instance longer than necessary.
                    When you are done with it, you should always call release() to
                    make sure any system resources allocated to it are properly released.
                    For example, if you are using a MediaPlayer and your activity receives a call to onStop(),
                    you must release the MediaPlayer, because it makes little sense to hold on to it
                    while your activity is not interacting with the user (unless you are playing media in the background,
                    which is discussed in the next section). When your activity is resumed or restarted,
                    of course, you need to create a new MediaPlayer and prepare it again before resuming playback.

                    Here's how you should release and then nullify your MediaPlayer:

                    mediaPlayer.release();
                    mediaPlayer = null;*/
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
        setContentView(R.layout.activity_record);

        // SD카드에 디렉토리를 만든다.
        mFilePath = SunUtil.makeDir("progress_recorder");

        //recordBtn = (Button) findViewById(R.id.recordBtn);
        recordBtn = (ToggleButton) findViewById(R.id.recordBtn);
        playRecFileBtn = (Button) findViewById(R.id.playRecFileBtn);
        memoDoneBtn = (Button) findViewById(R.id.memoDoneBtn);
        deleteRecFileBtn = (Button) findViewById(R.id.deleteRecFileBtn);
        //mRecProgressBar = (SeekBar) findViewById(R.id.recProgressBar);
        mPlayProgressBar = (SeekBar) findViewById(R.id.mPlayProgressBar);
        //mtvRecMaxPoint =(TextView) findViewById(R.id.tvRecMaxPoint);
        mTvPlayMaxPoint = (TextView) findViewById(R.id.tvPlayMaxPoint);


        ed_memo_note = (EditText) findViewById(R.id.ed_memo_note);
        ed_memo_title = (EditText) findViewById(R.id.ed_memo_title);

        deleteRecFileBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        playRecFileBtn.setOnClickListener(this);
        memoDoneBtn.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.recordBtn: //녹음 시작
                mBtnStartRecOnClick();
                break;
            case R.id.playRecFileBtn: //재생 시작
                mBtnStartPlayOnClick();
                break;
            case R.id.memoDoneBtn:
                Intent recordIntent = new Intent(getApplicationContext(), MainActivity.class);

                recordIntent.putExtra("recordmemoTitle", ed_memo_title.getText().toString());
                Log.d("recordmemoTitle", "recordmemoTitle"+ed_memo_title.getText().toString());
                recordIntent.putExtra("recordmemoNote", ed_memo_note.getText().toString());
                Log.d("recordmemoNote", "recordmemoNote"+ed_memo_note.getText().toString());

                //녹음 파일 주소 보내기
                //생각해보니까 보낼 필요가 없음. 어차피 액티비티 다시 들어와야 재생 가능.
                //==>> 아니 보내야 함.
                // 첫째, 텍스트 메모는 없고 파일만 올라와져 있어도 메인에서 볼 수 있어야 하므로
                // 둘째, 녹음만 해서 메모할 수도 있으므로
                recordIntent.putExtra("voicememo", mFilePath);
                //TODO 데이터 제대로 안 넘어가는 거 같은데....? 일단 로그 상 주고받는 건 같아보임..
                Log.d("음성메모파일보내기", "음성메모파일보내기"+mFilePath);

                setResult(RESULT_OK, recordIntent);
                finish();
            case R.id.deleteRecFileBtn:
                //TODO 삭제 기능되게 하기
                SunUtil.removeDir("progress_recorder");
            default:
                break;
        }//switch
    }//onClick


    //녹음시작 버튼 클릭
    private void  mBtnStartRecOnClick()
    {
        if(mRecState==REC_STOP)
        {
            mRecState = RECORDING;
            startRec();
            updateUI();
        }//if(mRecState==REC_STOP)
        else if(mRecState ==RECORDING)
        {
            mRecState = REC_STOP;
            stopRec();
            updateUI();
        }// else if(mRecState ==RECORDING)
    }//mBtnStartRecOnClick


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

    //녹음 시작
    private void startRec()
    {
        mCurRecTimeMs = 0;
        mCurProgressTimeDisplay = 0;
/*

        //Seekbar의 상태를 0.1초 후 체크 시작
        mProgressHandler.sendEmptyMessageDelayed(0, 100);
*/

        //리코더가 없으면 새로 만든 후 리셋, 아니면면 그냥 리셋
        //리셋: 미디어 리코더를 사용하지 않는 상태로 다시 시작한다
        //cf. start: setOutputFile()에 명시된 파일의 데이터 캡쳐와 인코딩을 시작한다
        if(mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
        }
        else
        {
            mRecorder.reset();
        }

        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //원래 mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);이었음
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mFilePath + mFileName);
            mRecorder.prepare();
            mRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }//startRec


    //녹음 정지
    private void stopRec()
    {
        try {
            mRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            mRecorder.release();
            mRecorder = null;
        }

        mCurRecTimeMs = -999;

        //seekbar의 상태를 즉시 체크
        //mProgressHandler.sendEmptyMessageDelayed(0,0);
    }//stopRec


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

        mPlayer.setOnCompletionListener(RecordActivity.this);
        //Register a callback to be invoked when the end of a media source has been reached during playback.

        fullFilePath = mFilePath + mFileName;
        Log.v("ProgressRecorder", "녹음파일명 ==========> " + fullFilePath);

        try {
            mPlayer.setDataSource(fullFilePath);
            /*Calling setDataSource(FileDescriptor), or setDataSource(String), or setDataSource(Context, Uri),
            or setDataSource(FileDescriptor, long, long), or setDataSource(MediaDataSource)
            transfers a MediaPlayer object in the Idle state to the Initialized state.*/
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
// 재생을 중지하고
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
        if (mRecState == REC_STOP)
        {

            //mBtnStartRec.setText("Rec");
            //mRecProgressBar.setProgress(0);
        } else if (mRecState == RECORDING)
        {
            // mBtnStartRec.setText("Stop");
        }


        if (mPlayerState == PLAY_STOP) {
          //  mBtnStartPlay.setText("Play");
            mPlayProgressBar.setProgress(0);
        } else if (mPlayerState == PLAYING);
            //mBtnStartPlay.setText("Stop");
    }//updateUI
}//RecordActivity
