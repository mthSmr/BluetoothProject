package com.example.android.bluetoothapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.R.interpolator.linear;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Mathilde on 21/04/2017.
 */

public class Sensor implements Parcelable {

    private String name;
    private boolean connected;
    public float value;
    private int address;
    @ColorInt private int color;


    Sensor(){
        name = "default";
        connected = false;
        address = -1;
        color = 0xff000000;
        value = 0;
    }

    Sensor(int newAddress){
        address = newAddress;
    }


    /********************
     *  getters/setters *
     ********************/

    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(int newColor)  { this.color = newColor; }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setValue(float newVal){
        this.value = newVal;
    }


    public String getName() {
        return name;
    }

    public boolean isConnected(){
        return connected;
    }

    public float getValue(){
        return value;
    }

    public int getAddress(){ return address; }

    public int getColor(){ return color; }



    /***************
     *  Functions  *
     ***************/

    public float calculateAverageValue(int delay){
        int count = 0;
        float sumValues = 0;
        long time= System.currentTimeMillis();

        while (System.currentTimeMillis() < (time + delay) ) {
            sumValues += value;
            count++;
        }
        return sumValues/count;
    }

    public LinearLayout generateGeneralView(Context context){

        LinearLayout generalLayout = new LinearLayout(context);
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.setMargins(0,2,0,2);
        generalLayout.setOrientation(LinearLayout.HORIZONTAL);
        generalLayout.setPadding(12,12,12,12);
        generalLayout.setLayoutParams(generalLayoutParams);
        generalLayout.setBackgroundColor(Color.parseColor("#e0e0e0"));

            TextView sensorNameView = new TextView(context);
            sensorNameView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4));
            sensorNameView.setText(name + ":");
            sensorNameView.setTypeface(null, Typeface.BOLD);
            sensorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            TextView valueView = new TextView(context);
            valueView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            valueView.setText("" + value);
            valueView.setTypeface(null, Typeface.BOLD);
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        generalLayout.addView(sensorNameView);
        generalLayout.addView(valueView);

        return generalLayout;

    }

    public void displayDetails(Context context){

        LinearLayout detailsLayout = new LinearLayout(context);
        LinearLayout.LayoutParams detailsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        detailsLayout.setVisibility(LinearLayout.INVISIBLE);

            TextView addressView = new TextView(context);
            addressView.setText("Address: " + address);
            addressView.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView instantValueView = new TextView(context);
            instantValueView.setText("Instant value: " + value);
    }


    /******************
     *   Parcelable   *
     ******************/

    public void writeToParcel(Parcel parcelOut, int flags){
        parcelOut.writeString(name);

        boolean[] connectedToArray = new boolean[1];
        connectedToArray[0] = connected;
        parcelOut.writeBooleanArray(connectedToArray);

        parcelOut.writeFloat(value);

        parcelOut.writeInt(address);

        parcelOut.writeInt(color);
    }


    Sensor(Parcel parcelIn){
        name = parcelIn.readString();

        boolean[] temp = new boolean[1];
        parcelIn.readBooleanArray(temp);
        connected = temp[0];

        value = parcelIn.readFloat();

        address = parcelIn.readInt();

        color = parcelIn.readInt();
    }


    public int describeContents(){
        return 0;
    }


    public static final Parcelable.Creator<Sensor> CREATOR = new Parcelable.Creator<Sensor>(){
        @Override
        public Sensor createFromParcel(Parcel parcelIn){
            return new Sensor(parcelIn);
        }

        @Override
        public Sensor[] newArray(int size){
            return new Sensor[size];
        }
    };

}
