package com.example.android.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class BluetoothReceiver extends BroadcastReceiver {

    private List<AccessPoint> foundDevices = new ArrayList<>();    //buffer



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice from the Intent
            AccessPoint accessPoint = new AccessPoint((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));// Create a new device item
            Log.i("INFO", "found device = " + accessPoint.getName() );
            // Add it to our adapter
            BluetoothConnectionActivity.updateAccessPointList(BluetoothConnectionActivity.newAccessPointAdapter,accessPoint);
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            Log.i("INFO","Start scanning network");
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.i("INFO","Stop scanning network");
        }
    }

}
