package com.amt.testgps.httpTask;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

public class AsynSendLocation {
    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";
    private String resposta = "";
    private String codi_resposta = "";

    public String SendLocation(String latitude, String longitude, String session_id,String battery){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = df.format(c.getTime());

        try {
            String postData = "{" +
                    "\"lat\":" + latitude + "," +
                    "\"lon\":" + longitude + "," +
                    "\"session_id\":" + session_id + "," +
                    "\"battery\":" + battery +
                    "}";
            writeToFile("["+formattedDate+"] "+"Sent: " + postData);

            URL myURL = new URL("http://www.raidmaqui.com/live/api/session/log");
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
                        System.out.println(response);
                    }
                    writeToFile("["+formattedDate+"] "+"Received: " + (String.valueOf(sb)));

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
                writeToFile("["+formattedDate+"] "+"Received: " + (String.valueOf(sb)));
                resposta = (String.valueOf(sb));
            }
            return resposta;
        } catch (Exception e) {
            Log.d("AsyncLogin: ", e.getLocalizedMessage());
            return resposta ;
        }
    }

    private void writeToFile(String data) {
        File storage = new File(Environment.getExternalStorageDirectory(), "TESTGPS");
        if (! storage.exists()){
            if (! storage.mkdirs()){
                Log.d("TESTGPS", "failed to create directory");
            }
        }
        try {
            FileWriter fileW = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/TESTGPS/Data.txt",true);
            fileW.append(data);
            fileW.append("\r\n");
            fileW.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}

