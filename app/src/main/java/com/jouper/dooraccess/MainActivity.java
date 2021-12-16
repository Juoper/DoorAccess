package com.jouper.dooraccess;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static MqttAndroidClient client;
    private static MqttConnectOptions mqttConnectOptions;
    private final MemoryPersistence persistence = new MemoryPersistence();

    Button openDoorBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDoorBtn = findViewById(R.id.openDoorBtn);
        openDoorBtn.setOnClickListener(this);


        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        client = new MqttAndroidClient(getApplicationContext(), "tcp://mainframe:1883","popelAndroid", persistence);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        System.out.println("recieved click");
        switch (view.getId()) {
            case R.id.openDoorBtn:

                sendMQTTCommand(getApplicationContext());

                break;

        }

    }


    public void launchSecondActivity(View view){
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }


    public static void sendMQTTCommand(Context applicationContext ){
        System.out.println("try mqtt");
        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override

                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");

                    Toast toast = Toast.makeText(applicationContext, "Opening the door", Toast.LENGTH_SHORT);
                    toast.show();

                    MqttMessage message = new MqttMessage();
                    message.setPayload("true".getBytes());
                    if (client.isConnected()) {
                        try {
                            client.publish("door/open", message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast toastError = Toast.makeText(applicationContext, "No Connection to mqtt", Toast.LENGTH_SHORT);
                        toastError.show();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast toast = Toast.makeText(applicationContext, "No Connection to mqtt", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}