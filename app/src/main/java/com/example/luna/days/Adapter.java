package com.example.luna.days;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LUNA on 2017-08-28.
 */

public class Adapter extends BaseAdapter
{

    LayoutInflater inflater;
    Context context;
    int layout;
    //다이어리 리스트
    ArrayList<Item_diary> dlist;

/*    //메모 리스트
    ArrayList<Item_memo> mlist;*/

    public Adapter(Context context, int layout, ArrayList<Item_diary> dlist)
    {
        this.context=context;
        this.layout=layout;
        //각 항목을 나타내는 layout파일의 resource ID->int. item_layout.xml이 이자리에 들어감.
        this.dlist=dlist;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dlist.size();
    }

    @Override
    public Object getItem(int position) {
        return dlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
        {
        if (convertView == null)
        {
            convertView = inflater.inflate(layout,parent,false);
        }

        ImageView mainphoto = (ImageView) convertView.findViewById(R.id.mainphoto);
        TextView mainplace = (TextView) convertView.findViewById(R.id.mainplace);
        TextView maindate = (TextView) convertView.findViewById(R.id.maindate);
        TextView mainevent = (TextView) convertView.findViewById(R.id.mainevent);

        //이미지 둥글게
        //...API 21이상에서부터 되네 ㅋ
/*            mainphoto.setBackground(new ShapeDrawable(new OvalShape()));
            mainphoto.setClipToOutline(true);*/

        mainphoto.setImageBitmap(dlist.get(position).userphoto);
        mainplace.setText(dlist.get(position).getPlace());
        maindate.setText(dlist.get(position).getDate());
        mainevent.setText(dlist.get(position).getEvent());

        return convertView;
    }

    public void addItem(Item_diary item_diary)
    {
        dlist.add(item_diary);
    }

}
