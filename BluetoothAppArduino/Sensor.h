
/* 
 * File:   Sensor.h
 * Author: Mathilde
 *
 * Created on 11 octobre 2016, 14:05
 */

#include "Arduino.h"


#ifndef SENSOR_H
#define SENSOR_H

class Sensor {

private:
    int digitalPin;
    int analogPin;
    int cap;
	  float analogVal;
	  bool value;
    

public:
    Sensor();
    Sensor(int, int, int);
    
    //getter
    bool getValue();
    int getPosition();
    int getCap();

    //setter
    void setPosition(int pos );
    void setCap(int);

  	//utilitaire
  	void read();


};


#endif /* SENSOR_H */

