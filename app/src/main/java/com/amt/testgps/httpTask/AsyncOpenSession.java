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

//WEB : http://examples.javacodegeeks.com/core-java/json/json-parsing-with-gson/

public class AsyncOpenSession {

    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";
    private String resposta = "";
    private String codi_resposta = "";

    public String OpenSession(){
        try {
            String postData = "{" +
                    "\"mail\":\"adriamt1@gmail.com\"," +
                    "\"token\":\"test\"" +
                    "}";

            URL myURL = new URL("http://www.raidmaqui.com/live/api/account/login");
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            myURLConnection.setRequestProperty("Content-Type", "application/json");
            //myURLConnection.setRequestProperty("User-Agent", "Monsters/a.3.1.3");
            myURLConnection.setRequestProperty("Connection","Keep-Alive");
            myURLConnection.setRequestProperty("Accept-Encoding","gzip");
            //myURLConnection.setRequestProperty("Host","puzzlemonsters.pennypop.com");
            myURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            //myURLConnection.setUseCaches(false);
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);
            byte[] outputInBytes = postData.getBytes("UTF-8");
            OutputStream os = myURLConnection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            codi_resposta = (String.valueOf(myURLConnection.getResponseCode()));

            System.out.println("Sent: " + outputInBytes + " to " + myURL + " received " + codi_resposta);


            if (GZIP_CONTENT_TYPE.equals(myURLConnection.getContentEncoding()))            {
                System.out.println("Using gzip stream");
                inputStream = new GZIPInputStream(myURLConnection.getInputStream());
            }else{
                System.out.println("Using uncompressed stream");
                inputStream =  myURLConnection.getInputStream();
            }

            System.out.println(inputStream);

            if(codi_resposta.equals("200")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String response ;
                    while((response = reader.readLine()) != null) {
                        sb.append(response);
                        System.out.println(response);
                    }
                    JSONObject jo = new JSONObject((String.valueOf(sb)));
                    JSONArray data = jo.getJSONArray("data");
                    JSONObject data1 = new JSONObject(data.getString(0));
                    resposta = (data1.getString("id"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String response;
                while((response = reader.readLine()) != null) {
                    sb.append(response);
                    System.out.println(response);
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
