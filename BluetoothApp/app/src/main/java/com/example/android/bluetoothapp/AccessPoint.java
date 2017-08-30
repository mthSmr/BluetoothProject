package com.example.android.bluetoothapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import static android.R.attr.id;
import static android.R.id.message;
import static android.R.string.cancel;
import static com.example.android.bluetoothapp.BluetoothConnectionActivity.bluetoothAdapter;
import static com.example.android.bluetoothapp.BluetoothConnectionActivity.uuid;

/**
 * Created by Mathilde on 10/05/2017.
 */

public class AccessPoint implements Parcelable{

    private BluetoothSocket socket;
    public BluetoothDevice bluetoothDevice;
    public ArrayList<Sensor> sensorList = new ArrayList<>();

    private boolean connected; //TODO: handle connection selection
    private final ParcelUuid uuids[];
    //private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    AccessPoint(BluetoothDevice btDevice) {
        this.bluetoothDevice = btDevice;
        this.uuids = bluetoothDevice.getUuids();
    }


    public String getName() {
        return this.bluetoothDevice.getName();
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }


    /********************
     *    Parcelable    *
     ********************/

    public void writeToParcel(Parcel parcelOut, int flags){
        parcelOut.writeParcelable(bluetoothDevice,flags);
    }


    private AccessPoint(Parcel parcelIn){
        bluetoothDevice = parcelIn.readParcelable(BluetoothDevice.class.getClassLoader());
        this.uuids = bluetoothDevice.getUuids();
    }


    public int describeContents(){
        return 0;
    }


    public static final Parcelable.Creator<AccessPoint> CREATOR = new Parcelable.Creator<AccessPoint>(){
        @Override
        public AccessPoint createFromParcel(Parcel parcelIn){
            return new AccessPoint(parcelIn);
        }

        @Override
        public AccessPoint[] newArray(int size){
            return new AccessPoint[size];
        }
    };



    /********************
     * Bluetooth network*
     ********************/

    public void connectDevice(){
        //https://looksok.wordpress.com/tag/listview-item-with-button/
        socket = null;
        //Log.i("CONNECT TO", getName() + " " + getAddress());
        pairDevice();

        try {
            createSocket();
            try {
                socket.connect();
                connected = true;
                Log.e("CONNECTION", "socket done");
            } catch (IOException e) {
                Log.e("CONNECTION", "socket error: " + e.toString());
                disconnectDevice();
            }
        }catch(Exception e){
            Log.e("CONNECTION","socket creation failed:" + e.toString());
        }

    }


    public void disconnectDevice(){
        if (connected) {
            try {
                socket.close();
                Log.e("DISCONNECTION", "socket closed");
            } catch (IOException closeExeption) {
                Log.e("DISCONNECTION", "close connection failed");
            }
        }
    }


    public void pairDevice() {
        try {
            Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void unpairDevice() {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...");
            Method m = bluetoothDevice.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(bluetoothDevice, (Object[]) null);
            Log.d("unpairDevice()", "Un-Pairing finished.");
        } catch (Exception e) {
            Log.e("unPair Exection", e.getMessage());
        }
    }


    private void createSocket (){
        //create thread for accessPoint
        BluetoothSocket temp = null;
        try{
            Log.e("CONNECTION", "uuid = " + uuids[0].getUuid().toString());
            temp = bluetoothDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
        } catch (IOException e){
            Log.e("CONNECTION", "socket initialization failed");
        }
        socket = temp;
    }


    public void initSensorsList(){
        while (!connected) {
            connectDevice();
        }
            pairDevice();
            BluetoothCommunication btCom = new BluetoothCommunication(socket);
            sensorList = btCom.queryContent();
            logSensors();
            //btCom.endCommunication();
    }


    private void logUUIDS(){
        for (int id=0; id<uuids.length; id++){
            Log.e("UUIDS", "uuid " + id + " = " + this.uuids[id].getUuid().toString());
        }
    }

    private void logSensors(){
        for (Sensor sensor : this.sensorList) {
            Log.e("ACCESS POINT","sensor:" + sensor.getAddress());
        }
    }

};