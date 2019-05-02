package com.coinshot.myfirebaseapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {
    public int multicastId;
    public int success;
    public int failure;
    public int canonicalIds;
    public List<Result> results = null;

    public static class Result{
        public String messageId;
    }

}
