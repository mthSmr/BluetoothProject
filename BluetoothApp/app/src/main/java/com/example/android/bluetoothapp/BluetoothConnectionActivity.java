package com.example.android.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.R.attr.handle;
import static android.R.id.message;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static java.util.Collections.addAll;
import static java.util.Collections.newSetFromMap;

public class BluetoothConnectionActivity extends AppCompatActivity {

    //view
    /*
    private LinearLayout btScanView = (LinearLayout) findViewById(R.id.bt_scan_view);
    private ProgressBar scanProgressBar = (ProgressBar) btScanView.findViewById(R.id.bt_scan_progress);
    private Button startScanBtn = (Button) btScanView.findViewById(R.id.scan_button);
    */

    //events
    private static final String EMPTY = "EMPTY";
    private static final String BT_DISCOVERY_FINISHED = "BT_DISCOVERY_FINISHED";
    private static final String BT_DISCOVERY_PROCESSING = "BT_DISCOVERY_PROCESSING";

    //Bluetooth tools
    public static final UUID uuid = UUID.randomUUID();
    public static BluetoothAdapter bluetoothAdapter;
    private static BluetoothReceiver btReceiver = new BluetoothReceiver();
    private IntentFilter filter = new IntentFilter();

    //Access points
    private List<AccessPoint> pairedAccessPointList = new ArrayList<>();
    private static List<AccessPoint> newAccessPointList = new ArrayList<AccessPoint>();
    public static ArrayList<AccessPoint> selection = new ArrayList<>();

    //ListView
    private ListView unknownDeviceListView;
    public ListView pairedDeviceListView;
    public static AccessPointAdapter newAccessPointAdapter;
    public AccessPointAdapter pairedAccessPointAdapter;



    /******************
     *  architecture  *
     ******************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        initBluetoothReceiver();
        initUnknownDeviceListView();
        initPairedDeviceListView();
        initNewAccessPointAdapter();
        initPairedAccessPointAdapter();
        setPairedDevices(bluetoothAdapter.getBondedDevices());
    }


    @Override
    protected void onStart(){
        super.onStart();
        if (newAccessPointList.size()>0){
            updateAccessPointList(newAccessPointAdapter,newAccessPointList);
        }
        if (newAccessPointList.size()>0){
            updateAccessPointList(pairedAccessPointAdapter,pairedAccessPointList);
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(btReceiver);
    }



    /***************
     *  Functions  *
     ***************/

    private void initBluetoothReceiver(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btReceiver, filter);//init intentfilters
    }


    private void initUnknownDeviceListView(){
        unknownDeviceListView = (ListView) findViewById(R.id.bt_list_view);   //Get the listView list_view from activity_main.xml file
        /*unknownDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccessPoint itemList = (AccessPoint)unknownDeviceListView.getItemAtPosition(position);
                addAll(pairedAccessPointList
               ,itemList);
            }
        });*/
    }


    private void initPairedDeviceListView(){
        pairedDeviceListView = (ListView) findViewById(R.id.paired_list_view);   //Get the listView list_view from activity_main.xml file
        /*pairedDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccessPoint itemList = (AccessPoint)pairedDeviceListView.getItemAtPosition(position);
                addAll(pairedAccessPointList
               ,itemList);
            }
        });*/
    }


    private void initNewAccessPointAdapter(){
        newAccessPointAdapter =  new AccessPointAdapter(BluetoothConnectionActivity.this, newAccessPointList);
        newAccessPointAdapter.setNotifyOnChange(true);
        unknownDeviceListView.setAdapter(newAccessPointAdapter);
    }


    private void initPairedAccessPointAdapter(){
        pairedAccessPointAdapter = new AccessPointAdapter(BluetoothConnectionActivity.this, pairedAccessPointList);
        pairedAccessPointAdapter.setNotifyOnChange(true);
        pairedDeviceListView.setAdapter(pairedAccessPointAdapter);
    }

    /*
    public List<AccessPoint> testListView(){
        List<AccessPoint> testList = new ArrayList<>();
        AccessPoint test = new AccessPoint();
        test.setName("test");
        test.setAddress("random address");
        testList.add(test);
        return testList;
    }*/



    /***************
     *  Functions  *
     ***************/

    //start Bluetooth device discovery
    public void doDiscovery(View view) {
        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        //reset previous device list
        newAccessPointAdapter.clear();
        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    /*
    public void activateProgressBar(){
        //show progress bar instead of the scan button
        startScanBtn.setVisibility(View.GONE);
        scanProgressBar.setVisibility(View.VISIBLE);
    }


    public void disactivateProgressBar(){
        //show progress bar instead of the scan button
        scanProgressBar.setVisibility(View.GONE);
        startScanBtn.setVisibility(View.VISIBLE);
    }*/


    //update listview content by adding 1 or several AccessPoints
    public static void updateAccessPointList(AccessPointAdapter toUpdate, List<AccessPoint> updatedAccessPoints){
        if (updatedAccessPoints != null){
            for (AccessPoint accessPoint : updatedAccessPoints) {
                //if (!findInAccessPoints(accessPoint)) {
                    toUpdate.insert(accessPoint, toUpdate.getCount());
                //}
            }
        }
        toUpdate.notifyDataSetChanged();
    }


    //update listview content by adding 1 or several AccessPoints
    public static void updateAccessPointList(AccessPointAdapter toUpdate, AccessPoint updatedAccessPoint){
        if (updatedAccessPoint != null){
            //if (!findInAccessPoints( updatedAccessPoint)){
                toUpdate.insert(updatedAccessPoint,toUpdate.getCount());
            //}
        }
        toUpdate.notifyDataSetChanged();
    }


    //look for an already existing AccessPoint
    private static boolean findInAccessPoints(AccessPoint newAccessPoint){
        boolean result = false;
        for (AccessPoint foundAccessPoint: newAccessPointList
             ) {
            if (foundAccessPoint.getAddress() == newAccessPoint.getAddress()){
                result = true;
            }
        }
        return result;
    }

    public void logList(List<AccessPoint> mList){
        Log.i("List",":");
        for (AccessPoint accessPoint: mList){
            Log.i("item", mList.indexOf(accessPoint) + ":" + accessPoint.getName() + " " + accessPoint.getAddress());
        }
    }


    public void displaySelection(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putParcelableArrayListExtra("List",selection);
        intent.putExtra("receiveAccessPoint", "receiveSelection");
        startActivity(intent);
        finish();
    }



    /*****************************
     *  Bluetooth communication  *
     *****************************/



    //TODO: Connecter un point BT & aux pairedBTDevices
    //TODO: Loading bar thing for the scanning network time


    /********************
     *  getters/setters *
     ********************/

    public void setPairedDevices(Set<BluetoothDevice> bluetoothdevices){
        for (BluetoothDevice bluetoothDevice:bluetoothdevices) {
            updateAccessPointList(pairedAccessPointAdapter,new AccessPoint(bluetoothDevice));
        }
    }


}
