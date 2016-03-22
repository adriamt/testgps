package com.amt.testgps.httpTask;


public abstract class HttpHandler {

    public abstract void onResponse(String result);

    public void open_session(){
        new AsyncHttpTask(this).execute("OpenSession");
    }

    public void create_session(String user_id){
        new AsyncHttpTask(this).execute("CreateSession", user_id);
    }

    public void send_location(String latitude, String longitude, String session_id, String battery){
        new AsyncHttpTask(this).execute("SendLocation", latitude, longitude, session_id, battery);
    }

}