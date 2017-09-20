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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //다이어리 추가할 때
    private static final int REQUEST_EDIT = 1;

    //다이어리 수정할 때
    private static final int REQUEST_MODIFY = 2;

    //메모 추가 할 때
    private static final int REQUEST_ADD_MEMO = 3;

    //STT메모 추가할 때
    private static final int REQUEST_MEMO_STT = 4;

    //녹음 및 재생 메모 추가할 때
    private static final int REQUEST_MEMO_RECORD = 5;

    //텍스트(STT포함) 메모 수정할 때
    public static final int REQUEST_MODIFY_TXTMEMO = 6;

    //오디오 파일 있는 메모 수정할 떄
    public static final int REQUEST_MODIFY_AUIDOMEMO = 7;

  /*  //사진메모
    public static final int REQUEST_MEMO_PHOTO= 6;*/

    //엘프
    Button elfBtn;

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
    private int delay = 3000;
    private int page = 0;
    private ViewpagerAdapter viewpagerAdapter;
    private ViewPager viewPager;

    private int previousState, currentState;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewpagerAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }

            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }//run
    };//Runnable


    //메모 관련 필요한 아이들
    GridView gridview1, gridView2;
    /*TextView tv_open_memo;
    ImageButton ibtn_record, ibtn_paint, ibtn_stt;*/
    Button memoBtn, recordBtn, sttBtn;
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
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

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

        //엘프
        elfBtn = (Button) findViewById(R.id.elfBtn);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fortuneteller_set);
        elfBtn.startAnimation(anim);


        //배열에서 랜덤하게 뽑아준다
        Random random = new Random();
        Random mediumRandom = new Random();
        Random bigRandom = new Random();
        final int randomValue = random.nextInt(3); //bound=3(배열 0~3)
        final int mediumRandomValue = mediumRandom.nextInt(4); //goodMorning용
        final int bigRandomValue = bigRandom.nextInt(7); //goodEvening용

        //시간을 인식하자!
        Calendar cal = Calendar.getInstance();
        //현재 시간만 구하기(0-24)
        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);


        //아침시간 세팅(8-11)
        final String[] goodMorning = {"좋은 아침! 오늘은 새로운 일을 계획하기 좋은 날인 것 같군요. 계획으로 끝나지 않고 바로 실행에 옮겨도 문제없을 것 같아요. 단, 투자는 신중히 검토해야 해요!",
                "한번에 욕심내지 말고 점진적으로 일을 진행해야 성과가 있을 것 같아요. 너무 급하게 하려하면 오히려 스텝이 엉킬 위험이 있어요. 마음을 편히 하고 약간은 느리게 진행하세요.",
                "새로운 일에 과감히 도전할 수 있는 패기와 개척자의 의지가 요구되는 하루가 될 거예요. 여느 때보다 더욱 기운을 내어 삶을 향해 돌진하세요.",
                "걱정, 근심, 고통거리라고 생각했던 일들이 이제는 당신의 처지와 형편을 향상시킬 거예요. 오늘은 예감이 좋아요. 어떤 일이든 망설이지 마시고 도전하세요.",
                "시간은 누구에게나 공평하죠. 그래서 더욱 소중해요. 다만 모두 그것을 소중한 선물로 생각하고 다루는가는 전혀 별개의 문제죠. 오늘은 소중한 선물같은 하루가 될 것 같군요."};

        //오후시간 세팅(12-5)
        final String[] goodAfternoon = {"하루를 열심히 사는 것도 좋지만 건강은 모든 일을 가능케 하는 근본이에요. 아무리 주의해도 지나침이 없죠. " +
                "틈틈이 스트레칭도 하고! 남은 오후 또한 건강히 보내길 바랍니다.",
                "벌써 오후네요. 이것 저것 해야 할 일도 많고 생각이 복잡하다면, 일의 우선순위를 노트에 적어보세요. 눈 앞이 환해지고 효율이 높아질 거예요.",
                "오늘 오후도 반짝반짝 빛나고 계신가요? 삶을 아름답게 하는 보석들은 바쁘게 달려갈 때는 보이지 않는대요. " +
                        "오히려 차분히 쉬면서 주변을 둘러보게 되면 바쁠 때는 보지 못했던 보물들을 보게 될 수 있어요.",
                "조금 떨어져 세상을 보면 나와 다른 사람과 가축과 나무와 돌과 물이 모두 하나죠. 그래서 이 중 어느 하나를 보살피는 일도 결국은 나 자신을 보살피는 일과 같아요. 조금 아쉬운 기분이 든다면, 오늘 남은 오후엔 스스로를 보살피는 데 집중해보세요.",
                "어떤 하루를 보내고 있나요? 당신이 변화시킬 수 있는 일들과 그렇지 못한 일들이 있어요. 바꿀 수 없는 일들에 마음을 쓰는 것은 효율적이지 못하겠죠. 역량에 따른 기준을 설정해 두는 건 어떨까요?"};


        //저녁시간 세팅(6-8) +     //밤시간 세팅(9-12)
        final String[] goodEvening = {"하루가 저물어 가고 있어요. 혹시 어떤 고민때문에 힘든 하루였다면 이렇게 생각해보세요. " +
                "누구나 문제를 가지고 살아가고, 답을 구하기 위해 먼 길을 나서기도 하지만 사실 진짜 답은 자신의 안에 있는 경우가 많다는걸요.",
                "마음이 어지러울 때 그 속에 있는 한 어지러움은 없어지지 않아요. 그 안에서 빠져 나와 외부의 시선으로 바라보다 보면 어느새 어지러움은 사라져 있을 거예요.",
                "지혜는 구하려고 애쓰면 오히려 혼미해 지고 멀어져요. 오히려 잠시 내려 놓고 고요한 마음으로 바라보다 보면 안개가 걷히듯이 불현듯 찾아 오는 그런 거죠. 오늘 저녁은 차분하게 보내는 게 좋을 것 같네요.",
                "좋은 습관을 들이고 작든 크든 생각한 것을 실천에 옮기고 있노라면 변화는 자연 따라와요. 그 변화가 누적되면 그곳에 성취도 있고 성공도 존재하는 거죠. 오늘 하루는 어떤 변화가 있었나요? ",
                "탐하고 채우려 들면 행복의 크기는 줄어들고 나누고 비우려 들면 행복은 반대로 커져요. 근심은 필요한 것 이상으로 가질 때 생기고 감사는 가진 것이 족함을 알 때 생겨나죠. 감사하는 마음과 행복한 기운이 가득한 저녁시간 보내세요.",
                "현명한 사람은 모든 이유를 자신의 내부에서 찾고 어리석은 사람은 타인들 속에서 찾아요. 현명한 사람은 감사해 하고 어리석은 사람은 원망하죠. 오늘 하루, 어떠한 사람에 조금 더 가까웠는지 생각해보면 좋을 것 같군요.",
                "어느 새 하루가 저물어가고 있어요. 오늘 어떤 문제 때문에 괴로웠다면 이말이 필요할 것 같네요. 모든 문제에는 반드시 문제를 푸는 열쇠가 있어요. 사람마다 다른 방법으로 그것을 풀지만 가장 좋은 방법은 선입견을 버리고 열린 마음으로 처음부터 바라보는 거죠.",
                "사람들과 부딛치며 살다 보면 자신이 누구인지 잊어버릴 수 있어요. 이것을 제어하는 방법은 조용한 곳에서 혼자만의 시간을 가지며 생각을 정리하는 거죠. 오늘 저녁은 생각을 정리하기 좋은 때가 될 것 같네요."};

