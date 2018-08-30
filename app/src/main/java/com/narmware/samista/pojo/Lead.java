package com.narmware.samista.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rohitsavant on 22/08/18.
 */

public class Lead implements Serializable {
    String id;
    String trading_name;
    String contact_name;
    String mobile_number;
    String city;
    String loan_type;
    String channel_partner;
    String remark;
    String comment;
    String pincode;
    String doing_business_since;
    String business_address;
    String designation;
    String nature_of_business;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrading_name() {
        return trading_name;
    }

    public void setTrading_name(String trading_name) {
        this.trading_name = trading_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLoan_type() {
        return loan_type;
    }

    public void setLoan_type(String loan_type) {
        this.loan_type = loan_type;
    }

    public String getChannel_partner() {
        return channel_partner;
    }

    public void setChannel_partner(String channel_partner) {
        this.channel_partner = channel_partner;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDoing_business_since() {
        return doing_business_since;
    }

    public void setDoing_business_since(String doing_business_since) {
        this.doing_business_since = doing_business_since;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getNature_of_business() {
        return nature_of_business;
    }

    public void setNature_of_business(String nature_of_business) {
        this.nature_of_business = nature_of_business;
    }
}
