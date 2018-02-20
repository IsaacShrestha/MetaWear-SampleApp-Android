package com.mbientlab.metawear.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mbientlab.metawear.app.help.HelpOption;
import com.mbientlab.metawear.app.help.HelpOptionAdapter;

/**
 * Created by isaacshrestha on 2/6/18.
 */

/*

public class WebAppInterface extends SensorFragment {

    public WebAppInterface() {
        super(100,100, 1000.f, 45);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebView myWebView = (WebView) view.findViewById(R.id.my_web_view);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.loadUrl("http://192.168.0.3/metawear-ads/");


    }


    @Override
    protected void boardReady(){}

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter){}

    @Override
    @JavascriptInterface
    public void setup(){}

    @Override
    protected void clean(){}

    @Override
    protected String saveData(){ return null;}

    @Override
    protected void resetData(boolean clearData) {}





} //End of class WebAppInterface...

*/