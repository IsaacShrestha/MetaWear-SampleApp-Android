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


public class AppHook {
    OkHttpClient okHttpClient = new OkHttpClient();

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

    //okHttp request/response to secuwear-webapp
    public void responseFromServer(Request request){
        //response here
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {

                    throw new IOException("Unexpected code " + response);

                } else {
                    // do something wih the result
                    Log.i(TAG, response.body().string());
                }

            }


        });
    }
    //okHttp request/response to server ends...

}