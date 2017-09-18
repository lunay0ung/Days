package com.example.luna.days;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by LUNA on 2017-09-07.
 */

public class Item_memo implements Serializable{

    //int id;
    // id = hashCode();

    Uri photomemoUri;
    String memo1, memo2;
    private int type;
    String fileName;
    String exactTime;


    /*   public Item_memo()
       {

       }
   */

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    //텍스트만
    public Item_memo(String memo1, String memo2)
    {
        this.memo1 = memo1;
        this.memo2 = memo2;

    }
/*

    //해시코드 생성
    @Override
    public int hashCode() {

    }

    @Override
    public boolean equals(Object obj) {
        Item_memo item_memo = (Item_memo) obj;
        if(obj == null || !(obj instanceof Item_memo))
        {
            return false;
        }
        return item_memo.memo1.equals(memo1) && item_memo.memo2.equals(memo2);
    }

*/


    //텍스트+녹음파일
    public Item_memo(String fileName, String memo1, String memo2)
    {
        this.fileName = fileName;
        this.memo1 = memo1;
        this.memo2 = memo2;
    }

    //사진만
    public Item_memo(Uri photomemoUri)
    {
        this.photomemoUri = photomemoUri;
    }


    //텍스트+사진
    public Item_memo(Uri photomemoUri, String memo1, String memo2)
    {
        this.photomemoUri = photomemoUri;
        this.memo1 = memo1;
        this.memo2 = memo2;
    }


    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Uri getPhotomemoUri() {
        return photomemoUri;
    }

    public void setPhotomemoUri(Uri photomemoUri) {
        this.photomemoUri = photomemoUri;
    }

    public String getMemo1() {
        return memo1;
    }

    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }

    public String getMemo2() {
        return memo2;
    }

    public void setMemo2(String memo2) {
        this.memo2 = memo2;
    }

}
