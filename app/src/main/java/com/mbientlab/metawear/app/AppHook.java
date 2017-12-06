package com.mbientlab.metawear.app;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.app.help.HelpOption;
import com.mbientlab.metawear.app.help.HelpOptionAdapter;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.Temperature;
import com.mbientlab.metawear.module.Temperature.SensorType;
import com.mbientlab.metawear.module.Timer;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//import javax.security.auth.callback.Callback;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
//import com.squareup.okhttp.OkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;


import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.VISIBLE;
import static junit.framework.Assert.assertTrue;


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