package com.example.adslibrary;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.adslibrary.BuildConfig;

import java.io.IOException;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;



/**
 * Created by isaacshrestha on 2/8/18.
 */



public class WebAppInterface {
   Context mContext;

    //posting data to ads server
    /*OkHttpClient client = new OkHttpClient();
    public Long systemTime = System.currentTimeMillis();
    public String stringSystemTime = Long.toString(systemTime);


    public String data;
    public String tempVal;*/

    public static String dataFromServer;
    public WebAppInterface(Context c) {
        mContext = c;
    }


    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        dataFromServer = toast;


        //calling request to server
        //requestToServer();

    }

    public static String someData() {
        return dataFromServer;
    }

}


