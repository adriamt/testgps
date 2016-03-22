package com.amt.testgps.httpTask;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class AsynCreateSession {
    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";
    private String resposta = "";
    private String codi_resposta = "";

    public String CreateSession(String user_id){
        try {
            String postData = "{" +
                    "\"user_id\":" + user_id +
                    "}";

            URL myURL = new URL("http://www.raidmaqui.com/live/api/session/create");
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            myURLConnection.setRequestProperty("Content-Type", "application/json");
            myURLConnection.setRequestProperty("Connection","Keep-Alive");
            myURLConnection.setRequestProperty("Accept-Encoding","gzip");
            myURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);
            byte[] outputInBytes = postData.getBytes("UTF-8");
            OutputStream os = myURLConnection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            codi_resposta = (String.valueOf(myURLConnection.getResponseCode()));


            if (GZIP_CONTENT_TYPE.equals(myURLConnection.getContentEncoding())){
                inputStream = new GZIPInputStream(myURLConnection.getInputStream());
            }else{
                inputStream =  myURLConnection.getInputStream();
            }


            if(codi_resposta.equals("200")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String response ;
                    while((response = reader.readLine()) != null) {
                        sb.append(response);
                    }
                    JSONObject jo = new JSONObject((String.valueOf(sb)));
                    JSONObject data = jo.getJSONObject("data");
                    resposta = (data.getString("id"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String response;
                while((response = reader.readLine()) != null) {
                    sb.append(response);
                }
                resposta = (String.valueOf(sb));
            }
            return resposta;
        } catch (Exception e) {
            Log.d("AsyncLogin: ", e.getLocalizedMessage());
            return resposta ;
        }
    }
}

