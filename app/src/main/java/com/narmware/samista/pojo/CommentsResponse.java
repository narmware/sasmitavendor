package com.narmware.samista.pojo;

/**
 * Created by rohitsavant on 29/08/18.
 */

public class CommentsResponse {
    String response;
    Comments[] data;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Comments[] getData() {
        return data;
    }

    public void setData(Comments[] data) {
        this.data = data;
    }
}
