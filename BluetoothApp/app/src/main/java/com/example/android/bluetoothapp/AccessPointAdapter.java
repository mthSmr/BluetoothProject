package com.example.android.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static android.R.attr.button;
import static android.os.Build.VERSION_CODES.M;
import static android.os.FileObserver.MODIFY;

/**
 * Created by Mathilde on 09/05/2017.
 */

public class AccessPointAdapter extends ArrayAdapter {

    List<AccessPoint> accessPointList;

    AccessPointAdapter(Context context, List<AccessPoint> newAccessPointList) {
        super(context, R.layout.access_point_layout, newAccessPointList);
        this.accessPointList = newAccessPointList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.access_point_layout ,parent, false);
        }

        AccessPointViewHolder viewHolder = (AccessPointViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new AccessPointViewHolder();
            viewHolder.accessPointAddress = (TextView) convertView.findViewById(R.id.access_point_address);
            viewHolder.accessPointName = (TextView) convertView.findViewById(R.id.access_point_name);
            viewHolder.addAccessPointBtn = (Button) convertView.findViewById(R.id.add_access_point);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (AccessPointViewHolder) convertView.getTag();
        }

        viewHolder.addAccessPointBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AccessPoint accessPoint = (AccessPoint) view.getTag();
                //accessPoint.connectDevice();
                //accessPoint.pairDevice();

                Button addBtn = (Button) view.findViewById(R.id.add_access_point);
                //RelativeLayout apView  = (RelativeLayout) view.findViewById(R.id.access_point_layout);
                RelativeLayout parentView = (RelativeLayout) view.getParent();

                if (addBtn.getText().toString().equals("+")){
                    addBtn.setText("-");
                    selectAccessPoint(accessPoint);
                    highLightAccessPoint(parentView);
                } else if (addBtn.getText().toString().equals("-")){
                    addBtn.setText("+");
                    removeAccessPoint(accessPoint);
                    nrmlClrAccessPoint(parentView);
                } else {
                    Log.e("MODIFY LAYOUT", "Problem in changing btn: btn txt = " + addBtn.getText().toString());
                }

                Button selectBtn = (Button) view.getRootView().findViewById(R.id.display);

                if(selectBtn.getVisibility()== View.GONE){
                    selectBtn.setVisibility(View.VISIBLE);
                }
                if(BluetoothConnectionActivity.selection.isEmpty()){
                    selectBtn.setVisibility(View.GONE);
                }

            }
        });
        viewHolder.addAccessPointBtn.setTag(this.accessPointList.get(position));

        //getItem(position) va récupérer l'item [position] de la List<Sensor> sensorList
        AccessPoint accessPoint = accessPointList.get(position);
        //remplir la vue
        viewHolder.accessPointAddress.setText(accessPoint.getAddress());
        viewHolder.accessPointName.setText(accessPoint.getName());
        return convertView;
    }


    private void selectAccessPoint(AccessPoint accessPoint){
        if (!BluetoothConnectionActivity.selection.contains(accessPoint)) {
            BluetoothConnectionActivity.selection.add(accessPoint);
        }else{
            Log.e("MODIFY LAYOUT", "Access point alredy added");
        }
    }

    private void removeAccessPoint(AccessPoint accessPoint){
        if (BluetoothConnectionActivity.selection.contains(accessPoint)) {
            BluetoothConnectionActivity.selection.remove(accessPoint);
        }else{
            Log.e("MODIFY LAYOUT", "Access point alredy removed");
        }
    }

    private void highLightAccessPoint(RelativeLayout layout){
        layout.setBackgroundColor(0xffffff);
        layout.invalidate();
    }

    private void nrmlClrAccessPoint(RelativeLayout layout){
        layout.setBackgroundColor(0xe0e0e0);
        layout.invalidate();
    }

}
