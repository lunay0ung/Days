package com.example.luna.days;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{


    //다이어리 추가할 때
    private static final int REQUEST_EDIT= 1;

    //다이어리 수정할 때
    private static final int REQUEST_MODIFY =2;

    //메모 추가 할 때
    private static final int REQUEST_ADD_MEMO= 3;

    //STT메모 추가할 때
    private static final int REQUEST_MEMO_STT= 4;

    //녹음 및 재생 메모 추가할 때
    private static final int REQUEST_MEMO_RECORD= 5;

    //텍스트(STT포함) 메모 수정할 때
    public static final int REQUEST_MODIFY_TXTMEMO=6;

  /*  //사진메모
    public static final int REQUEST_MEMO_PHOTO= 6;*/

    //>>>>>다이어리 관련
    Button ibtn_add_diary;
    ListView listview1;
    Item_diary item_diary;

    ArrayList<Item_diary> item_dlist;
    Adapter adapter;
    Uri mintoBitmapUri;

    //포지션
    int index;

    //메인화면의 뷰페이저

    //viewpager돌아가게 한 것 (링크 아래)
    //https://stackoverflow.com/questions/17610085/how-to-switch-automatically-between-viewpager-pages


    Handler handler;

    //페이지 번호
    int p = 0;
    //화면이 전환되는 방향
    int v = 1;
    private int delay = 1000;
    private int page = 0;
    private ViewpagerAdapter viewpagerAdapter;
    private ViewPager viewPager;

    private int previousState, currentState;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(viewpagerAdapter.getCount() == page)
            {
                page=0;
            }
            else
            {
                page++;
            }

            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }//run
    };//Runnable


    //메모 관련 필요한 아이들
    GridView gridview1, gridView2;
    TextView tv_open_memo;
    ImageButton ibtn_record, ibtn_paint, ibtn_stt;
    //ibtn_record: 녹음/재생
    //ibtn_stt: STT

    //MemoAdpater memoAdapter/* memoAdapter2*/;

    //리사이클러뷰 아이들
    ArrayList<Item_memo> item_memoList;
    Item_memo item_memo;
    RecyclerView recyclerView;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    MemoRcvAdapter memoRcvAdapter;
    MemoRcvAdapter.TextTypeViewHolder textTypeViewHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));


        //탭호스트 작동시키기
        //http://recipes4dev.tistory.com/115
        //setup 시키지 않으면 작동 안 함
        TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setup();

        //첫번째 탭. 페이지 뷰: content1
        TabHost.TabSpec ts1 = tabhost.newTabSpec("Tap Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("다이어리");
        tabhost.addTab(ts1);

        //두번째 탭
        TabHost.TabSpec ts2 = tabhost.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("메모");
        tabhost.addTab(ts2);

        //>>>>다이어리 탭
        //다이어리 추가가버튼
        ibtn_add_diary = (Button) findViewById(R.id.ibtn_add_dairy);
        ibtn_add_diary.setOnClickListener(this);

        //다이어리 액티비티 뷰
        final EditText ed_diarydate = (EditText) findViewById(R.id.ed_diarydate);
        final EditText ed_diaryplace = (EditText) findViewById(R.id.ed_diaryplace);
        final EditText ed_diaryevent = (EditText) findViewById(R.id.ed_diaryevent);
        final EditText ed_diarynote = (EditText) findViewById(R.id.ed_diarynote);

        //다이어리 '탭'에 띄워줄 것들
        listview1 = (ListView)findViewById(R.id.listview1);
        item_dlist= new ArrayList<Item_diary>();
        adapter = new Adapter(this, R.layout.listview_diary, item_dlist);
        //listview1.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        //멀티플 초이스 모드로 하면 삭제버튼이 작동을 안 함

        listview1.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listview1.setAdapter(adapter);

        //저장했던 것 세팅해랏
        restoreState();

        //메인화면 뷰페이저(광고화면)
        handler = new Handler();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewpagerAdapter = new ViewpagerAdapter(getApplicationContext());
        viewPager.setAdapter(viewpagerAdapter);
        // viewPager.setCurrentItem(adapter.myImages.length*1000); //myImages의 리턴값이 Integer.MAX_VALUE이므로 오류발생

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }//onPageScrolled


            @Override
            public void onPageSelected(int position) {
                page = position;
                Log.e("onPageSelected", "pageSelected:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });// ViewPager.OnPageChangeListener


        //리사이클러뷰 세팅
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        item_memoList = new ArrayList<Item_memo>();
        memoRcvAdapter = new MemoRcvAdapter(item_memoList, getApplicationContext(), recyclerView);
        recyclerView.setAdapter(memoRcvAdapter);


        //Add swipe to dismiss support
        //슬라이드 -> 삭제
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder ViewHolder, RecyclerView.ViewHolder target) {
                return false;
            }//onMove

            @Override
            public void onSwiped(RecyclerView.ViewHolder ViewHolder, int swipeDir) {

                item_memoList.remove(ViewHolder.getAdapterPosition());
                memoRcvAdapter.notifyItemRemoved(ViewHolder.getAdapterPosition());

            }//onSwiped
        };//simpleItemTouchCallback

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


     /*   gridview1 = (GridView) findViewById(R.id.gridview1);
       // gridView2 = (GridView) findViewById(R.id.gridview1);
        item_memoList = new ArrayList<Item_memo>();
        memoAdapter = new MemoAdpater(this, R.layout.gridview_memo, item_memoList);
       // memoAdapter2 = new MemoAdpater(this, R.layout.gridview_stt, item_memoList);


        //gridview1.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gridview1.setAdapter(memoAdapter);
        //gridview1.setAdapter(memoAdapter2);
        gridview1.setChoiceMode(gridview1.CHOICE_MODE_MULTIPLE);
*/


        //메모창 열기
        tv_open_memo = (TextView) findViewById(R.id.tv_open_memo);
        tv_open_memo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)

            {
                Intent memointent = new Intent(getApplicationContext(), MemoActivity.class);
                startActivityForResult(memointent, REQUEST_ADD_MEMO);
            }
        });//setOnClickListener

        //'메모를 입력하세요' 텍스트뷰에 밑줄 긋기 효과
        SpannableString content = new SpannableString("메모를 입력하세요.");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0); tv_open_memo.setText(content);

        //STT창 열기
        ibtn_stt = (ImageButton) findViewById(R.id.ibtn_stt);
        ibtn_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sttIntent = new Intent(getApplicationContext(), STTActivity.class);
                startActivityForResult(sttIntent,REQUEST_MEMO_STT);
            }//onClick
        });//setOnClickListener


        //녹음창 열기
        ibtn_record = (ImageButton) findViewById(R.id.ibtn_record);
        ibtn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent recordIntent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivityForResult(recordIntent, REQUEST_MEMO_RECORD);

            }//onClick
        });//ibtn_record.setOnClickListener


        //다이어리 리스트 숏클릭 시 미리보기 다이얼로그를 띄워주자
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long ld)
            {
                index = position;
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_dialog, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);

                //레이아웃 크기 조정
                 final Dialog dialog = builder.create();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.copyFrom(dialog.getWindow().getAttributes());
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = 650;


                dialog.show();
                Window window = dialog.getWindow();

                //바탕화면 색깔 설정
                //안 할 경우 넓힌 부분이 검정색으로 나옴
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                window.setAttributes(params);


