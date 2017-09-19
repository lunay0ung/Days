package com.example.luna.days;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    ProgressBar progressLoading;
    ImageView imageView;
    private int progressStatus = 0;
    Splash splash;
    TextView tv_splash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        imageView = (ImageView) findViewById(R.id.imageView);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        tv_splash = (TextView) findViewById(R.id.tv_splash);


        // progressLoading.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        //프로그레스 바 색상 바꾸기
        progressLoading.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        startLoading();



        //애니메이션 액션 로딩
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_alpha);
        //뷰의 애니메이션 시작
        tv_splash.startAnimation(anim);

        splash = new Splash();
        splash.execute(0);

    }//onCreate


    private void startLoading()
    {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

            }//run
        },2100);//Runnable



    }//startLoading


    class Splash extends AsyncTask<Integer, Integer, Integer>
    {

        /*
        android.os.AsyncTask<Params, Progress, Result>
        AsyncTask enables proper and easy use of the UI thread.
        This class allows you to perform background operations and publish results on the UI thread
        without having to manipulate threads and/or handlers.

        The three types used by an asynchronous task are the following:

        1. Params, the type of the parameters sent to the task upon execution.
        2. Progress, the type of the progress units published during the background computation.
        3. Result, the type of the result of the background computation.

        Not all types are always used by an asynchronous task. To mark a type as unused, simply use the type Void:
*/

        private boolean isCancelled = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressLoading.setProgress(progressStatus);
            //int progressStatus = 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressLoading.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressLoading.setProgress(progressStatus);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressLoading.setProgress(0);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {


            while (isCancelled()==false /*&& progressStatus <10*/)
            {
                progressStatus++;

                publishProgress(progressStatus);

                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return progressStatus;

        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

}
