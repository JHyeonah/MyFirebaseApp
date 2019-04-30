package com.coinshot.myfirebaseapp.model;

import java.util.List;

public class Response {
    public Integer multicastId;
    public Integer success;
    public Integer failure;
    public Integer canonicalIds;
    public List<Result> results = null;

    public static class Result {

        public String messageId;

    }

}
