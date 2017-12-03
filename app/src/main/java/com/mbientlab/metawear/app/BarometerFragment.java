/*
 * Copyright 2015 MbientLab Inc. All rights reserved.
 *
 * IMPORTANT: Your use of this Software is limited to those specific rights
 * granted under the terms of a software license agreement between the user who
 * downloaded the software, his/her employer (which must be your employer) and
 * MbientLab Inc, (the "License").  You may not use this Software unless you
 * agree to abide by the terms of the License which can be found at
 * www.mbientlab.com/terms . The License limits your use, and you acknowledge,
 * that the  Software may not be modified, copied or distributed and can be used
 * solely and exclusively in conjunction with a MbientLab Inc, product.  Other
 * than for the foregoing purpose, you may not use, reproduce, copy, prepare
 * derivative works of, modify, distribute, perform, display or sell this
 * Software and/or its documentation for any purpose.
 *
 * YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
 * PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
 * MBIENTLAB OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT, NEGLIGENCE,
 * STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE
 * THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED
 * TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST
 * PROFITS OR LOST DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY,
 * SERVICES, OR ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY
 * DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 *
 * Should you have any questions regarding your right to use this Software,
 * contact MbientLab Inc, at www.mbientlab.com.
 */

package com.mbientlab.metawear.app;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.app.help.HelpOptionAdapter;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.BarometerBosch.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by etsai on 8/22/2015.
 */
public class BarometerFragment extends SensorFragment {
    private static final float BAROMETER_SAMPLE_FREQ = 26.32f, LIGHT_SAMPLE_PERIOD= 1 / BAROMETER_SAMPLE_FREQ;

    private BarometerBosch barometer;
    private float altitudeMin, altitudeMax;

    private Route altitudeRoute = null;
    private final ArrayList<Entry> altitudeData= new ArrayList<>(), pressureData= new ArrayList<>();

    public BarometerFragment() {
        super(R.string.navigation_fragment_barometer, R.layout.fragment_sensor, 80000, 110000);
        altitudeMin= -300;
        altitudeMax= 1500;
        postRequesttoServer("Hello");
    }

    //okHttp post code starts from here
    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/vnd.api+json");

    public void postRequesttoServer(String str){

        //okHttp requests here
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject postdata = new JSONObject();
       // JSONObject data = new JSONObject();
        try {
            //data.put("eventtype",str);
            postdata.put("time", "Hello" );
            postdata.put("celsius", "Hello" );
        } catch(JSONException e){
            // TODO Auto-generated catch block
            System.out.println("1st exception");
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());

        Log.i(TAG, String.valueOf(body));
        //initialize requests here
        Request request = new Request.Builder()
                .url("http://192.168.0.3:8000/api/temperature")
                .post(body)
                .build();

        //execute requests here
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //String mMessage = e.getMessage().toString();
                Log.i(TAG, e.getMessage());
                System.out.println("Second Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                Log.i(TAG, "The response body =" + mMessage);
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        final String serverResponse = json.getString("Your Index");

                    } catch (Exception e){
                        System.out.println("Third exception: Failed JSON Object");
                        Log.i(TAG, e.getMessage());
                    }
                } else {
                    System.out.println("response failed");
                    Log.i(TAG, response.body().string());
                }

            }


        });
    }
    //okHttp post code ends here
    @Override
    protected void boardReady() throws UnsupportedModuleException {
        barometer = mwBoard.getModuleOrThrow(BarometerBosch.class);
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter) {

    }

    @Override
    protected void setup() {
        barometer.configure()
                .pressureOversampling(OversamplingMode.ULTRA_HIGH)
                .filterCoeff(FilterCoeff.OFF)
                .standbyTime(0.5f)
                .commit();

        barometer.pressure().addRouteAsync(source -> source.stream((data, env) -> {
            LineData chartData = chart.getData();
            if (pressureData.size() >= sampleCount) {
                chartData.addXValue(String.format(Locale.US, "%.2f", sampleCount * LIGHT_SAMPLE_PERIOD));
                sampleCount++;

                updateChart();
            }
            chartData.addEntry(new Entry(data.value(Float.class), sampleCount), 0);
        })).continueWithTask(task -> {
            streamRoute = task.getResult();

            return barometer.altitude().addRouteAsync(source -> source.stream((data, env) -> {
                LineData chartData = chart.getData();
                if (altitudeData.size() >= sampleCount) {
                    chartData.addXValue(String.format(Locale.US, "%.2f", sampleCount * LIGHT_SAMPLE_PERIOD));
                    sampleCount++;

                    updateChart();
                }
                chartData.addEntry(new Entry(data.value(Float.class), sampleCount), 1);
            }));
        }).continueWith(task -> {
            altitudeRoute = task.getResult();

            barometer.altitude().start();
            barometer.pressure().start();
            barometer.start();
            return null;
        });
    }

    @Override
    protected void initializeChart() {
        ///< configure axis settings
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMaxValue(max);
        leftAxis.setAxisMinValue(min);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setStartAtZero(false);
        rightAxis.setAxisMaxValue(altitudeMax);
        rightAxis.setAxisMinValue(altitudeMin);
    }

    @Override
    protected void clean() {
        barometer.stop();
        barometer.altitude().stop();
        barometer.pressure().stop();

        if (altitudeRoute != null) {
            altitudeRoute.remove();
            altitudeRoute = null;
        }
    }

    @Override
    protected String saveData() {
        final String CSV_HEADER = String.format("time,pressure,altitude%n");
        String filename = String.format(Locale.US, "%s_%tY%<tm%<td-%<tH%<tM%<tS%<tL.csv", getContext().getString(sensorResId), Calendar.getInstance());

        try {
            FileOutputStream fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(CSV_HEADER.getBytes());

            LineData data = chart.getLineData();
            LineDataSet pressureDataSet = data.getDataSetByIndex(0), altitudeDataSet = data.getDataSetByIndex(1);
            for (int i = 0; i < data.getXValCount(); i++) {
                fos.write(String.format(Locale.US, "%.3f,%.3f,%.3f%n", i * LIGHT_SAMPLE_PERIOD,
                        pressureDataSet.getEntryForXIndex(i).getVal(),
                        altitudeDataSet.getEntryForXIndex(i).getVal()).getBytes());
            }
            fos.close();
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void resetData(boolean clearData) {
        if (clearData) {
            sampleCount = 0;
            chartXValues.clear();
            altitudeData.clear();
            pressureData.clear();
        }

        ArrayList<LineDataSet> spinAxisData= new ArrayList<>();
        spinAxisData.add(new LineDataSet(pressureData, "pressure"));
        spinAxisData.get(0).setAxisDependency(YAxis.AxisDependency.LEFT);
        spinAxisData.get(0).setColor(Color.RED);
        spinAxisData.get(0).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(altitudeData, "altitude"));
        spinAxisData.get(1).setAxisDependency(YAxis.AxisDependency.RIGHT);
        spinAxisData.get(1).setColor(Color.GREEN);
        spinAxisData.get(1).setDrawCircles(false);

        LineData data= new LineData(chartXValues);
        for(LineDataSet set: spinAxisData) {
            data.addDataSet(set);
        }
        data.setDrawValues(false);
        chart.setData(data);
    }
}
