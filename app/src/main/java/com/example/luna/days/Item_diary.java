package com.example.luna.days;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by LUNA on 2017-08-28.
 */

public class Item_diary {

    //각각 날짜/장소/한줄요약/이미지
    String date;
    String place;
    String event;
    String note;
    Bitmap userphoto;
    Uri userphotoUri;

/*
    //날짜, 한줄요약
    public Item_diary (String date, String event)
    {
        this.date=date;
        this.event=event;
    }
*/

    //전부(노트까지)
    public Item_diary (String date, String place, String event, String note, Bitmap userphoto, Uri userphotoUri)
    {
        this.date=date;
        this.place=place;
        this.event=event;
        this.note=note;
        this.userphoto=userphoto;
        this.userphotoUri=userphotoUri;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public Bitmap getDiaryImage() {
        return userphoto;
    }

    public void setDiaryImage(Bitmap diaryImage) {
        this.userphoto = diaryImage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Bitmap getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(Bitmap userphoto) {
        this.userphoto = userphoto;
    }

    public Uri getUserphotoUri() {
        return userphotoUri;
    }

    public void setUserphotoUri(Uri userphotoUri) {
        this.userphotoUri = userphotoUri;
    }
}
