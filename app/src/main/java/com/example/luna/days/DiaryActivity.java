package com.example.luna.days;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity implements View.OnClickListener{

    //다이어리 뷰
    Button btn_diary_done;
    EditText ed_diaryevent, ed_diarydate, ed_diaryplace, ed_diarynote;


    //사진
    //ImageButton ibtn_diaryphoto_add;
    Button btn_diaryphoto_add,  btn_diaryphoto_delete;
    ImageView iv_diarybasic;
    String mCurrentPhotoPath;
    public Uri photoUri, albumUri = null;
    Boolean album = false;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_IMAGE_CROP =3;

    //데이트
    private int  mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;

    // //  https://m.blog.naver.com/PostView.nhn?blogId=kittoboy&logNo=110133522888&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        //EditText 아이디
        ed_diaryevent = (EditText) findViewById(R.id.ed_diaryevent);
        ed_diaryplace = (EditText) findViewById(R.id.ed_diaryplace);
        ed_diaryevent = (EditText) findViewById(R.id.ed_diaryevent);
        ed_diarynote = (EditText) findViewById(R.id.ed_diarynote);

        //이미지뷰 아이디
        iv_diarybasic = (ImageView) findViewById(R.id.iv_diarybasic);

        //사진 추가
        btn_diaryphoto_add = (Button) findViewById(R.id.btn_diaryphoto_add);
        btn_diaryphoto_add.setOnClickListener(this);

        //사진 삭제
        btn_diaryphoto_delete = (Button) findViewById(R.id.btn_diaryphoto_delete);
        btn_diaryphoto_delete.setOnClickListener(this);

        //데이트 피커 관련
        ed_diarydate = (EditText) findViewById(R.id.ed_diarydate);
        ed_diarydate.setOnClickListener(this);

        //현재 날짜 가져오기
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay= c.get(Calendar.DAY_OF_MONTH);

        //ed_diarydate에 현재 날짜 띄우기
        updateDisplay();


        //데이터 전송 버튼
        btn_diary_done = (Button) findViewById(R.id.btn_diary_done);
        btn_diary_done.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {

        //사진추가 다이얼로그 부분
        // http://jeongchul.tistory.com/287 참고
        // https://m.blog.naver.com/PostView.nhn?blogId=gyeom__&logNo=220815406153&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
        if(v.getId()==R.id.btn_diaryphoto_add)
        {
            DialogInterface.OnClickListener cameraListner = new DialogInterface.OnClickListener()
            {//
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {
                    doTakePhotoAction();
                }
            };//

            DialogInterface.OnClickListener albumListner = new DialogInterface.OnClickListener()
            {///
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {
                    doTakeAlbumAction();
                }
            };///

            DialogInterface.OnClickListener cancelListner = new DialogInterface.OnClickListener()
            {//
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {
                    dialogInterface.dismiss();
                }
            };//

            new AlertDialog.Builder(this)

                    .setTitle("이미지 업로드 방법 선택")
                    .setNegativeButton("앨범선택", albumListner)
                    .setPositiveButton("사진촬영", cameraListner)
                    .setNeutralButton("취소", cancelListner)
                    .show();
        } //if(btn_diaryphoto_add) 끝

        //사진 삭제
        if (v.getId() ==R.id.btn_diaryphoto_delete)
        {
            doRemovePhotoAction();
        }

            //데이트피커 팝업
        if(v.getId()==R.id.ed_diarydate)
        {
            showDialog(DATE_DIALOG_ID);
        }

        //데이터 전송하자
        if(v.getId()==R.id.btn_diary_done)
        {
            //EditText의 null값을 처리하기 위해 변수선언
            // 참고: http://blog.naver.com/PostView.nhn?blogId=ycomputer&logNo=120152209274
            String get_ed_diarydate = ed_diarydate.getText().toString();
            String get_ed_diaryevent = ed_diaryevent.getText().toString();
            String get_ed_diaryplace = ed_diaryplace.getText().toString();

            //날짜 혹은 한줄 요약값(필수사항)이 입력되지 않았을 때
            if (get_ed_diarydate.getBytes().length <= 0 || get_ed_diaryevent.getBytes().length <= 0)
            {//필수 입력값(날짜, 한줄 요약) 입력 안 됐을 때 알림창 뜨도록 + 조건문 시작
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiaryActivity.this);

                //제목 세팅
                alertDialogBuilder.setTitle("<날짜>와 <한줄 요약>은 필수 입력값이에요!");

                //알림창 내용 세팅
                alertDialogBuilder
                        .setMessage("다이어리를 마저 쓰시겠어요?")
                        .setCancelable(false) //뒤로 버튼 클릭 시 취소가능설정 여부
                        .setPositiveButton("마저 쓸래요", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //다이어리를 계속 쓴다(다이얼로그 취소)
                                        dialog.cancel();
                                    }

                                })
                        .setNegativeButton("다음에 쓸래요", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //다이어리 창을 벗어난다
                                        DiaryActivity.this.finish();
                                    }
                                });
                //다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                //다이얼로그 보여주기
                alertDialog.show();
            } //필수입력값이 null일 때 세팅 될 다이얼로그를 띄울 조건문 끝

            /*
            보낼 수 있는 것: 이미지, 날짜, 한줄 요약, 장소
            보낼 수 있는 경우의 수: 날짜, 한줄 요약/ 날짜, 한줄 요약 + 이미지/ 날짜, 한줄 요약+ 장소/날짜, 한줄 요약+이미지+장소

            보내야 하는 것: 날짜, 한줄 요약
            --> 이미지를 보내지 않으면 기본 이미지를 띄운다(랜덤으로 돌려가면서?)
            --> 혹은 얼굴 표시 모양 선택하게 해서...마치 페북 좋아요 슬퍼요 등등처럼. 그러나 이건 좀 어려워보이므로 뒤로 미룬다
            --> 장소를 보내지 않으면 아예 장소 칸이 없도록 ==> Item_diary에서 재정의 해줘야
             */
            else
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                //죄다 뿌리자!
                ed_diaryevent = (EditText) findViewById(R.id.ed_diaryevent);
                ed_diaryplace = (EditText)findViewById(R.id.ed_diaryplace);
                ed_diarydate = (EditText)findViewById(R.id.ed_diarydate);
                ed_diarynote=(EditText)findViewById(R.id.ed_diarynote);

                iv_diarybasic = (ImageView) findViewById(R.id.iv_diarybasic);
                intent.putExtra("date", ed_diarydate.getText().toString());
                intent.putExtra("event", ed_diaryevent.getText().toString());
                intent.putExtra("place", ed_diaryplace.getText().toString());
                intent.putExtra("note", ed_diarynote.getText().toString());

                if(albumUri == null && photoUri == null)
                {
                    //drawable URI 구하기
                    Uri defaultImg = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+getResources().getResourcePackageName(R.drawable.default_img)
                            +'/'+getResources().getResourceTypeName(R.drawable.default_img)+'/'+String.valueOf(R.drawable.default_img));
                    photoUri = defaultImg;
                    intent.putExtra("imageUri", photoUri.toString());
                }
                else if (albumUri == null)
                {
                    intent.putExtra("imageUri", photoUri.toString());
                }
                else
                {
                    intent.putExtra("imageUri", albumUri.toString());
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        }//btn_done, 데이터 전송버튼 end
    }//onClick

    //사진 삭제
    public void doRemovePhotoAction ()
    {
        iv_diarybasic.setImageBitmap(null);

    }

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
        cropIntent.putExtra("outputX", 350); //크롭한 이미지의 x축 크기
        cropIntent.putExtra("outputY", 350);
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


    private void updateDisplay()
    {
        ed_diarydate.setText(String.format("%d년 %d월 %d일", mYear, mMonth+1, mDay));
    }

    //데이트피커, 날짜설정(원본 보호)
