package com.amt.testgps.httpTask;

import android.os.AsyncTask;


public class AsyncHttpTask extends AsyncTask<String, Void, String>{

    private HttpHandler httpHandler;

    public AsyncHttpTask(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    // Operation , XML
    @Override
    protected String doInBackground(String... arg0) {
        String resposta = "";

        switch (arg0[0]){
            case "OpenSession":
                resposta = new AsyncOpenSession().OpenSession();
                return resposta;
            case "CreateSession":
                resposta = new AsynCreateSession().CreateSession(arg0[1]);
                return resposta;
            case "SendLocation":
                resposta = new AsynSendLocation().SendLocation(arg0[1],arg0[2],arg0[3]);
                return resposta;
            default:
                return resposta;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result);
    }
}