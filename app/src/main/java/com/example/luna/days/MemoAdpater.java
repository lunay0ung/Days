package com.example.luna.days;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LUNA on 2017-09-07.
 */

public class MemoAdpater extends BaseAdapter{


    ArrayList<Item_memo> item_memoList;
    LayoutInflater inflater;
    Context context;
    int memolayout, voicmemolayout, layout, layout2;

/*    public final int[] layoutImages = new int[]
            {
                    R.drawable.dottedline2, R.drawable.play_button_32px
            };*/


    private static final int GRIDVIEW_MEMO = 0;
    private static final int GRIDVIEW_W_RECORD = 1;
    private static final int GRIDVIEW_W_PHOTO = 2;
    private static final int GRIDVIEW_ONLYPHOTO = 3;
    private static final int GRIDVIEW_TYPE_MAX = 4;

    //layout, layout2 각각 memo, voicememo레이아웃
    public MemoAdpater(Context context, int layout, ArrayList<Item_memo> item_memoList)
    {
        this.context = context;
        this.layout= layout;
        this.item_memoList = item_memoList;
        inflater = LayoutInflater.from(context);
    }

        public MemoAdpater()
        {

        }

    @Override
    public int getViewTypeCount() {
        return GRIDVIEW_TYPE_MAX;
    }

    //position위치의 아이템 타입 리턴
    @Override
    public int getItemViewType(int position) {
        return item_memoList.get(position).getType();
    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return item_memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return item_memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {final Context context = parent.getContext();
       int viewType = getItemViewType(position);

      /*
        일단 정상적으로 되는 것.
        if(convertView == null)
        {
            convertView = inflater.inflate(layout, parent, false);
        }
        TextView tv_mainmemo1 = (TextView) convertView.findViewById(R.id.tv_mainmemo1);
        TextView tv_mainmemo2 = (TextView) convertView.findViewById(R.id.tv_mainmemo2);

        tv_mainmemo1.setText(item_memoList.get(position).getMemo1());
        tv_mainmemo2.setText(item_memoList.get(position).getMemo2());
           // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
*/
        if(convertView == null) {
            //convertView = inflater.inflate(layout, parent, false);
            convertView = inflater.inflate(layout, parent, false);
            //데이터셋에서 position에 위치한 데이터 참조 획득
            Item_memo item_memo = item_memoList.get(position);

            

        }

            switch (viewType)
            {
                case GRIDVIEW_MEMO:
                    convertView = inflater.inflate(R.layout.gridview_memo, parent, false);
                    Log.e("메모","adapter");
                    TextView tv_mainmemo1 = (TextView) convertView.findViewById(R.id.tv_mainmemo1);
                    TextView tv_mainmemo2 = (TextView) convertView.findViewById(R.id.tv_mainmemo2);
                    LinearLayout memo_layout1 = (LinearLayout) convertView.findViewById(R.id.memo_layout1);

                    tv_mainmemo1.setText(item_memoList.get(position).memo1);
                    tv_mainmemo2.setText(item_memoList.get(position).memo2);

                    memo_layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("클릭2", "클릭2");
                        }
                    });

                    break;

                case GRIDVIEW_W_RECORD:
                    convertView = inflater.inflate(R.layout.gridview_stt, parent, false);
                    //gridview_stt 오해의 소지가 있지만 녹음파일 있는 것, STT는 TXT이므로 GRIDVIEW_MEMO적용

                    TextView tv1_mainmemo_record = (TextView) convertView.findViewById(R.id.tv1_mainmemo_record);
                    TextView tv2_mainmemo_record = (TextView) convertView.findViewById(R.id.tv2_mainmemo_record);
                    LinearLayout memo_layout_record = (LinearLayout) convertView.findViewById(R.id.memo_layout_record);
                    //ImageView dottedLine = (ImageView) convertView.findViewById(R.id.dottedLine);
                    Button fakePlayBtn = (Button) convertView.findViewById(R.id.fakePlayBtn);

                    tv1_mainmemo_record.setText(item_memoList.get(position).memo1);
                    tv2_mainmemo_record.setText(item_memoList.get(position).memo2);


                    memo_layout_record.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("클릭", "클릭");
                        }
                    });


                    //dottedLine.setImageResource(R.drawable.smalldots_16px);
                    fakePlayBtn.setBackgroundResource(R.drawable.simple_play_btn_24px);
                    //this, R.drawable.dottedline2
                    //fakePlayBtn.set

                    break;

              /*  case GRIDVIEW_W_PHOTO:
                    convertView = inflater.inflate(R.layout.gridview_withphoto, parent, false);

                    TextView tv1_mainmemo_photo = (TextView) convertView.findViewById(R.id.tv1_mainmemo_photo);
                    TextView tv2_mainmemo_photo = (TextView) convertView.findViewById(R.id.tv2_mainmemo_photo);
                    ImageView withPhoto = (ImageView) convertView.findViewById(R.id.withPhoto);
                    tv1_mainmemo_photo.setText(item_memoList.get(position).getMemo1());
                    tv2_mainmemo_photo.setText(item_memoList.get(position).getMemo2());
                    withPhoto.setImageURI(item_memoList.get(position).getPhotomemoUri());

                    break;

                case GRIDVIEW_ONLYPHOTO:
                    convertView = inflater.inflate(R.layout.gridview_onlyphoto, parent, false);

                    ImageView onlyPhoto = (ImageView) convertView.findViewById(R.id.onlyPhoto);
                    onlyPhoto.setImageURI(item_memoList.get(position).getPhotomemoUri());

                    break;
*/
            }//switch
      //  }// if(convertView == null)






        return convertView;
    }


   /* public void addMemo(Item_memo item_memo)
    {
        item_memoList.add(item_memo);
    }*/


    public void addItem (String title, String note)
    {
        Item_memo item_memo = new Item_memo(title, note);
        item_memo.setType(GRIDVIEW_MEMO);

        item_memoList.add(item_memo);
    }

    public void addItem(String fileName, String title, String note)
    {
        Item_memo item_memo = new Item_memo(fileName, title, note);
        item_memo.setType(GRIDVIEW_W_RECORD);

        item_memoList.add(item_memo);
    }

}



