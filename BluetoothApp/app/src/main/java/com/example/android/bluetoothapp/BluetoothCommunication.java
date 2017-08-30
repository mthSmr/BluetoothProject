package com.example.android.bluetoothapp;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Mathilde on 09/06/2017.
 */

public class BluetoothCommunication {

    private final BluetoothSocket socket;
    private final DataInputStream streamIn;
    private final DataOutputStream streamOut;

    private byte[] message;
    private ArrayList<Object> answer;



    BluetoothCommunication(BluetoothSocket btsocket) {
        this.socket = btsocket;
        OutputStream tmpStreamOut= null;
        InputStream tmpStreamIn = null;
        try{
            tmpStreamOut = socket.getOutputStream();
        } catch (Exception e){
            Log.e("CONNECTION","output stream failed:" + e.toString());
        }
        this.streamOut = new DataOutputStream(tmpStreamOut);
        try{
            tmpStreamIn = socket.getInputStream();
        } catch (Exception e){
            Log.e("CONNECTION","input stream failed:" + e.toString());
        }
        this.streamIn = new DataInputStream(tmpStreamIn);
    }

    public void queryEmpty(){
        ArduinoQuery query = new ArduinoQuery(0,0);
        message = query.create();
        sendQuery();
        //receiveAwr();
    }


    public ArrayList<Sensor> queryContent(){
        ArduinoQuery query = new ArduinoQuery(1,0);
        message = query.create();
        sendQuery();
        receiveAwr();
        ArrayList<Sensor> sensors = new ArrayList<>();
        for (Object result: answer) {
            if (result != 0){
                Sensor sensor = new Sensor();
                sensor.setAddress(((Integer)result).intValue());
                sensors.add(sensor);
                Log.i("QUERY", "received " + result);
            }
        }
        return sensors;
    }


    public void querySensor(Sensor sensor){
        ArduinoQuery query = new ArduinoQuery(2,1);
        try {
            query.addData( ArduinoData.INT, sensor.getAddress());
        } catch (IOException e){Log.e("QUERYSENSOR","error while adding data");}

        message = query.create();
        sendQuery();
        receiveAwr();
        //sensor.setValue(answer.get(0));
    }


    //vérifie que le device trouvé est bien un point d'accès
    public void testAccessPointID(){
    }


    public void endCommunication(){
        try {
            streamIn.close();
            streamOut.close();
        } catch (IOException e){
            Log.e("END COMMUNICATION", "failed to close stream:" + e.toString());
        }
    }


    private void sendQuery(){
        try{
            for (int arg: message) {
                streamOut.writeInt(arg);
            }
        } catch (IOException e){
            Log.e("SEND","sending message failed:" + e.toString());
        }
    }


    private void receiveAwr(){
        byte[] buffer = ByteBuffer.allocate(1024).array();
        try {
            streamIn.readFully(buffer);
            answer = read(buffer);
        } catch (IOException e){
            Log.e("RECEIVE","reading message failed:" + e.toString());
        }
    }


    private void logMessage(){
        for (Object data : answer) {
            Log.e("RECEIVE","reading:" + data.toString());
        }
    }


    private void resetbuffer(byte[] buffer){
        buffer = ByteBuffer.allocate(0).array();
    }

    public int[] toInt(byte[] buffer, int[] result){
        for (int cpt = 0; cpt<buffer.length; cpt=cpt+4){
            int value = 0;
            if (cpt + 3 < buffer.length){value += (buffer[cpt + 3] & 0x000000FF) << 24;}
            if (cpt + 2 < buffer.length){value += (buffer[cpt + 2] & 0x000000FF) << 16;}
            if (cpt + 1 < buffer.length){value += (buffer[cpt + 1] & 0x000000FF) << 8;}
            value += (buffer[cpt] & 0x000000FF);
            //result[cpt/4] = value;
            Log.i("TOINT","new int = " + value);
        }
        return result;
    }


    public final int readLittleInt() throws IOException {
        byte[] buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).array();
        streamIn.readFully(buffer, 0, 4);
        return (buffer[3]) << 24 | (buffer[2] & 0xff) << 16 |
                (buffer[1] & 0xff) << 8 | (buffer[0] & 0xff);
    }


    public ArrayList<Object> read(byte[] buffer){
        ByteBuffer tempBuffer = ByteBuffer.wrap(buffer);
        int tempMessageType = tempBuffer.getInt();
        int tempDataLenght = tempBuffer.getInt();
        ArrayList<Object> result = new ArrayList<>();

        if (tempDataLenght>0) {
            for (int i = 0; i < tempDataLenght; i++) {

                switch (tempBuffer.getInt()){
                    case ArduinoData.BYTE:
                        Byte dataByte = new Byte(tempBuffer.get());
                        result.add(dataByte);
                        break;

                    case ArduinoData.INT:
                        Integer dataInt = new Integer(tempBuffer.getInt());
                        result.add(dataInt);
                        break;

                    case ArduinoData.CHAR:
                        Character dataChar = new Character(tempBuffer.getChar());
                        result.add(dataChar);
                        break;

                    case ArduinoData.FLOAT:
                        Float dataFloat = new Float(tempBuffer.getFloat());
                        result.add(dataFloat);
                        break;

                    case ArduinoData.BOOLEAN:
                        byte temp = tempBuffer.get();
                        if (temp == 1){
                            Boolean dataBool = new Boolean(Boolean.TRUE);
                            result.add(dataBool);
                        } else{
                            Boolean dataBool = new Boolean(Boolean.FALSE);
                            result.add(dataBool);
                        }
                        break;

                    default:
                        break;
                }
            }
        }
        return result;
    }


}
