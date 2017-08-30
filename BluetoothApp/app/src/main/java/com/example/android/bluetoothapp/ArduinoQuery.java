package com.example.android.bluetoothapp;

import android.util.Log;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.R.attr.data;
import static android.support.v7.widget.AppCompatDrawableManager.get;

/**
 * Created by Mathilde on 28/06/2017.
 */

public class ArduinoQuery {

    int dataLenght = 0;
    int messageType;
    ArrayList<ArduinoData> datas;

    ArduinoQuery(int messageType, int dataLenght){
        try {
            setDataLenght(dataLenght);
            this.messageType = messageType;
        } catch (IOException e){
            Log.e("ARDUINO_QUERY", "failed initializatio: " + e.toString());
        }
    }


    public void setMessageType(int messageType){
        this.messageType = messageType;
    }

    public void setDataLenght(int numberOfValues) throws IOException{
        if(numberOfValues<0){
            throw new IOException("Invalide number of values");
        }else{
            this.dataLenght = numberOfValues;
        }
    }


    private void addData(ArduinoData data){
        this.datas.add(data);
    }


    public void addData(byte dataType, Object data) throws IOException{
        if(ArduinoData.INT < dataType && dataType < ArduinoData.BOOLEAN){
            ArduinoData convertedData;
            switch (dataType){

                case ArduinoData.BYTE:
                    convertedData =  new ArduinoData(1);
                    convertedData.setValue(0,((Byte) data).byteValue());
                    convertedData.setByteType(dataType);
                    datas.add(convertedData);
                    break;

                case ArduinoData.INT:
                    convertedData =  new ArduinoData(4);
                    convertedData.setValue(0,((Integer) data).byteValue());
                    convertedData.setByteType(dataType);
                    datas.add(convertedData);
                    break;

                case ArduinoData.CHAR:
                    convertedData =  new ArduinoData(4);
                    convertedData.setValue( (((Character)data).toString()).getBytes() );
                    convertedData.setByteType(dataType);
                    datas.add(convertedData);
                    break;

                case ArduinoData.FLOAT:
                    convertedData =  new ArduinoData(8);
                    convertedData.setValue(0,((Float) data).byteValue());
                    convertedData.setByteType(dataType);
                    datas.add(convertedData);
                    break;

                case ArduinoData.BOOLEAN:
                    convertedData =  new ArduinoData(1);
                    if ((Boolean)data == Boolean.TRUE){
                        convertedData.setValue(0,(byte)1);
                    }else if ((Boolean)data == Boolean.FALSE){
                        convertedData.setValue(0,(byte)0);
                    }
                    convertedData.setByteType(dataType);
                    datas.add(convertedData);
                    break;

                default:
                    break;
            }
        }else{
            throw new IOException("Invalide data type");
        }
    }


    public byte[] create(){
        ArrayList<Byte> processedMessage = new ArrayList<>();
        processedMessage.add(new Byte(((Integer) messageType).byteValue()));
        processedMessage.add(new Byte(((Integer) datas.toArray().length).byteValue()));
        for (ArduinoData data:datas) {
            processedMessage.add( new Byte(data.getType()) );
            for (byte bt: data.getValue()) {
                processedMessage.add(new Byte(bt));
            }
        }

        byte[] finalMessage = new byte[processedMessage.toArray().length];
        int i = 0;
        for (Byte data: processedMessage){
            finalMessage[i] = data.byteValue();
            i++;
        }
        return finalMessage;
    }

};


