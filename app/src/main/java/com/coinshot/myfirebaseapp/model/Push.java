package com.coinshot.myfirebaseapp.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Push {
    public String to;
    public Data data;

    public Push(String to, String title, String message){
        this.to = to;
        data = new Data(title, message);
    }

    @Override
    public String toString() {
        return "Push{" + "to='" + to + '\'' + ", data=" + data + '}' ;
    }

    public static class Data{
        public String title;
        public String message;

        public Data(String title, String message){
            this.title = title;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Data{" + "title='" + title + '\'' + ", message='" + message + '\'' + '}';
        }
    }

}
