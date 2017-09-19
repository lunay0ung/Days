package com.example.luna.days;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ModifyActivity extends AppCompatActivity implements View.OnClickListener{

    DiaryActivity diaryActivity;
    Item_diary item_diary;
    ArrayList<Item_diary> item_dlist;

    String mCurrentPhotoPath;
    private Uri photoUri, albumUri = null;
   // Uri mImageUri = photoUri;
    //String mImg;
    //TODO: mImageUri랑 mphotoUri 일치시키기

    Bitmap original_userphoto;

    Boolean album = false;

    //다이얼로그
    private ProgressDialog dialog;
    private Dialog d;

    //데이트
    private int  mYear;
    private int mMonth;
    private int mDay;
    private final int DATE_DIALOG_ID = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_IMAGE_CROP =3;

    EditText m_ed_diarydate;
    EditText m_ed_diaryplace;
    EditText m_ed_diaryevent;
    EditText m_ed_diarynote;

    ImageView m_iv_diarybasic;

    Button mbtn_diaryphoto_add;
   // Button mbtn_diaryphoto_delete;
    Button mbtn_diary_done;

    String mImagUri;

    Uri changeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        //다이어리 수정하는 modify activity에 있는 뷰
         m_iv_diarybasic = (ImageView) findViewById(R.id.m_iv_diarybasic);
         m_ed_diarydate = (EditText) findViewById(R.id.m_ed_diarydate);
         m_ed_diaryplace = (EditText) findViewById(R.id.m_ed_diaryplace);
         m_ed_diaryevent = (EditText) findViewById(R.id.m_ed_diaryevent);
         m_ed_diarynote = (EditText) findViewById(R.id.m_ed_diarynote);

        //데이트 피커 뜨도록
        m_ed_diarydate = (EditText) findViewById(R.id.m_ed_diarydate);
        m_ed_diarydate.setOnClickListener(this);

        //현재 날짜 가져오기
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay= c.get(Calendar.DAY_OF_MONTH);

        //ed_diarydate에 현재 날짜 띄우기
       // updateDisplay();


        //사진 재업로드
        mbtn_diaryphoto_add = (Button) findViewById(R.id.mbtn_diaryphoto_add);
        mbtn_diaryphoto_add.setOnClickListener(this);

        //사진 삭제
    /*    mbtn_diaryphoto_delete=(Button) findViewById(R.id.mbtn_diaryphoto_delete);
        mbtn_diaryphoto_delete.setOnClickListener(this);*/

        //입력 완료
        mbtn_diary_done=(Button) findViewById(R.id.mbtn_diary_done);
        mbtn_diary_done.setOnClickListener(this);

        // 메인 액티비티-수정-누르면 다이어리 액티비티에 있던 값을 가져온다
        Intent mintent = getIntent();

        //텍스트뷰 채워줌
        String mdate= mintent.getExtras().getString("mdate");
        String mplace= mintent.getExtras().getString("mplace");
        String mevent= mintent.getExtras().getString("mevent");
        String mnote = mintent.getExtras().getString("mnote");
        mImagUri = mintent.getExtras().getString("mImagUri");


        /////
        m_ed_diarydate.setText(mdate);
        m_ed_diaryplace.setText(mplace);
        m_ed_diaryevent.setText(mevent);
        m_ed_diarynote.setText(mnote);
        Log.d("mnote","mnote"+mnote);

        //이미지 Uri받기

        Uri mImagUri_this = Uri.parse(mImagUri);
        try {
            original_userphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mImagUri_this);
            Log.d("사진수정","사진수정"+original_userphoto);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        m_iv_diarybasic.setImageBitmap(original_userphoto);

        //이미지뷰 채워줌
        //방법 1.
        //참고 : http://answerofgod.tistory.com/entry/android의-uri값-전달하기
        //intent.getExtra로 URI 값을 가져오면 nullpointexcet~발생
        //uri전달방법 2개
        //intent.putExtra로 보내고 intet.getParcelableExtra로 가져오기
        //예시>>
        //send -> putExtra("uri",urivalue);
        //receive -> Uri urivalue=getParcelableExtra("uri");
        //다이어리->메인->수정액티비티: 그대로 사진 받아오는 애, 잘 됨.
/*         original_userphoto  = mintent.getParcelableExtra("mImg");

        //받은 비트맵 그대로 이미지 뷰에 넣어줌
        m_iv_diarybasic.setImageBitmap(original_userphoto);*/

        findViewById(R.id.m_ed_diarydate).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO 날짜 창 띄웠을 때 오늘 날짜 말고 이전에 입력했던 날짜 뜨도록 수정해야 함
                new DatePickerDialog(ModifyActivity.this, dateSetListener, mYear, mMonth, mDay).show();
               // showDialog(DATE_DIALOG_ID);
            }//onClick
        }); //setPmC;ocl


        //다이어리 사진 재업로드/재수정
        findViewById(R.id.mbtn_diaryphoto_add).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DialogInterface.OnClickListener cameraListner = new DialogInterface.OnClickListener() {//
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakePhotoAction();
                    }
                };//

                DialogInterface.OnClickListener albumListner = new DialogInterface.OnClickListener() {///
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakeAlbumAction();
                    }
                };///

                DialogInterface.OnClickListener cancelListner = new DialogInterface.OnClickListener() {//
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };//

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ModifyActivity.this);
                alertDialog.setTitle("이미지 업로드 방법 선택")
                        .setNegativeButton("앨범선택", albumListner)
                        .setPositiveButton("사진촬영", cameraListner)
                        .setNeutralButton("취소", cancelListner)
                        .create()
                        .show();
            }
        });

        //다이어리 사진 재삭제
