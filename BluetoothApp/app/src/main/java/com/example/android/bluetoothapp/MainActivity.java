package com.example.android.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.app.AlertDialog;

import static android.R.attr.enabled;
import static android.R.id.message;

/************* description **************
 *  -  Bouton pour accéder à la gestion du bluetooth
 *  -  Liste des capteurs - nom + données moyenne sur un temps (5 minutes?)
 *  -  Click sur capteur --> détails du capteur (nom, adresse, état, valeur instant...)
 */



public class MainActivity extends AppCompatActivity {

    /*** Bluetooth ***/
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private static BluetoothAdapter bluetoothAdapter;

    /*** Sensor ***/
    ArrayList<AccessPoint> sensorList = new ArrayList<>();
    ListView sensorListView;
    SensorAdapter sensorAdapter;

    //


    /******************
     *  architecture  *
     ******************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // Bluetooth init

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            handleIncompatibleBT();
        } else{
            startBluetooth();
        }

    // sensor tools initialisation
        sensorListView = (ListView) findViewById(R.id.main_list_view);   //Get the listView list_view from activity_main.xml file

        sensorAdapter =  new SensorAdapter(MainActivity.this, sensorList);
        sensorAdapter.setNotifyOnChange(true);
        sensorListView.setAdapter(sensorAdapter);

    // init the sensor list
        //sensorList.add(new AccessPoint( (BluetoothDevice) bluetoothAdapter.getBondedDevices().toArray()[0]));
        //initSensorList(sensorList.get(0).sensorList);
    }


    protected void onStart(){
        super.onStart();
    }


    /***************
     *  Functions  *
     ***************/

    public void initSensorList(ArrayList<Sensor> sensorList){

        Sensor testSensor = new Sensor(0);
        testSensor.setName("Hon Sensor");  //je sais c'est vraiment nul comme blague -_-'
        testSensor.value = 753;
        testSensor.setColor(0xffff3d00);
        sensorList.add(testSensor);

        Sensor testSsr = new Sensor(1);
        testSsr.setName("Yo man!");
        testSsr.value = 12;
        sensorList.add(testSsr);
    }


    public void updateSensorList(ArrayList<AccessPoint> updatedAccessPoint){
        sensorAdapter.clear();
        if (updatedAccessPoint != null){
            for (AccessPoint accessPoint: updatedAccessPoint) {
                sensorAdapter.insert(accessPoint, sensorAdapter.getCount());
            }
        }
        sensorAdapter.notifyDataSetChanged();
    }


    /**************
     *  Bluetooth *
     **************/

    //open bluetooth if possible and allowed by user
    private boolean startBluetooth(){
        if (!this.bluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
            return true;
        } else {
            return false;
        }

    }


    //ask permission to user to turn on bluetooth
    private void turnOnBluetooth(){
        Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
    }


    private void handleIncompatibleBT() {
        AlertDialog incompatibleBTWindow = new AlertDialog.Builder(this)
                .setTitle("Not compatible")
                .setMessage("Your phone does not support Bluetooth")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void callBluetoothActivity(View view){
        // call BluetoothConnection Activity
        Intent intent = new Intent(MainActivity.this, BluetoothConnectionActivity.class); //TODO: use addExtra(String, Parceable pour envoyer le btAdapter)
        startActivity(intent);

    }


    @Override
    protected void onNewIntent(Intent intent){
        Log.i("MAIN", "onNewIntent method called");
        super.onNewIntent(intent);
        if(intent.getStringExtra("receiveAccessPoint").equals("receiveSelection")){
            receiveSelection(intent);
        }
    }


    public void receiveSelection(Intent intent){
        sensorList = (ArrayList) intent.getParcelableArrayListExtra("List");
        Log.i("RECEIVESELECTION", "" + sensorList.size());
        updateSensorList(sensorList);
    }




    /********************
     *  getters/setters *
     ********************/



}
