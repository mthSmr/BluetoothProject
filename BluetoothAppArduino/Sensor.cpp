
/* 
 * File:   Sensor.cpp
 * Author: Mathilde
 * 
 * Created on 11 octobre 2016, 14:05
 */

#include "Sensor.h"

Sensor::Sensor(){
}

Sensor::Sensor(int newDigitalPin, int newAnalogPin, int newCap){
    this->digitalPin = newDigitalPin;
    this->analogPin = newAnalogPin;
    this->cap = newCap;
}

void Sensor::read(){
  int analog;
    // print out the value you read:
  digitalWrite(this->digitalPin, HIGH);
  analog = analogRead(analogPin);
//  Serial.print("capteur ");
//    Serial.print(i);
//    Serial.print(" = ");
//    Serial.println(analog);
  digitalWrite(this->digitalPin, LOW);

  if(analog>=this->cap){
    this->value = true ;
    analogVal = analog;
  }else{
    this->value = false ;
  }
  
}

void Sensor::setCap(int newCap){
    this->cap = newCap;
}

int Sensor::getCap(){
  return this->cap;
}

bool Sensor::getValue(){
  return this->value;
}

void Sensor::setPosition(int pos)
{
  this->pos = pos;
}

int Sensor::getPosition()
{
  return this->pos;
}






