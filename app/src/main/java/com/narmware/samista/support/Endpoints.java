package com.narmware.samista.support;

/**
 * Created by rohitsavant on 08/08/18.
 */

public class Endpoints {
    public static final String BASE_URL = "http://sasmitabussiness.com/master/api/";
    public static final String LOGIN_URL = BASE_URL + "converter_login.php";
    public static final String GET_LIVE_LEADS_URL = BASE_URL + "get_live_leads.php";
    public static final String GET_CLOSED_LEADS_URL = BASE_URL + "lead_close.php";
    public static final String GET_COMMENTS = BASE_URL + "get_comment.php";
    public static final String ADD_COMMENTS = BASE_URL + "add_comment.php";
    public static final String SEND_SELFIE = BASE_URL + "img_upload.php";

    //Variables
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    public static final String USER_ID= "user_id";

    public static final String GENERATOR= "GENERATOR";
    public static final String CONVERTOR= "CONVERTOR";

    public static final String LEAD_ID = "lead_id";
    public static final String COMMENT = "comments";
    public static final String STATUS = "status";

    public static final String CLOSED_LEAD_IMG= "image";
    public static final String CLOSEDIN= "CLOSEIN";
    public static final String CLOSEDOUT= "CLOSEOUT";
    public static final String OPEN = "LEAD";

}
