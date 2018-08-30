package com.narmware.samista.pojo;

/**
 * Created by rohitsavant on 28/08/18.
 */

public class LeadResponse {
    String response;
    Lead[] data;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Lead[] getData() {
        return data;
    }

    public void setData(Lead[] data) {
        this.data = data;
    }
}
