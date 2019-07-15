import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Sonarduino extends PApplet {


// Set grid width, height, and center
final int WIDTH = 1000;
final int HEIGHT = 1000;
final int xCenter = WIDTH / 2;
final int yCenter = HEIGHT / 2;
// Determine length of a single line to receive from Arduino
final int LINE_FEED = 10;
// Set communication rate
final int BAUD_RATE = 9600;
// Get last element in serial port list
String arduinoPortName = Serial.list()[Serial.list().length - 1];
// Create objects
Serial arduinoPort;
SensorData sensorData;
// Init degree and radius to 0
int degree = 0;
int radius = 0;

float currentTemp = 0;
float currentDistance = 0;
float currentHumidity = 0;
float currentTempF = 0;
float currentDistanceIn = 0;

// Open up communication with Arduino
public void setup() {
  
  arduinoPort = new Serial(this, arduinoPortName, BAUD_RATE);
  arduinoPort.bufferUntil(LINE_FEED);
}

// Get sensorData class when information is received
public void serialEvent(Serial port) {
  sensorData = getSensorData();
  if (sensorData != null) {
    println("Temperature: " + sensorData.getTemperature());
    println("Distance: " + sensorData.getDistance());
    println("Humidity: " + sensorData.getHumidity());
    radius = min(300, PApplet.parseInt(sensorData.getDistance() * 2));
    currentTemp = sensorData.getTemperature();
    currentTempF = sensorData.getFahrenheit();
    currentDistance = sensorData.getDistance();
    currentDistanceIn = sensorData.getInches();
    currentHumidity = sensorData.getHumidity();
  }
}

// Create sensorData class from Arduino string
public SensorData getSensorData() {
  SensorData result = null;
  if (arduinoPort.available() > 0) {
    final String arduinoOutput = arduinoPort.readStringUntil(LINE_FEED);
    result = parseArduinoOutput(arduinoOutput);
  }
  return result;
}

// Format Arduino output into a list of two floats
public SensorData parseArduinoOutput(final String arduinoOutput) {
  SensorData result = null;
  if (arduinoOutput != null) {
    final int[] data = PApplet.parseInt(split(trim(arduinoOutput), ','));
    if (data.length == 3) {
      result = new SensorData(data[0] / 100.0f, data[1] / 100.0f, data[2] / 100.0f);
    }
  }
    return result;
}

// Init circles
public void init_screen() {
  background(255);
  stroke(0);
  strokeWeight(1);
  textSize(16);
  int[] radius_values = { 300, 250, 200, 150, 100, 50 };
  for (int r = 0; r < radius_values.length; r++) {
    //fill(#082e6e);
    fill(0);
    text(">" + radius_values[r] + " cm", xCenter - 35, xCenter - radius_values[r] - 10);
    fill(0, 102, 153, 51);
    final int current_radius = radius_values[r] * 2;
    ellipse(xCenter, yCenter, current_radius, current_radius);
  }  
  strokeWeight(10);
  updateLabels();
}

// Draw point from Arduino sensorData
public void draw() {
  init_screen();
  int x = (int)(radius * Math.cos(degree * Math.PI / 180));
  int y = (int)(radius * Math.sin(degree * Math.PI / 180));
  point(xCenter + x, yCenter + y);
  if (++degree == 360)
    degree = 0;
}

public void updateLabels() {
  
   // Write labels
   fill(0xff082e6e);
   text("Distance:", 10, 90);
   text("Temperature:", 10, 115);
   text("Humidity:", 10, 140);
   fill(0);
   text(currentDistance + " cm  /  " + nf(currentDistanceIn, 0, 2) + " in", 130, 90);
   text(currentTemp + " C  /  " + currentTempF + " F", 130, 115);
   text(currentHumidity + "%", 130, 140);
}
  
  
  
  
  
class SensorData {
  private float temperature;
  private float distance;
  private float humidity;
  
  SensorData(float temperature, float distance, float humidity) {
    this.temperature = temperature;
    this.distance = distance;
    this.humidity = humidity;
  }
  
  public float getTemperature() {
    return this.temperature;
  }
  
  public float getDistance() {
    return this.distance;
  }
  
  public float getHumidity() {
    return this.humidity;
  }
  
  public float getFahrenheit() {
    return this.temperature * 1.8f + 32;
  }
  
  public float getInches() {
    return this.distance * 2.54f;
  }
}
  public void settings() {  size(1000, 1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Sonarduino" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
