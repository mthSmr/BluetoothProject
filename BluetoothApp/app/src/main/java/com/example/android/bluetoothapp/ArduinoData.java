package com.example.android.bluetoothapp;

/**
 * Created by Mathilde on 28/06/2017.
 */

public class ArduinoData {
    public static final byte BYTE  = 1;
    public static final byte INT  = 2;
    public static final byte CHAR  = 3;
    public static final byte FLOAT = 4;
    public static final byte BOOLEAN  = 5;

    public static final int BIG_ENDIAN = 1;
    public static final int LITTLE_ENDIAN = 2;


    private int size;
    private byte[] value = new byte[size];
    private byte type;
    private int endian;


    ArduinoData(int size){
        this.size = size;
        this.endian = BIG_ENDIAN;

    }

    public void setByteType(byte type) {
        this.type = type;
    }

    public void setEndian(int endian){
        this.endian = endian;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setValue(int index, byte value) throws IndexOutOfBoundsException{
        if (index>=0 && index<this.size) {
            this.value[index] = value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void setValue(byte[] value){
        this.value = value;
    }

    public byte getType(){
        return this.type;
    }

    public byte[] getValue(){
        return this.value;
    }
}
