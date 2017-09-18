package com.example.luna.days;

import java.io.Serializable;

/**
 * Created by LUNA on 2017-09-07.
 */

public class Item_memo implements Serializable{



    String memo1, memo2;
    private int type;
    String fileName;


    //텍스트만
    public Item_memo(String memo1, String memo2)
    {
        this.memo1 = memo1;
        this.memo2 = memo2;

    }

    //텍스트+녹음파일
    public Item_memo(String fileName, String memo1, String memo2)
    {
        this.fileName = fileName;
        this.memo1 = memo1;
        this.memo2 = memo2;
    }



    public String getAudioUri() {
        return fileName;
    }

    public void setAudioUri(String fileName) {
        this.fileName = fileName;
    }



    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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
