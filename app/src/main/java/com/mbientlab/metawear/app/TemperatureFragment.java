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

import android.app.DownloadManager;
import android.nfc.Tag;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by etsai on 8/19/2015.
 */
public class TemperatureFragment extends SingleDataSensorFragment {
    private static final int TEMP_SAMPLE_PERIOD= 33, SINGLE_EXT_THERM_INDEX= 1;

    private byte gpioDataPin= 0, gpioPulldownPin= 1;
    private boolean activeHigh= false;

    private long startTime= -1;
    private Temperature tempModule;
    private Timer timerModule;
    private Timer.ScheduledTask scheduledTask;
    private List<String> spinnerEntries= null;
    private int selectedSourceIndex= 0;

    private Spinner sourceSelector;


    public TemperatureFragment() {
        super(R.string.navigation_fragment_temperature, "celsius", R.layout.fragment_temperature, TEMP_SAMPLE_PERIOD / 1000.f, 15, 45);
    }




    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sourceSelector= (Spinner) view.findViewById(R.id.temperature_source);
        System.out.println("SourceSelector = "+sourceSelector);
        sourceSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View innerView, int position, long id) {
                if (tempModule.sensors()[position].type() == SensorType.BOSCH_ENV) {
                    try {
                        mwBoard.getModuleOrThrow(BarometerBosch.class).start();
                    } catch (UnsupportedModuleException e) {
                        view.findViewById(R.id.sample_control).setEnabled(false);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.title_error);
                        builder.setMessage(R.string.message_no_bosch_barometer);
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                    }
                } else {
                    try {
                        mwBoard.getModuleOrThrow(BarometerBosch.class).stop();
                    } catch (UnsupportedModuleException e) {
                        e.printStackTrace();
                    }
                    view.findViewById(R.id.sample_control).setEnabled(true);
                }

                int[] extThermResIds = new int[]{R.id.ext_thermistor_data_pin_wrapper, R.id.ext_thermistor_pulldown_pin_wrapper,
                        R.id.ext_thermistor_active_setting_title, R.id.ext_thermistor_active_setting
                };
                for (int resId : extThermResIds) {
                    view.findViewById(resId).setVisibility(tempModule.sensors()[position].type() == SensorType.EXT_THERMISTOR ? VISIBLE : GONE);
                }

                selectedSourceIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (spinnerEntries != null) {
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerEntries);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sourceSelector.setAdapter(spinnerAdapter);
            sourceSelector.setSelection(selectedSourceIndex);
            System.out.println("SpinnerEntries ="+spinnerAdapter);
            System.out.println("from line 151");
        }

        final EditText extThermPinText= (EditText) view.findViewById(R.id.ext_thermistor_data_pin);
        extThermPinText.setText(String.format(Locale.US, "%d", gpioDataPin));
        System.out.println("ThermPin 1 ="+extThermPinText);
        extThermPinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextInputLayout extThermWrapper = (TextInputLayout) view.findViewById(R.id.ext_thermistor_data_pin_wrapper);
                try {
                    gpioDataPin = Byte.valueOf(s.toString());
                    view.findViewById(R.id.sample_control).setEnabled(true);
                    extThermWrapper.setError(null);


                } catch (Exception e) {
                    view.findViewById(R.id.sample_control).setEnabled(false);
                    extThermWrapper.setError(e.getLocalizedMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final EditText pulldownPinText= (EditText) view.findViewById(R.id.ext_thermistor_pulldown_pin);
        pulldownPinText.setText(String.format(Locale.US, "%d", gpioPulldownPin));
        pulldownPinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextInputLayout extThermWrapper = (TextInputLayout) view.findViewById(R.id.ext_thermistor_pulldown_pin_wrapper);

                try {
                    gpioPulldownPin = Byte.valueOf(s.toString());
                    view.findViewById(R.id.sample_control).setEnabled(true);
                    extThermWrapper.setError(null);

                } catch (Exception e) {
                    view.findViewById(R.id.sample_control).setEnabled(false);
                    extThermWrapper.setError(e.getLocalizedMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final Spinner activeSelections= (Spinner) view.findViewById(R.id.ext_thermistor_active_setting);
        activeSelections.setSelection(activeHigh ? 1 : 0);
        activeSelections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeHigh = (position != 0);
                System.out.println("from line 215");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException {
        timerModule= mwBoard.getModuleOrThrow(Timer.class);
        tempModule= mwBoard.getModuleOrThrow(Temperature.class);
        String totalData = timerModule + "\n" + tempModule;
        System.out.println("timerModule from boardReady() ="+timerModule);
        System.out.println("tempModule from boardReady() ="+tempModule);

        spinnerEntries = new ArrayList<>();
        for (Temperature.Sensor it: tempModule.sensors()) {
            spinnerEntries.add(it.type().toString()
            );
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerEntries);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSelector.setAdapter(spinnerAdapter);
        sourceSelector.setSelection(selectedSourceIndex);
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter) {
        adapter.add(new HelpOption(R.string.config_name_temp_source, R.string.config_desc_temp_source));
        adapter.add(new HelpOption(R.string.config_name_temp_active, R.string.config_desc_temp_active));
        adapter.add(new HelpOption(R.string.config_name_temp_data_pin, R.string.config_desc_temp_data_pin));
        adapter.add(new HelpOption(R.string.config_name_temp_pulldown_pin, R.string.config_desc_temp_pulldown_pin));
    }

    @Override
    protected void setup() {
        Temperature.Sensor tempSensor = tempModule.sensors()[selectedSourceIndex];
        System.out.println("setup tempSensor ="+tempSensor);

        if (tempSensor.type() == SensorType.EXT_THERMISTOR) {
            ((Temperature.ExternalThermistor) tempModule.sensors()[selectedSourceIndex]).configure(gpioDataPin, gpioPulldownPin, activeHigh);
        }
        tempSensor.addRouteAsync(source -> source.stream((data, env) -> {
            final Float celsius = data.value(Float.class);
            //System.out.println("output #celsius = "+ celsius);


           //Calling AppHook to post  Temperature data to WebApp
            String strUrl = "http://192.168.0.4:8000/api/temperature";
            AppHook posttoWebapp = new AppHook();
            posttoWebapp.postSingleData(strUrl,"celsius", celsius.toString());


            //Calling AppHook to post in SecuWear
            String reqUrl = "http://192.168.0.4:4000/api/events";
            Long systemTime = System.currentTimeMillis();

            AppHook secuwear = new AppHook();
            secuwear.posttoSecuWear(reqUrl, systemTime,"Temperature handler executed", "app/src/main/java/com/mbientlab/metawear/app/TemperatureFragment.java","line 266");


            posttoWebapp = null;
            secuwear = null;


            LineData chartData = chart.getData();
            if (startTime == -1) {

                chartData.addXValue("0");
                startTime = System.currentTimeMillis();


            } else {
                chartData.addXValue(String.format(Locale.US, "%.2f", sampleCount * samplingPeriod));
            }
            chartData.addEntry(new Entry(celsius, sampleCount), 0);

            sampleCount++;

            updateChart();
        })).continueWithTask(task -> {
            streamRoute = task.getResult();
            return timerModule.scheduleAsync(TEMP_SAMPLE_PERIOD, false, tempSensor::read);
        }).continueWithTask(task -> {
            scheduledTask = task.getResult();
            scheduledTask.start();
            System.out.println("setup scheduledTask ="+ scheduledTask);
            return null;
        });

    }

    @Override
    protected void clean() {
        scheduledTask.remove();
    }

    @Override
    protected void resetData(boolean clearData) {
        super.resetData(clearData);

        if (clearData) {
            startTime= -1;
        }
    }


}