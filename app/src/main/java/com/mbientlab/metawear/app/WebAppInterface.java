package com.mbientlab.metawear.app;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by isaacshrestha on 2/6/18.
 */

public class WebAppInterface {
    Context mContext;

    //posting data to ads server
    public static OkHttpClient client = new OkHttpClient();
    public Long systemTime = System.currentTimeMillis();
    public String stringSystemTime = Long.toString(systemTime);
    //public static temp;



    WebAppInterface(Context c) {
        mContext = c;
    }


    @JavascriptInterface
    public void showToast(String toast) { //I am not calling this function
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

        //calling request to server
        //requestToServer();

    }

    //Request to server
    @JavascriptInterface
    public void requestToServer() {

        //Toast.makeText(mContext, Temp, Toast.LENGTH_SHORT).show();

        //Finding device MAC
        WifiManager manager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        //Finding device model name
        String model = Build.MODEL;
        String reqString = Build.MANUFACTURER+" " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.SERIAL;

        //Full device info with MAC address
        String fullDeviceAddress = reqString +" "+ address;

        //Get package name
          String packageName = BuildConfig.APPLICATION_ID;



        //getting cookie
            String cookies = CookieManager.getInstance().getCookie("http://192.168.0.5:4000/api/temperature");


        Log.i(TAG, "COOKIES = "+packageName);
        //Creating data for server
        RequestBody body = new FormBody.Builder()
                .add("device", fullDeviceAddress)
                .add("time", stringSystemTime)
                .add("package", packageName)
                .add("temperature", "temperature")
                .add("cookies", cookies)
                .build();

        //requests here
        Request request = new Request.Builder()
                .url("http://ishrestha.com/metawear-ads/ads.php")
                .post(body)
                .build();

        //calling response
        responseFromServer(request);

    }


    //Response from server
    public void responseFromServer(Request request) {
        //response here
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "#######" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {

                    throw new IOException("Unexpected code " + response);

                } else {
                    // do something wih the result
                    try {
                        Log.i(TAG, response.body().string());

                    } catch (OutOfMemoryError e) {
                        Log.i(TAG, "####line 117");

                    }
                }
            }
        });

    }//End of Response
}
