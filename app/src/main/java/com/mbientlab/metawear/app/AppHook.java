package com.mbientlab.metawear.app;

import android.util.Log;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


import static android.content.ContentValues.TAG;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.lang.Object;


public class AppHook {

    public static OkHttpClient client = new OkHttpClient();

    //post request to SecuWear
    public void posttoSecuWear(String url,Long systemTime, String event, String codeFile, String codeLine){
        //URL to post
        String strUrl = url;

        //Creating data for server
        RequestBody body = new FormBody.Builder()
                .add("systemTime", systemTime.toString())
                .add("eventtype", "Request from MetaWear to WebApp")
                .add("event", event )
                .add("codereference" , codeFile+" : "+codeLine)
                .add("domain", "Mobile")
                .add("run", "1")
                .build();

        //requests here
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();

        //calling response
            responseFromServer(request);



    }

    //post request for single data
    public void postSingleData(String url, String name, String data){
        //URL to post
        String strUrl = url;

        //Creating data for server
        RequestBody body = new FormBody.Builder()
                .add(name , data)
                .build();

        //requests here
        Request request = new Request.Builder()
                .url(strUrl)
                .post(body)
                .build();

        //calling response
        responseFromServer(request);

    }

    //post request for two data
    public void postTwoData(String url, String name1, String name2, String data1, String data2){
        //URL to post
        String strUrl = url;

        //Creating data for server
        RequestBody body = new FormBody.Builder()
                .add(name1 , data1)
                .add(name2, data2)
                .build();

        //requests here
        Request request = new Request.Builder()
                .url(strUrl)
                .post(body)
                .build();

        //calling response
        responseFromServer(request);
    }

    //Response from server
    public void responseFromServer(Request request){
        //response here
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "#######"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {

                    throw new IOException("Unexpected code " + response);

                } else {
                    // do something wih the result
                    try {
                        Log.i(TAG, response.body().string());

                    }catch (OutOfMemoryError e){
                        Log.i(TAG, "####line 117");

                    }




                    //InputStream inputStream.close();
                }
            }



        });



    }
    //okHttp request/response to server ends...

}