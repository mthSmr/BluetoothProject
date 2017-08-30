/*
  Example Bluetooth Serial Passthrough Sketch
 by: Jim Lindblom
 SparkFun Electronics
 date: February 26, 2013
 license: Public domain

 This example sketch converts an RN-42 bluetooth module to
 communicate at 9600 bps (from 115200), and passes any serial
 data between Serial Monitor and bluetooth module.
 */

 #include "Sensor.h"


#define bluetooth Serial1

#define bds 115200
#define QUERY_SIZE 1024
#define SENSORS_NUM 2

int query[QUERY_SIZE] = {-1};
Sensor sensorList[SENSORS_NUM];


/*************************
 *     - FUNCTIONS -     *
 *************************/

void startCommandeMode(){
  bluetooth.print("$");  // Print three times individually
  bluetooth.print("$");
  bluetooth.print("$");  // Enter command mode
}

void endCommandeMode(){
  bluetooth.print("-");  // Print three times individually
  bluetooth.print("-");
  bluetooth.print("-");  // Enter command mode
}

void rebootBT(){
  startCommandeMode();
  bluetooth.print("R,1");
  endCommandeMode();
}

void communicationTest(){ //test/example of communication in both ways
  
  /**** works ****/
  if(bluetooth.available())  // If the bluetooth sent any characters
  {
    // Send any characters the bluetooth prints to the serial monitor
    char value = (char) bluetooth.read();
    Serial.write(value);
  }

  /**** works ****/
  if(Serial.available())  // If stuff was typed in the serial monitor
  {
    // Send any characters the Serial monitor prints to the bluetooth
    char value = (char)Serial.read();
    bluetooth.write(value);
  }
  delay(10);
}


int readBluetooth(){
  //int count = 0;
  int value = -1;
  if (bluetooth.available())  // If the bluetooth sent any characters
      {
          value = bluetooth.read();
          Serial.println("Read - Received:");
          Serial.println(value);
        // Send any characters the bluetooth prints to the serial monitor
        
        //query[count] = value;
        //count++;
      }
   return value;
}


void returnContent(){
  for(int i = 0; i<SENSORS_NUM; i++){
    bluetooth.write(sensorList[i].getPosition());
    Serial.print("--> Answered: ");
    Serial.println(sensorList[i].getPosition());
  }
}


void handleQuery(){
    switch(readBluetooth()){

      case 0: bluetooth.write(0);
              Serial.println("--> Answered 0");
              rebootBT();
        break;

      case 1: returnContent();
              rebootBT();
        break;

      case 2:
        break;

      default:
        break;
        
    }
}


void resetQuery(){
  for(int i=0; i<QUERY_SIZE; i++){
    query[i] = -1;
  }
}


/**************************
 *     - SETUP/LOOP -     *
 **************************/

void setup()
{
  Serial.begin(bds);  // Begin the serial monitor at 9600bps
  bluetooth.begin(bds);  // The Bluetooth Mate defaults to 115200bps

  //startCommandeMode();
  //delay(100);  // Short delay, wait for the Mate to send back CMD
  /*
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
  //bluetooth.begin(9600);  // Start bluetooth serial at 9600
  */

  Sensor temperature = Sensor(1,2,0,2);
  Sensor humidity = Sensor(3,4,0,3);
  sensorList[0]=temperature;
  sensorList[1]=humidity;
  
  
}

void loop()     /* >>> -!!!- a tester -!!!- <<< */
{
  //readBluetooth();
  handleQuery();


  //*******************************//
  //resetQuery();

}

