package com.example.android.bluetoothapp;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.example.android.bluetoothapp.R.id.main_list_view;
import static com.example.android.bluetoothapp.R.id.submenu;

/**
 * Created by Mathilde on 03/05/2017.
 */

public class SensorAdapter extends ArrayAdapter {

    List<AccessPoint> accessPointList;
    AccessPointMainViewHolder accessPointViewHolder;
    AccessPoint accessPoint;

    SensorAdapter(Context context, List<AccessPoint> newAccessPointList) {
        super(context, R.layout.access_point_main_layout, newAccessPointList);
        this.accessPointList = newAccessPointList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("GETVIEW SENSORADAPTER", "call getView");
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.access_point_main_layout ,parent, false);
        }

        accessPointViewHolder = (AccessPointMainViewHolder) convertView.getTag();
        accessPoint = accessPointList.get(position);

        if(accessPointViewHolder == null){
            accessPointViewHolder = new AccessPointMainViewHolder();
            accessPointViewHolder.accessPointAddressMain = (TextView) convertView.findViewById(R.id.access_point_address_main);
            accessPointViewHolder.accessPointNameMain = (TextView) convertView.findViewById(R.id.access_point_name_main);
            accessPointViewHolder.sensorNbr = (TextView) convertView.findViewById(R.id.sensor_nbr);
            accessPointViewHolder.showSubMenu = (Button) convertView.findViewById(R.id.show_submenu);
            accessPointViewHolder.submenu = (LinearLayout) convertView.findViewById(R.id.submenu);
            convertView.setTag(accessPointViewHolder);
        }else{
            accessPointViewHolder = (AccessPointMainViewHolder) convertView.getTag();
        }

        accessPoint.initSensorsList();
        accessPointViewHolder.submenu.removeAllViewsInLayout();
        for ( Sensor sensor : accessPoint.sensorList ) {
            getSensorView(accessPointViewHolder.submenu, sensor);
        }

        //remplir la vue

        accessPointViewHolder.accessPointAddressMain.setText((String) accessPoint.getAddress());
        accessPointViewHolder.accessPointNameMain.setText(accessPoint.getName());
        accessPointViewHolder.sensorNbr.setText(Integer.toString(accessPoint.sensorList.size()));


        accessPointViewHolder.showSubMenu.setTag(this.accessPointList.get(position).sensorList);
        accessPointViewHolder.showSubMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("SUBMENU","button clicked");

                ArrayList<Sensor> sensors = (ArrayList<Sensor>) view.getTag();
                Button btn = (Button) view.findViewById(R.id.show_submenu);

                if (btn.getText().toString().equals("...")){
                    btn.setText("<-");
                    accessPointViewHolder.submenu.setVisibility(View.VISIBLE);
                } else if (btn.getText().toString().equals("<-")){
                    btn.setText("...");
                    accessPointViewHolder.submenu.setVisibility(View.GONE);
                } else {
                    Log.e("MODIFY LAYOUT", "Problem in changing btn: btn txt = " + btn.getText().toString());
                }
                notifyDataSetChanged();
            }



        });

        return convertView;
    }

    private void getSensorView(LinearLayout submenuLayout, Sensor sensor){
        View sensorView = LayoutInflater.from(getContext()).inflate(R.layout.sensor_layout, null);
        SensorViewHolder sensorViewHolder = new SensorViewHolder();
        sensorViewHolder.sensorAverageVal = (TextView) sensorView.findViewById(R.id.sensor_average);
        sensorViewHolder.sensorAddress = (TextView) sensorView.findViewById(R.id.sensor_address);
        sensorViewHolder.sensorName = (TextView) sensorView.findViewById(R.id.sensor_name);
        sensorViewHolder.sensorImage = (ImageView) sensorView.findViewById(R.id.sensor_image);

        sensorViewHolder.sensorAverageVal.setText( String.valueOf(sensor.getValue()));
        sensorViewHolder.sensorAddress.setText(String.valueOf(sensor.getAddress()));
        sensorViewHolder.sensorName.setText(sensor.getName());
        sensorViewHolder.sensorImage.setImageDrawable(new ColorDrawable(sensor.getColor()));

        sensorView.setTag(sensorViewHolder);
        submenuLayout.addView(sensorView);
    }



}