/*        findViewById(mbtn_diaryphoto_delete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doRemovePhotoAction();

            }
        });*/


    } //onCreate

    //데이트피커
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfmonth)
        {
           /* String msg = String.format("%d년 %d월 %d일", year, monthOfyear+1, dayOfmonth);
            m_ed_diarydate.setText(msg);*/
            mYear = year;
            mMonth = monthOfyear;
            mDay = dayOfmonth;
            updateDisplay();
        }
    };

    private void updateDisplay()
    {
        String msg = String.format("%d년 %d월 %d일",  mYear, mMonth+1, mDay);
        m_ed_diarydate.setText(msg);

    }

    //TODO m_ed_diarydate에 있는 todo를 하려다가 만들어놓음. 효과는 없지만 일단 안 지우고 주석처리.
 /*   @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DATE_DIALOG_ID)
        {
            return new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDay);
        }
        return null;
    } //onCreateDialog
*/

    @Override
    public void onClick(View v) {

        //다이어리 수정 완료
        if (v.getId() == R.id.mbtn_diary_done)
        {//EditText의 null값을 처리하기 위해 변수선언
            // 참고: http://blog.naver.com/PostView.nhn?blogId=ycomputer&logNo=120152209274

//            m_ed_diarydate = (EditText) findViewById(R.id.m_ed_diarydate);
//            m_ed_diaryevent = (EditText) findViewById(R.id.m_ed_diaryevent);
//            m_ed_diaryplace = (EditText) findViewById(R.id.m_ed_diaryplace);
//            m_ed_diarynote = (EditText) findViewById(R.id.m_ed_diarynote);
//            m_iv_diarybasic = (ImageView) findViewById(R.id.iv_diarybasic);

            String get_m_ed_diarydate = m_ed_diarydate.getText().toString();
            String get_m_ed_diaryevent = m_ed_diaryevent.getText().toString();
            String get_m_ed_diaryplace = m_ed_diaryplace.getText().toString();
            String get_m_ed_diarynote  = m_ed_diarynote.getText().toString();

            //날짜 혹은 한줄 요약값(필수사항)이 입력되지 않았을 때
            if (get_m_ed_diarydate.getBytes().length <= 0 || get_m_ed_diaryevent.getBytes().length <= 0)
            {//필수 입력값(날짜, 한줄 요약) 입력 안 됐을 때 알림창 뜨도록 + 조건문 시작
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ModifyActivity.this);

                //제목 세팅
                alertDialogBuilder.setTitle("<날짜>와 <한줄 요약>은 필수 입력값이에요!");

                //알림창 내용 세팅
                alertDialogBuilder
                        .setMessage("다이어리를 마저 쓰시겠어요?")
                        .setCancelable(false) //뒤로 버튼 클릭 시 취소가능설정 여부
                        .setPositiveButton("마저 쓸래요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //다이어리를 계속 쓴다(다이얼로그 취소)
                                dialog.cancel();
                            }

                        })
                        .setNegativeButton("다음에 쓸래요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //다이어리 창을 벗어gg난다
                               //ModifyActivity.this.finish();
                            }
                        });
                //다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                //다이얼로그 보여주기
                alertDialog.show();
            } //필수입력값이 null일 때 세팅 될 다이얼로그를 띄울 조건문 끝

            else
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Log.d("필수입력 다 됐을 때 수정", "");
                //Toast.makeText(getApplicationContext(), "죄다 뿌리자", Toast.LENGTH_SHORT).show();

                //죄다 뿌리자!


                intent.putExtra("go_mdate", m_ed_diarydate.getText().toString());
                intent.putExtra("go_mevent", m_ed_diaryevent.getText().toString());
                intent.putExtra("go_mplace", m_ed_diaryplace.getText().toString());
                intent.putExtra("go_mnote", m_ed_diarynote.getText().toString());

                    if (albumUri == null && photoUri == null)  //사진 수정 안하고 저장했을 때
                    {
                        // 메인 액티비티-수정-누르면 다이어리 액티비티에 있던 값을 가져온다
/*                        Intent mintent = getIntent();
                        String mImagUri = mintent.getStringExtra("mImagUri");*/
                        //Uri mImagUri_this = Uri.parse(mImagUri);
                        //mphotoUri = mImagUri_this;
                        intent.putExtra("go_mimageUri", mImagUri);

                  //     Toast.makeText(getApplicationContext(), "앨범/포토 Uri 가 null" , Toast.LENGTH_SHORT).show();
                    }
                    else if (albumUri == null)  //카메라 찍었을때 - 다시 수정하면 사진 안넘어옴
                    {
                       // changeUri = photoUri;
                       // intent.putExtra("go_mimageUri", changeUri.toString());
                        intent.putExtra("go_mimageUri", photoUri.toString());
                       // Toast.makeText(getApplicationContext(), "albumUri가 null" , Toast.LENGTH_SHORT).show();
                    }
                    else //갤러리로 사진 수정했을 때 - 다시 수정하면 사진 안넘어옴
                    {
                       // changeUri = albumUri;
                       // intent.putExtra("go_mimageUri", changeUri.toString());
                        intent.putExtra("go_mimageUri", albumUri.toString());
                        //Toast.makeText(getApplicationContext(), "photoUri가 null" , Toast.LENGTH_SHORT).show();
                    }

                    setResult(RESULT_OK, intent);

                finish();
            } //else end
        } //if-btn done 버튼 끝
    } //onClick 끝


    //사진 삭제
    public void doRemovePhotoAction ()
    {
        m_iv_diarybasic.setImageBitmap(null);
    }

    //데이트, 날짜설정 관련
