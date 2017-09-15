package com.example.luna.days;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by LUNA on 2017-09-10.
 */

public class ViewpagerAdapter extends PagerAdapter {


    Context context;
    Bitmap myImage;
    BitmapFactory.Options options;

    public final int[] myImages = new int[]
            {
                    R.drawable.travelad, R.drawable.treasure, R.drawable.kakaotalkrecruit, R.drawable.rabbit
            };


    ViewpagerAdapter(Context context) {
        this.context = context;
        options = new BitmapFactory.Options();
    }

    @Override
    public int getCount() {
        // return Images.length;
        // return myImages.length*3;  --> 이렇게 하면  pageSelected8부터 다시 page~0으로 돌아감(9개의 이미지가 리턴되므로)
        return Integer.MAX_VALUE;
        //최대 아이템 개수를 최대 크기만큼 잡아준다
        //그냥 이렇게 잡아줄 경우 인덱스 관련 exception 이 발생할 수 있기 때문에 instantiateItem 메쏘드에서는 아래와 같이 계산하여 실제 반환해야 하는 뷰를 찾아 생성해서 넘겨주도록 한다.

    }

    //instantiateItem에서 생성한 객체를 사용할지 여부 판단
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
        //return (view == object);
    }

    //viewPager에 사용할 view를 생성하고 등록
    //여기서는 리사이즈 후 넘겨줌
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        final int realpos =position%(4);
        ImageView myimageView = new ImageView(context);
       // int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
      //  myimageView.setPadding(padding, padding, padding, padding);
        myimageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //If set to a value > 1, requests the decoder to subsample the original image, returning a smaller image to save memory.
        options.inSampleSize = 1;
        myImage = BitmapFactory.decodeResource(context.getResources(), myImages[realpos], options);

        //각 이미지 클릭커블하게
        //https://stackoverflow.com/questions/16350987/viewpager-onitemclicklistener
        myimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(realpos ==0)
                {
                    //intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.lufthansa.com/kr/ko/Homepage?WT.srch=1&WT.mc_id=SEABRAND_lhcom_KR_ko&subID=1375280166205028552"));
                    // intent = new Intent(view.getContext().getApplicationContext(), Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "루프트한자 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                }
                if (realpos==1)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://event.talkchannel.kakao.com/acm/kakao/201709/open.html?slot=46&sh=2"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "카카오 채널로 이동합니다.", Toast.LENGTH_SHORT).show();
                }
                if (realpos ==2)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.welcomekakao.com/competitions/35/welcome-kakao"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "카카오톡 채용 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                }
                if (realpos ==3)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://event.talkchannel.kakao.com/acm/kakao/201709/open.html?slot=46&sh=2"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "카카오 채널로 이동합니다.", Toast.LENGTH_SHORT).show();
                }


            }//onClick
        });//setOnClickListener


        myimageView.setImageBitmap(myImage);
        ((ViewPager) container).addView(myimageView);

        return myimageView;
    }

    //화면에서 사라진 view삭제
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager)container).removeView((ImageView) object);
    }



}