/*

        final String[] goodNight = {"상황이란 굽이쳐 흐르는 물과 같습니다 어느 곳에서는 바위도 옮길 만큼 거칠지만 다른 곳에선 평온하기가 거울 같습니다 시간이 그 두 경우를 만들어 냅니다.",
                "재물보다 사람을 얻는 것이 중요합니다 재물은 흐르는 물과 같고 사람은 단단한 대지와 같습니다 물은 이리저리 움직이지만 대지는 내가 기대고 누울 수 있는 것입니다",
                "밤2 현실적이라는 것이 이상을 버렸음을 의미하지는 않습니다 이상을 추구한다는 것은 헛된 무지개에 집착하는 것과는 다릅니다 이상은 삶을 향기롭게 만드는 마법입니다",
                "밤3  당장 불편한 것을 잠시 피하기 위하여 약속하지 마십시오 작은 것을 얻고 나중에 큰 것으로 갚아야 하니 참 나쁜 투자입니다 때로는 거절도 친절의 한 종류가 됩니다"};*/

        //새벽(1-4) 잠 못 이루는 시간대
        final String[] goodLateNight = {"지혜는 구하려고 애쓰면 오히려 혼미해 지고 멀어져요. 오히려 잠시 내려 놓고 고요한 마음으로 바라보다 보면 안개가 걷히듯이 불현듯 찾아 올 거예요. 지혜가 필요하다면, 이 말을 음미해보세요.",
                "무엇에 대해 생각하고 계시나요? 살면서 자기 자신의 모습을 보기 위하여 투자하는 시간만큼 값진 것은 없어요. 스스로가 자신의 주인이어야 함이 당연한데 사람들은 남의 삶을 사는 경우가 많거든요.",
                "아직도 깨어있으시네요. 고민이라도 있는 건가요? 눈을 감고 마음의 심연을 들여다 보았을 때 어둡다면 자신에 대한 믿음이 부족하고 세상을 의심하기 때문이래요. 자기 확신이 있다면 그 곳은 틀림없이 밝게 빛나고 있을 거라네요.",
                "늦은 밤까지 사색을 즐기시나요? 고민이 있으신가요? 이 세상에는 수백억의 사람들이 다녀 갔거나 살고 있어요. 당신이 겪는 고민은 이미 이 사람들에 의해 답이 나와 있지만 단지 그것을 찾아보려 하지 않을 뿐이에요."};


        //새벽(5-7) 일찍 일어나는 시간대
        final String[] goodEarlyMorning = {"새벽인데, 무슨 일로 깨어있으신가요? 갑자기 이런 말이 하고 싶네요. 변화를 두려워하지 말라는 거요. 변화는 성장의 전제거든요. " +
                "변화하지 않는 것은 정체가 아니라 퇴보예요. 본래의 나란 없으며 변화의 과정을 걷는 나만 있는 거죠.",
                "누구나 살면서 두려운 일을 만나게 돼요. 사람마다 그 종류가 같지는 않지만 두렵다 함은 피하고 싶음을 뜻하죠. 그러나 피할수록 그것의 크기는 더 커질 뿐이에요. 오늘은 어제보다 조금 더 용감한 하루 맞이하기를!",
                "비몽사몽하군요. 그래도 우리가 신에게서 받은 가장 소중한 시간이라는 선물을 잘 활용하려면 얼른 정신 차려야겠어요. " +
                        "모두 다 같이 시간이라는 공통적인 재료를 사용하더라도 어떻게 활용하느냐에 따라 각기 다른 내용의 작품을 만들어내니까요.",
                "미래는 운명이 결정하는 것이 아니라 작은 습관들이 쌓여 만들어지는 거예요. 건강과 재물과 명예가 모두 사람들이 작게 생각하는 습관들로부터 만들어 지는 거죠. 오늘은 미래를 결정하는 데 결정적인 하루가 될 것 같군요. 예감이 좋아요."};


        //엘프, 수정구슬, 메시지
        elfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //엘프버튼에 부여한 애니메이션 사라지게 하기(clear하지 않을 경우 엘프버튼을 사라지게 할 수 없음)
                elfBtn.clearAnimation();

                //엘프버튼이 visibile하면 gone하게 햇
                elfBtn.setVisibility(elfBtn.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                //커스텀 다이얼로그 뷰에 씌우기
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_dialog_fortuneteller, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);

                //다이얼로그 생성
                final Dialog elf_dialog = builder.create();

                //다이얼로그 애니메이션 세팅
                elf_dialog.getWindow().getAttributes().windowAnimations = R.style.FortuneTellerDialogAnimation;

                //다이얼로그 보여주기
                elf_dialog.show();

                //다이얼로그 중심 위젯
                ImageView iv_crystalBall = (ImageView) elf_dialog.findViewById(R.id.iv_crystalBall);
                TextView tv_elfSays = (TextView) elf_dialog.findViewById(R.id.tv_elfSays);
                Button btn_closeDialog = (Button) elf_dialog.findViewById(R.id.btn_closeDialog);


                //다이얼로그 크리스탈 볼 주변 이미지뷰
                ImageView iv_blackStars, iv_orangeStar, iv_yellowStar, iv_smallStars;
                iv_blackStars = (ImageView) elf_dialog.findViewById(R.id.iv_blackStars);
                iv_orangeStar = (ImageView) elf_dialog.findViewById(R.id.iv_orangeStar);
                iv_yellowStar = (ImageView) elf_dialog.findViewById(R.id.iv_yellowStar);
                iv_smallStars = (ImageView) elf_dialog.findViewById(R.id.iv_smallStars);

                //다이얼로그 내 객체에 생명 불어넣어주기

                //1) 이미지뷰
                iv_crystalBall.setImageResource(R.drawable.crystal_ball_128x128px);
                iv_blackStars.setImageResource(R.drawable.blck_stars_64px);
                iv_orangeStar.setImageResource(R.drawable.orange_star_128px);
                iv_yellowStar.setImageResource(R.drawable.yellow_star_128);
                iv_smallStars.setImageResource(R.drawable.blck_stars_64px);

                ///1-1) 이미지뷰 애니메이션
                Animation blackStars = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blackstars);
                iv_blackStars.startAnimation(blackStars);

                Animation smallblackStars = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.smallblackstars);
                iv_smallStars.startAnimation(smallblackStars);

                Animation crystalBall = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.crystalball_set);
                iv_crystalBall.startAnimation(crystalBall);


                //2) 텍스트뷰
                tv_elfSays.setTextColor(Color.BLACK);
                Animation elf_says = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.elfsays_alpha);
                tv_elfSays.startAnimation(elf_says);


                //3) 다이얼로그를 닫는 버튼
                btn_closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        elf_dialog.dismiss();
                    }//onClick
                });//setOnClickListener


                //다이얼로그 내 텍스트위젯에 메시지 띄우기
                if (hour >= 8 && hour <= 11) {
                    tv_elfSays.setText(goodMorning[randomValue]);
                    //Toast.makeText(MainActivity.this, goodMorning[mediumRandomValue] , Toast.LENGTH_SHORT).show();
                }
                if (hour >= 12 && hour <= 17) {
                    tv_elfSays.setText(goodAfternoon[mediumRandomValue]);
                    //Toast.makeText(MainActivity.this, goodAfternoon[randomValue] , Toast.LENGTH_SHORT).show();
                }

                if (hour >= 18 && hour <= 23 || hour >= 0 && hour < 1) //밤 12시까지
                {
                    tv_elfSays.setText(goodEvening[bigRandomValue]);
                    //Toast.makeText(MainActivity.this, goodEvening[bigRandomValue] , Toast.LENGTH_SHORT).show();
                }