/*
    private void updateDisplay()
    {
        String msg = String.format("%d년 %d월 %d일", mYear, mMonth+1, mDay);
        m_ed_diarydate.setText(msg);
      // m_ed_diarydate.setText(String.format("%d년 %d월 %d일", mYear, mMonth+1, mDay));
    }
*/

/*

   */
/* //데이트피커, 날짜설정
    private DatePickerDialog.OnDateSetListener mDateSetListener
            = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            mYear = year;
            mMonth = monthOfYear;
            mDay= dayOfMonth;
            updateDisplay();
        }
    };
*//*

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DATE_DIALOG_ID)
        {
            return new DatePickerDialog(ModifyActivity.this, mDateSetListener, mYear, mMonth, mDay);
        }

        return null;
    } //onCreateDialog
*/


    //카메라에서 사진촬영
    public void doTakePhotoAction () //카메라 촬영 후 이미지 가져오기
    { //
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) !=null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile(); //사진찍은 후 저장할 임시파일
            } catch (IOException ex)
            {
                Toast.makeText(getApplicationContext(),"creatImageFile Failed", Toast.LENGTH_LONG).show();
            }
            if (photoFile !=null)
            {
                photoUri = Uri.fromFile(photoFile); //임시파일의 위치, 경로 가져옴
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //임시파일위치에 저장
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    } //메소드 end

    //저장할 폴더 생성
    private File createImageFile() throws IOException
    {
        //특정경로와 폴더 지정 않고 메모리 최상위치에 저장
        String imageFileName = "tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }


    //앨범에서 이미지 가져오기
    public void doTakeAlbumAction ()
    {
        //앨범호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        //  intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //images on the SD card(http://zgather.blogspot.kr/2011/05/blog-post_06.html)
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    //이미지 크랍
    private void cropImage()
    {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("outputX", 550); //크롭한 이미지의 x축 크기
        cropIntent.putExtra("outputY", 400);
        cropIntent.putExtra("aspectX", 1);  //가로 세로 비율
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true); //꽉찬 비율로 저장

        if (album == false)
        {
            cropIntent.putExtra("output", photoUri);
            //사진을 찍고 저장한 파일을 크랍한 경우
            //크랍이미지를 해당경로에 저장
        }
        else if (album==true)
        {
            cropIntent.putExtra("output", albumUri);
            //앨범에서 가져온 파일을 크랍한 뒤 저장할 때
        }
        //android.content.ActivityNotFoundException: No Activity found to handle Intent
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP); //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    { //
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
        {

            Toast.makeText(getApplicationContext(),"RESULT_NOT_OK", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        { //
            switch (requestCode)
            { ///
                case REQUEST_TAKE_PHOTO: //앨범 이미지 가져오기
                    album = true;
                    File albumFile = null;
                    try
                    {
                        albumFile = createImageFile();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (albumFile != null)
                    {
                        albumUri = Uri.fromFile(albumFile);
                        //앨범 이미지 크랍한 결과는 새로운 위치에 저장
                    }

                    photoUri = data.getData(); //앨범 이미지의 경로

                    Bitmap image_bitmap = null;
                    try
                    {
                        image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    m_iv_diarybasic.setImageBitmap(image_bitmap);

                    // break; <== REQUEST_IMAGE_CAPTURE로 전달하여 crop

                case REQUEST_IMAGE_CAPTURE:
                    cropImage();
                    break;

                case REQUEST_IMAGE_CROP:
                   /* Bitmap photo = BitmapFactory.decodeFile(photoUri.getPath());
                    Bitmap photo2 = BitmapFactory.decodeFile(albumUri.getPath());
                    user_image.setImageBitmap(photo); //사진 찍어서 가져오는 것
                    user_image.setImageBitmap(photo2); //갤러리에서 가져오는 것*/
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //동기화

                    if (album == false)
                    {
                        Bitmap photo = BitmapFactory.decodeFile(photoUri.getPath());
                        m_iv_diarybasic.setImageBitmap(photo);
                        mediaScanIntent.setData(photoUri); //동기화
                    }
                    else if (album == true)
                    {
                        album = false;
                        Bitmap photo2 = BitmapFactory.decodeFile(albumUri.getPath());
                        m_iv_diarybasic.setImageBitmap(photo2);
                        mediaScanIntent.setData(albumUri); //동기화
                    }
                    this.sendBroadcast(mediaScanIntent); //동기화
                    break;
            } ///switch end
        } // else end
    } //onActivityResult end

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }
}