/*                final AlertDialog dialog = builder.create();
                dialog.show();*/

                ImageButton cd_iv_close = (ImageButton) view.findViewById((R.id.cd_iv_close));
                ImageView cd_iv = (ImageView) view.findViewById(R.id.cd_iv);
                TextView cd_diarydate = (TextView) view.findViewById(R.id.cd_diarydate);
                TextView cd_diaryplace = (TextView)view.findViewById(R.id.cd_diaryplace);
                TextView cd_diaryevent = (TextView)view.findViewById(R.id.cd_diaryevent);
                TextView cd_diarynote = (TextView)view.findViewById(R.id.cd_diarynote);

                TextView diarydate = (TextView) view.findViewById(R.id.diarydate);
                TextView diaryplace = (TextView)view.findViewById(R.id.diaryplace);
                TextView diaryevent = (TextView)view.findViewById(R.id.diaryevent);
                TextView diarynote = (TextView)view.findViewById(R.id.diarynote);


                cd_iv.setImageBitmap(adapter.dlist.get(index).getDiaryImage());
                cd_iv.setImageURI(adapter.dlist.get(index).getUserphotoUri());
                cd_diarydate.setText(adapter.dlist.get(index).getDate());
                cd_diaryplace.setText(adapter.dlist.get(index).getPlace());
                cd_diaryevent.setText(adapter.dlist.get(index).getEvent());
                cd_diarynote.setText(adapter.dlist.get(index).getNote());

                cd_diarydate.setTextColor(Color.BLACK);
                cd_diaryplace.setTextColor(Color.BLACK);
                cd_diaryevent.setTextColor(Color.BLACK);
                cd_diarynote.setTextColor(Color.BLACK);

                diarydate.setTextColor(Color.GRAY);
                diaryplace.setTextColor(Color.GRAY);
                diaryevent.setTextColor(Color.GRAY);
                diarynote.setTextColor(Color.GRAY);


                // x표시된 이미지뷰가 안 뜬다. 왜지? 그리고 노트의 텍스트부분이 이상함 ㅡㅡㅡ
                // 이미지 버튼으로 바꾸니 뜨는데....
                cd_iv_close.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(view.getId()==R.id.cd_iv_close)
                        dialog.dismiss();
                    }//onClick
                });//setOnClickListener

            }
        });



        //TODO 리스트 슬라이드 하면 삭제하고 싶다
        //롱클릭 시 수정/삭제할 수 있는 다이얼로그
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            //첫번째: 엄마view(listview1)
            //두번째: 이벤트가 발생한 항목 뷰(리스트뷰에 들어가있는 하나하나의 row)
            //세번째: 이벤트가 발생한 인덱스(선택한 자식의 인덱스 리턴)
            //네번째" 이벤트가 발생한 항목 뷰의 아이디(안드로이드가 알아서 매김)
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long Id)
            {

                index = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String[] choices = {"수정", "삭제"};
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }//setNegativeButton onClick
                        }) //setNegativeButton done
                        .setItems(choices, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which)
                            {
                                switch (which)
                                {
                                    case 0: //수정 == m

                                        //
                                        //Toast.makeText(getApplicationContext(),"수정 액티비티 띄우기", Toast.LENGTH_SHORT).show();
                                        //여기서 원래 있던 자료를 보내면 modify activity에서 수정 후 다시 그 값을 받아온다(REQUEST_MODIFY)
                                        //일단 자료만 보냄
                                        Intent mintent = new Intent(getApplicationContext(), ModifyActivity.class);
                                        //TODO : 다른 방법도 있으니 참고
                                        mintent.putExtra("mdate", adapter.dlist.get(position).getDate());
                                        mintent.putExtra("mplace", adapter.dlist.get(position).getPlace());
                                        mintent.putExtra("mevent", adapter.dlist.get(position).getEvent());
                                        mintent.putExtra("mnote", adapter.dlist.get(position).getNote());
                                        //mintent.putExtra("mImg", adapter.dlist.get(position).getDiaryImage());
                                        // ==> 비트맵 자체를 넘김

                                        mintent.putExtra("mImagUri", adapter.dlist.get(position).getUserphotoUri().toString());
                                        //이미지 Uri를 mImgUri로 넘김

                                        /*>>>>설명
                                        <인텐트로 비트맵 넘기기>
                                        넘겨주는 Activity
                                        intent intent = new intent(this, Main.class);
                                        intent.putExtra("image", BITMAP);
                                        //'키'의 역할을 하는 image와 그에 해당하는 값인 value가 짝을 이루어 저장됨.
                                        startActivity(intent);

                                        받는 Activity
                                        Intent intent = getIntent();
                                        Bitmap bitmap = intent.getParcelableExtra("image")
                                         */

                                        startActivityForResult(mintent, REQUEST_MODIFY);
                                        break;
                                    case 1: //삭제

                                        //삭제 확인 여부 다이얼로그
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                                        //제목 세팅
                                        alertDialogBuilder.setTitle("<일기 삭제여부 확인>");

                                        //알림창 내용 세팅
                                        alertDialogBuilder
                                                .setMessage(item_dlist.get(index).getDate()+"에 쓰신 일기를 정말로 삭제하시겠어요?")
                                                .setCancelable(false) //뒤로 버튼 클릭 시 취소가능설정 여부
                                                .setPositiveButton("삭제 할래요", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //다이어리를 삭제한다
                                                        item_dlist.remove(position);
                                                        listview1.clearChoices();
                                                        adapter.notifyDataSetChanged();
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

/*                                        item_dlist.remove(position);
                                        listview1.clearChoices();
                                        adapter.notifyDataSetChanged();*/
                                } //switch 끝
                                dialogInterface.dismiss();
                            } //setItems, onClick
                        }); //setItems
                        builder.show();
                return true;
            }
        }); //setonLONGclick end 일단 보류...삭제 메소드를 만들고 그걸 넣어주자
    } //onCreate end


    @Override
    public void onClick(View v)
    {
        ///// 이게 이렇게 4개면 안 될 것 같음..
        //날짜, 한줄만
        if (v.getId() ==R.id.ibtn_add_dairy)
        {
            Intent addintent = new Intent(getApplicationContext(), DiaryActivity.class);
            startActivityForResult(addintent, REQUEST_EDIT);
        }
    }//onclick 끝

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

                if (resultCode == RESULT_OK)
                {// Toast.makeText(getApplicationContext(), "resultCode == RESULT_OK", Toast.LENGTH_SHORT).show();

                    switch (requestCode)
                    {
                        case REQUEST_EDIT:
                        {//Toast.makeText(getApplicationContext(), "requestCode == REQUEST_EDIT", Toast.LENGTH_SHORT).show();
                            TextView maindate = (TextView) listview1.findViewById(R.id.maindate);
                            TextView mainevent = (TextView) listview1.findViewById(R.id.mainevent);
                            TextView mainplace =(TextView) listview1.findViewById(R.id.mainplace);
                            ImageView mainphoto = (ImageView) listview1.findViewById(R.id.mainphoto);

                            String date = data.getStringExtra("date");
                            String event = data.getStringExtra("event");
                            String place = data.getStringExtra("place");
                            String note = data.getStringExtra("note");
                            final String imageUri = data.getStringExtra("imageUri");

                            Bitmap userphoto = null;
                            //사진 업로드 됐을 때
                            Uri intoBitmapUri = Uri.parse(imageUri);


                            try {
                                userphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), intoBitmapUri);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            item_diary = new Item_diary(date, place, event, note, userphoto, intoBitmapUri);

                            item_dlist.add(item_diary);
                            adapter.notifyDataSetChanged();

                                  /*Uri uri_test = data.getData();
                                    InputStream image_stream =
                                            getContentResolver().openInputStream(uri_test);
                                    Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
                                    */
                                 /*
                                   참고: https://stackoverflow.com/questions/25828808/issue-converting-uri-to-bitmap-2014
                                    Uri IMAGE_URI = imageReturnedIntent.getData();
                                    InputStream image_stream = getContentResolver().openInputStream(IMAGE_URI);
                                    Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
                                    my_img_view.setImageBitmap(bitmap)*/

                                 break;
                        }//REQUEST_EDIT 조건문 닫기

                        case REQUEST_MODIFY:
                        {

                            String go_mdate = data.getStringExtra("go_mdate");
                            String go_mevent = data.getStringExtra("go_mevent");
                            String go_mplace = data.getStringExtra("go_mplace");
                            String go_mnote = data.getStringExtra("go_mnote");

                            adapter.dlist.get(index).setDate(go_mdate);
                            adapter.dlist.get(index).setPlace(go_mplace);
                            adapter.dlist.get(index).setEvent(go_mevent);
                            adapter.dlist.get(index).setNote(go_mnote);

                            String go_mimageUri = data.getStringExtra("go_mimageUri");
                            Bitmap muserphoto = null;
                            //사진 업로드 됐을 때
                            Uri mintoBitmapUri = Uri.parse(go_mimageUri);

                            Log.d("mintoBitmapUri","mintoBitmapUri"+mintoBitmapUri);

                            try {
                                muserphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mintoBitmapUri);
                                Log.d("사진수정","사진수정"+muserphoto);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            //item_diary = new Item_diary(mdate, mevent, mnote, mplace,muserphoto);
                            adapter.dlist.get(index).setDiaryImage(muserphoto);
                            //바뀐 Uri를 저장해줌
                            adapter.dlist.get(index).setUserphotoUri(mintoBitmapUri);
                            adapter.notifyDataSetChanged();

                            break;
                        }//REQUEST_MODIFY 조건문 닫기
                        case REQUEST_ADD_MEMO: //메모 추가
                            String memotitle = data.getStringExtra("memotitle");
                            Log.e("메모제목", "메모제목"+data.getStringExtra("memotitle"));

                            String memonote = data.getStringExtra("memonote");
                            Log.e("메모내용", "메모내용"+data.getStringExtra("memonote"));

                            if(memotitle.length() > 0 || memonote.length()>0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                            {
                                item_memo = new Item_memo(memotitle, memonote);
                                item_memoList.add(item_memo);
                                Log.e("메모",""+item_memoList.size());
                                memoRcvAdapter.notifyDataSetChanged();
                            }

                            break;

                        case REQUEST_MEMO_STT: //STT 메모 추가
                            String sttTitle = data.getStringExtra("sttTitle");
                            String sttNote = data.getStringExtra("sttNote");

                            if(sttNote.length() > 0 || sttTitle.length()>0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                            {
                                item_memo = new Item_memo(sttTitle, sttNote);
                                memoRcvAdapter.addItem(sttTitle, sttNote);
                                memoRcvAdapter.notifyDataSetChanged();
                            }
                            break;

                        case REQUEST_MEMO_RECORD: //오디오 메모 추가
                            String recordmemoTitle = data.getStringExtra("recordmemoTitle");
                            String recordmemoNote = data.getStringExtra("recordmemoNote");
                            String voicememo = data.getStringExtra("voicememo");
                            File getFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "progress_recorder");
                            Log.e("음성메모파일받기", "음성메모파일받기"+Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "progress_recorder");


                            if(recordmemoTitle.length() > 0 || recordmemoNote.length()>0 /*|| getFile.exists()==true*/) //메모 제목/내용 중 하나라도 있으면 메모 생성
                            {
                                Log.e("음성메모", "음성메모 데이터");
                                item_memo = new Item_memo(voicememo, recordmemoTitle, recordmemoNote);
                                memoRcvAdapter.addItem(voicememo, recordmemoTitle, recordmemoNote);
                                memoRcvAdapter.notifyDataSetChanged();
                            }
                            break;
/*

                        case REQUEST_MODIFY_TXTMEMO: //텍스트 스타일 메모 수정

                            String mTitle = data.getStringExtra("m_Title");
                            String mNote = data.getStringExtra("m_Note");

                            if(mTitle.length() > 0 || mNote.length()>0 */
/*|| getFile.exists()==true*//*
) //메모 제목/내용 중 하나라도 있으면 메모 생성
                            {

                                memoRcvAdapter.item_memoList.get(index).setMemo1(mTitle);
                                memoRcvAdapter.item_memoList.get(index).setMemo2(mNote);
                                //memoRcvAdapter.editItem(mTitle, mNote);
                                memoRcvAdapter.notifyItemChanged(index);

                             */
/*   adapter.dlist.get(index).setDiaryImage(muserphoto);
                                //바뀐 Uri를 저장해줌
                                adapter.dlist.get(index).setUserphotoUri(mintoBitmapUri);
                                adapter.notifyDataSetChanged();*//*

                            }
                            break;
*/

                    }//switch
                }//resultCode ok

    }// onActivityResult 함수 닫기


    /////////////////////저장 * 재개
    @Override
    protected void onStop()
    {
        super.onStop();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

/*    @Override
    protected void onResume()
    {
        super.onResume();
        //restoreState();
        //여기에 놓으면 resume될 때마다 불러와서 리스트가 배로 생김
    }*/

    public void saveState()
    {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        /////////////////////json
        JSONArray jsonArray = new JSONArray();

        //리스트의 크기만큼 jsonObject를 만든다
        for(int i=0; i<adapter.dlist.size();i++)
        {
            JSONObject jsonObject = new JSONObject();
            //for문이 한번 돌 때마다 key값이 변함
            String key_img = "Img"+i;
            String key_date="date"+i;
            String key_place="place"+i;
            String key_event="event"+i;
            String key_note ="note"+i;

            try {
                jsonObject.put(key_img,adapter.dlist.get(i).getUserphotoUri().toString());
                jsonObject.put(key_date, adapter.dlist.get(i).getDate());
                jsonObject.put(key_place, adapter.dlist.get(i).getPlace());
                jsonObject.put(key_event, adapter.dlist.get(i).getEvent());
                jsonObject.put(key_note, adapter.dlist.get(i).getNote());

                //jsonArray를 이쪽에 선언해야 for문이 한번씩 돌 때마다(리스트가 생길 때마다)
                //Array의 크기가 커진다--새로운 jsonObject를 넣어줌
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString("diaryDetail", jsonArray.toString());
        Log.d("json 세이브브브브 하나",jsonArray.toString());
        editor.commit();
    }

    public void restoreState()
    {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        try {
           JSONArray jsonArray = new JSONArray(pref.getString("diaryDetail",""));

            if(jsonArray.length() != 0)
            {
                editor.clear();
                editor.commit();
            }

            for (int i=0; i < jsonArray.length(); i++)
            {
                    //jsonArray에서 jsonObject 꺼내기
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String key_img = "Img"+i;
                    String key_date="date"+i;
                    String key_place="place"+i;
                    String key_event="event"+i;
                    String key_note ="note"+i;

                    String image = jsonObject.getString(key_img);
                    String date = jsonObject.getString(key_date);
                    String place = jsonObject.getString(key_place);
                    String event = jsonObject.getString(key_event);
                    String note = jsonObject.getString(key_note);

                    Uri uri = Uri.parse(image);
                    Bitmap userphoto = null;

                try {
                    Item_diary item_diary;
                    userphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    adapter.addItem(new Item_diary(date, place, event, note, userphoto, uri ));
                    //adapter.addItems(date, place, event, note, userphoto, uri);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } //for문

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("restoreState","불러오기 하나"+pref.getString("diaryDetail",""));
        adapter.notifyDataSetChanged();

    }

    //////////////////저장 * 재개 끝

    /*
        // 값(Key Data) 삭제하기
    private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("hi");
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    출처: http://arabiannight.tistory.com/entry/안드로이드Android-SharedPreferences-사용-예제 [아라비안나이트]
     */



} // Main Activity 클래스 닫기