/*    private DatePickerDialog.OnDateSetListener mDateSetListener
            = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            mYear = year;
            mMonth = monthOfYear;
            mDay= dayOfMonth;
            updateDisplay();
        }
    };*/

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

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DATE_DIALOG_ID)
        {
            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);

        }
        return null;
    } //onCreateDialog

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

                    iv_diarybasic.setImageBitmap(image_bitmap);

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
                        iv_diarybasic.setImageBitmap(photo);
                        mediaScanIntent.setData(photoUri); //동기화
                    }
                    else if (album == true)
                    {
                        album = false;
                        Bitmap photo2 = BitmapFactory.decodeFile(albumUri.getPath());
                        iv_diarybasic.setImageBitmap(photo2);
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


/*


    /////////////////////저장 * 재개 EditText라 필요 없음 ^^^^^^^^^^^^^
    @Override
    protected void onPause() {
        super.onPause();

        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreState();
    }

    public void restoreState() {

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if ((pref != null) && pref.contains("date_added") && pref.contains("event_added") || pref.contains("place_added") || pref.contains("note_added"))
        { //pref if문
            String date_added = pref.getString("date_added", "");
            String place_added = pref.getString("place_added", "");
            String event_added = pref.getString("event_added", "");
            String note_added  = pref.getString("note_added", "");
            ed_diarydate.setText(date_added);
            ed_diaryevent.setText(event_added);
            ed_diaryplace.setText(place_added);
            ed_diarynote.setText(note_added);
        }//pref if문 끝
    }

    public void saveState()
    {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("date_added", ed_diarydate.getText().toString());
        editor.putString("event_added", ed_diaryevent.getText().toString());
        editor.putString("place_added", ed_diaryplace.getText().toString());
        editor.putString("note_added", ed_diarynote.getText().toString());

        String date_added = pref.getString("date_added", "");
        String event_added = pref.getString("event_added", "");
        String place_added = pref.getString("place_added", "");
        String note_added = pref.getString("note_added", "");

        editor.commit();

    }
    //////////////////저장 * 재개 끝
*/


}