/*                    if(hour >= 21 && hour <=23)
                    {
                        tv_elfSays.setText(goodNight[randomValue]);
                        Toast.makeText(MainActivity.this, goodNight[randomValue] , Toast.LENGTH_SHORT).show();
                    }*/

  /*                  if(hour >=0 && hour < 1) //밤 12시까지
                    {
                        tv_elfSays.setText(goodEvening[bigRandomValue]);
                        Toast.makeText(MainActivity.this, goodEvening[bigRandomValue] , Toast.LENGTH_SHORT).show();
                    }
*/
                if (hour >= 1 && hour <= 4) {
                    tv_elfSays.setText(goodLateNight[randomValue]);
                    //Toast.makeText(MainActivity.this, goodLateNight[randomValue] , Toast.LENGTH_SHORT).show();
                }

                if (hour >= 5 && hour <= 7) {
                    tv_elfSays.setText(goodEarlyMorning[randomValue]);
                    //Toast.makeText(MainActivity.this, goodEarlyMorning[randomValue] , Toast.LENGTH_SHORT).show();
                }

            }//OnClick
        });//setOnClickListener


        //다이어리 액티비티 뷰
        final EditText ed_diarydate = (EditText) findViewById(R.id.ed_diarydate);
        final EditText ed_diaryplace = (EditText) findViewById(R.id.ed_diaryplace);
        final EditText ed_diaryevent = (EditText) findViewById(R.id.ed_diaryevent);
        final EditText ed_diarynote = (EditText) findViewById(R.id.ed_diarynote);

        //다이어리 '탭'에 띄워줄 것들
        listview1 = (ListView) findViewById(R.id.listview1);
        item_dlist = new ArrayList<Item_diary>();
        adapter = new Adapter(this, R.layout.listview_diary, item_dlist);
        //listview1.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        //멀티플 초이스 모드로 하면 삭제버튼이 작동을 안 함

        listview1.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listview1.setAdapter(adapter);


        //메인화면 뷰페이저(광고화면)
        handler = new Handler();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewpagerAdapter = new ViewpagerAdapter(getApplicationContext());
        viewPager.setAdapter(viewpagerAdapter);
        // viewPager.setCurrentItem(adapter.myImages.length*1000); //myImages의 리턴값이 Integer.MAX_VALUE이므로 오류발생

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
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


        //메모창 열기
        memoBtn = (Button) findViewById(R.id.memoBtn);
        memoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                Intent memointent = new Intent(getApplicationContext(), MemoActivity.class);
                startActivityForResult(memointent, REQUEST_ADD_MEMO);
            }
        });//setOnClickListener


        //STT창 열기
        sttBtn = (Button) findViewById(R.id.sttBtn);
        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sttIntent = new Intent(getApplicationContext(), STTActivity.class);
                startActivityForResult(sttIntent, REQUEST_MEMO_STT);
            }//onClick
        });//setOnClickListener


        //녹음창 열기
        recordBtn = (Button) findViewById(R.id.recordBtn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent recordIntent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivityForResult(recordIntent, REQUEST_MEMO_RECORD);

            }//onClick
        });//ibtn_record.setOnClickListener


        //다이어리 리스트 숏클릭 시 미리보기 다이얼로그를 띄워주자
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long ld) {
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
                TextView cd_diaryplace = (TextView) view.findViewById(R.id.cd_diaryplace);
                TextView cd_diaryevent = (TextView) view.findViewById(R.id.cd_diaryevent);
                TextView cd_diarynote = (TextView) view.findViewById(R.id.cd_diarynote);

                TextView diarydate = (TextView) view.findViewById(R.id.diarydate);
                TextView diaryplace = (TextView) view.findViewById(R.id.diaryplace);
                TextView diaryevent = (TextView) view.findViewById(R.id.diaryevent);
                TextView diarynote = (TextView) view.findViewById(R.id.diarynote);


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
                cd_iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.cd_iv_close)
                            dialog.dismiss();
                    }//onClick
                });//setOnClickListener

            }//onItemClick
        });//listview1.setOnItemClickListener


        //TODO 리스트 슬라이드 하면 삭제하고 싶다
        //롱클릭 시 수정/삭제할 수 있는 다이얼로그
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            //첫번째: 엄마view(listview1)
            //두번째: 이벤트가 발생한 항목 뷰(리스트뷰에 들어가있는 하나하나의 row)
            //세번째: 이벤트가 발생한 인덱스(선택한 자식의 인덱스 리턴)
            //네번째" 이벤트가 발생한 항목 뷰의 아이디(안드로이드가 알아서 매김)
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long Id) {

                index = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String[] choices = {"수정", "삭제"};
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }//setNegativeButton onClick
                }) //setNegativeButton done
                        .setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which) {
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
                                                .setMessage(item_dlist.get(index).getDate() + "에 쓰신 일기를 정말로 삭제하시겠어요?")
                                                .setCancelable(false) //뒤로 버튼 클릭 시 취소가능설정 여부
                                                .setPositiveButton("삭제 할래요", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //다이어리를 삭제한다
                                                        item_dlist.remove(position);
                                                        listview1.clearChoices();
                                                        adapter.notifyDataSetChanged();
                                                    }

                                                })
                                                .setNegativeButton("삭제 안 해요", new DialogInterface.OnClickListener() {
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


        //저장했던 것 세팅해랏
        restoreState();

    } //onCreate end


    @Override
    public void onClick(View v) {
        ///// 이게 이렇게 4개면 안 될 것 같음..
        //날짜, 한줄만
        if (v.getId() == R.id.ibtn_add_dairy) {
            Intent addintent = new Intent(getApplicationContext(), DiaryActivity.class);
            startActivityForResult(addintent, REQUEST_EDIT);
        }
    }//onclick 끝

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    //TODO
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {// Toast.makeText(getApplicationContext(), "resultCode == RESULT_OK", Toast.LENGTH_SHORT).show();

            switch (requestCode) {
                case REQUEST_EDIT: {//Toast.makeText(getApplicationContext(), "requestCode == REQUEST_EDIT", Toast.LENGTH_SHORT).show();
                    TextView maindate = (TextView) listview1.findViewById(R.id.maindate);
                    TextView mainevent = (TextView) listview1.findViewById(R.id.mainevent);
                    TextView mainplace = (TextView) listview1.findViewById(R.id.mainplace);
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
                    } catch (IOException e) {
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
                    //다이어리 수정
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

                    Log.d("mintoBitmapUri", "mintoBitmapUri" + mintoBitmapUri);

                    try {
                        muserphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mintoBitmapUri);
                        Log.d("사진수정", "사진수정" + muserphoto);
                    } catch (IOException e) {
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
                    Log.e("메모제목", "메모제목" + data.getStringExtra("memotitle"));

                    String memonote = data.getStringExtra("memonote");
                    Log.e("메모내용", "메모내용" + data.getStringExtra("memonote"));

                    if (memotitle.length() > 0 || memonote.length() > 0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                    {
                        item_memo = new Item_memo(memotitle, memonote);
                        item_memoList.add(item_memo);
                        Log.e("메모", "" + item_memoList.size());
                        memoRcvAdapter.notifyDataSetChanged();
                    }

                    break;

                case REQUEST_MEMO_STT: //STT 메모 추가
                    String sttTitle = data.getStringExtra("sttTitle");
                    String sttNote = data.getStringExtra("sttNote");

                    if (sttNote.length() > 0 || sttTitle.length() > 0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                    {
                        item_memo = new Item_memo(sttTitle, sttNote);
                        item_memoList.add(item_memo);
                        //memoRcvAdapter.addItem(sttTitle, sttNote);
                        memoRcvAdapter.notifyDataSetChanged();
                    }
                    break;

                case REQUEST_MEMO_RECORD: //오디오 메모 추가
                    String recordmemoTitle = data.getStringExtra("recordmemoTitle");
                    String recordmemoNote = data.getStringExtra("recordmemoNote");
                    String special_audioUri = data.getStringExtra("special_audioUri"); //예시: /storage/emulated/0/recorded.mp41120276096


                    //Log.e("받은 파일주소+해시코드", special_audioUri);


                    //오디오파일이 없을 떄
                    if (special_audioUri == null) {
                        //텍스트 메모는 있음(텍스트 메모 없으면 취급안함)
                        if (recordmemoTitle.length() > 0 || recordmemoNote.length() > 0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                        {
                            item_memo = new Item_memo(recordmemoTitle, recordmemoNote);
                            item_memoList.add(item_memo);
                            memoRcvAdapter.notifyDataSetChanged();
                        }
                    }

                    //오디오 파일있음
                    else {
                        //Uri thisAudioUri = Uri.parse(special_audioUri);
                        // Log.e("받은 파일주소+해시코드2", ""+thisAudioUri); //예시: /storage/emulated/0/recorded.mp41120276096

                        item_memo = new Item_memo(special_audioUri, recordmemoTitle, recordmemoNote);
                        memoRcvAdapter.addItem(special_audioUri, recordmemoTitle, recordmemoNote);
                        memoRcvAdapter.notifyDataSetChanged();
                    }

                    break;

                case REQUEST_MODIFY_TXTMEMO: //텍스트 스타일 메모 수정

                    // 직렬화된 클래스 주고 받는 것... 실 to the 패
                    // Item_memo Modified_item_memo = (Item_memo) data.getSerializableExtra("m_memo");
                    // String title = Modified_item_memo.getMemo1();
                    // String note = Modified_item_memo.getMemo2();
                    // memoRcvAdapter.editItem(Modified_item_memo);


                    String mTitle = data.getStringExtra("mTitle");
                    String mNote = data.getStringExtra("mNote");
                    int arrayIndex = data.getExtras().getInt("arrayIndex");
                    Log.e("arrayIndex", "" + arrayIndex);

                    if (mTitle.length() > 0 || mNote.length() > 0) {
                        item_memoList.get(arrayIndex).setMemo1(mTitle);
                        item_memoList.get(arrayIndex).setMemo2(mNote);
                        memoRcvAdapter.notifyDataSetChanged();
                    }

                    break;

                case REQUEST_MODIFY_AUIDOMEMO:

                    int recordmemo_arrayIndex = data.getExtras().getInt("arrayIndex");
                    String m_recordmemoTitle = data.getStringExtra("m_recordmemoTitle");
                    String m_recordmemoNote = data.getStringExtra("m_recordmemoNote");
                    String m_special_audioUri = data.getStringExtra("special_audioUri");
                    Log.e("수정->메인 받는 파일주소+해시", m_special_audioUri);


                    File file = new File(m_special_audioUri);
                    Log.e("메인, 오디오파일 삭제후", ""+file.exists()); //->false

                    //오디오 파일이 있다면 ..새로 추가
                    if(file.exists())
                    {
                        item_memoList.get(recordmemo_arrayIndex).setMemo1(m_recordmemoTitle);
                        item_memoList.get(recordmemo_arrayIndex).setMemo2(m_recordmemoNote);
                        item_memoList.get(recordmemo_arrayIndex).setAudioUri(m_special_audioUri);
                        Log.e("수정->메인 세팅 파일주소+해시 ", m_special_audioUri);

                        memoRcvAdapter.notifyDataSetChanged();
                    }

                    //**  //텍스트는 아예 없고 오디오만 있을 때
                    if (m_recordmemoTitle.length() < 0 && m_recordmemoNote.length() < 0 && file.exists() /*파일.exist추가*/) {
                        item_memoList.get(recordmemo_arrayIndex).setMemo1(m_recordmemoTitle);
                        item_memoList.get(recordmemo_arrayIndex).setMemo2(m_recordmemoNote);
                        item_memoList.get(recordmemo_arrayIndex).setAudioUri(m_special_audioUri);

                        memoRcvAdapter.notifyDataSetChanged();
                    }

                    //오디오파일이 없을 떄
                    //if (m_special_audioUri == null) {
                    if(!file.exists())
                    {
                        //텍스트 메모는 있음(텍스트 메모 없으면 취급안함)
                        if (m_recordmemoTitle.length() > 0 || m_recordmemoNote.length() > 0) //메모 제목/내용 중 하나라도 있으면 메모 생성
                        {
                            item_memoList.get(recordmemo_arrayIndex).setMemo1(m_recordmemoTitle);
                            item_memoList.get(recordmemo_arrayIndex).setMemo2(m_recordmemoNote);
                            memoRcvAdapter.notifyDataSetChanged();
                        }
                    }

                    //오디오 파일있음
                    else {

                        item_memoList.get(recordmemo_arrayIndex).setMemo1(m_recordmemoTitle);
                        item_memoList.get(recordmemo_arrayIndex).setMemo2(m_recordmemoNote);
                        item_memoList.get(recordmemo_arrayIndex).setAudioUri(m_special_audioUri);

                        memoRcvAdapter.notifyDataSetChanged();
                        }


                    break;


            }//switch
        }//resultCode ok

    }// onActivityResult 함수 닫기


    /////////////////////저장 * 재개
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
        handler.removeCallbacks(runnable);
    }

/*    @Override
    protected void onResume()
    {
        super.onResume();
        //restoreState();
        //여기에 놓으면 resume될 때마다 불러와서 리스트가 배로 생김
    }*/

    public void saveState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        /////////////////////json
        JSONArray jsonArray = new JSONArray();

        //리스트의 크기만큼 jsonObject를 만든다
        for (int i = 0; i < adapter.dlist.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            //for문이 한번 돌 때마다 key값이 변함
            String key_img = "Img" + i;
            String key_date = "date" + i;
            String key_place = "place" + i;
            String key_event = "event" + i;
            String key_note = "note" + i;

            try {
                jsonObject.put(key_img, adapter.dlist.get(i).getUserphotoUri().toString());
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
        }//for

        //다이어리 저장
        editor.putString("diaryDetail", jsonArray.toString());
        editor.commit();


        //**   //메모 저장 테스트
        SharedPreferences pref_memo = getSharedPreferences("pref_memo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor_memo = pref_memo.edit();

        JSONArray jsonArray_memo = new JSONArray();

        //데이터의 크기만큼 jsonobject를 만든다
        for (int i = 0; i < memoRcvAdapter.item_memoList.size(); i++) {
            JSONObject jsonObject_memo = new JSONObject();


            String key_audio = "Audio" + i;
            String key_title = "Title" + i;
            String key_note = "Note" + i;

            try {
                jsonObject_memo.put(key_title, memoRcvAdapter.item_memoList.get(i).getMemo1());
                Log.e("제목 검사", key_title);
                jsonObject_memo.put(key_note, memoRcvAdapter.item_memoList.get(i).getMemo2());
                Log.e("내용 검사", key_note);

                String audioUri = memoRcvAdapter.item_memoList.get(i).getAudioUri();
                jsonObject_memo.put(key_audio, memoRcvAdapter.item_memoList.get(i).getAudioUri());
                jsonArray_memo.put(jsonObject_memo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }//for

        editor_memo.putString("memoDetail", jsonArray_memo.toString());
        editor_memo.commit();
        //테스트 메모 저장 세팅 끝
    }//saveState()

    public void restoreState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        try {
            JSONArray jsonArray = new JSONArray(pref.getString("diaryDetail", ""));

            if (jsonArray.length() != 0) {
                editor.clear();
                editor.commit();
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                //jsonArray에서 jsonObject 꺼내기
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String key_img = "Img" + i;
                String key_date = "date" + i;
                String key_place = "place" + i;
                String key_event = "event" + i;
                String key_note = "note" + i;

                String image = jsonObject.getString(key_img);
                String date = jsonObject.getString(key_date);
                String place = jsonObject.getString(key_place);
                String event = jsonObject.getString(key_event);
                String note = jsonObject.getString(key_note);

                Uri uri = Uri.parse(image);
                Bitmap userphoto = null;

                try {
                    //Item_diary item_diary;
                    userphoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    adapter.addItem(new Item_diary(date, place, event, note, userphoto, uri));
                    //adapter.addItems(date, place, event, note, userphoto, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } //for문

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();


        ///메모 테스트
        SharedPreferences pref_memo = getSharedPreferences("pref_memo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor_memo = pref_memo.edit();

        try {
            JSONArray jsonArray_memo = new JSONArray(pref_memo.getString("memoDetail", ""));
            Log.e("jsonArray복원", "jsonArray" + jsonArray_memo);
            if (jsonArray_memo.length() != 0) {
                editor_memo.clear();
                editor_memo.commit();
            }

            for (int i = 0; i < jsonArray_memo.length(); i++) {
                JSONObject jsonObject_memo = jsonArray_memo.getJSONObject(i);
                Log.e("데이터 검사", "" + jsonArray_memo.getJSONObject(i)); //여기까진 옴

                String audio;
                String key_audio = "Audio" + i;

                if (!jsonObject_memo.isNull(key_audio)) { //오디오 키값이 없으면
                    audio = jsonObject_memo.getString(key_audio); //넣어준다

                }

                if (jsonObject_memo.has(key_audio)) {

                    Log.e("몇번올까?", "누가올까?" + jsonObject_memo);
                    String key_title = "Title" + i;
                    String key_note = "Note" + i;

                    String title = jsonObject_memo.getString(key_title);
                    String note = jsonObject_memo.getString(key_note);
                    audio = jsonObject_memo.getString(key_audio);

                    try {
                        memoRcvAdapter.addItem(audio, title, note);
                        // memoRcvAdapter.addItem(new Item_memo(audio, title, note)); ...아 이것때문에 몇시간 날림 ㅡㅡ..

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("오냐마냐2", "kkkk");
                    String key_title = "Title" + i;
                    String key_note = "Note" + i;

                    String title = jsonObject_memo.getString(key_title);
                    String note = jsonObject_memo.getString(key_note);
                    memoRcvAdapter.addItem(new Item_memo(title, note));

                }
            }//for
        } catch (JSONException e) {
            e.printStackTrace();
        }
        memoRcvAdapter.notifyDataSetChanged();
        //메모 끝
    }//restoreState

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

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

} // Main Activity 클래스 닫기
