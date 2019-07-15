#include <dht.h>

const unsigned int TEMP_PIN = 7;
const float SUPPLY_VOLTAGE = 5.0;
const unsigned int TRIG_PIN = 11;
const unsigned int ECHO_PIN = 12;
const float SENSOR_GAP = 0.2;
const unsigned int BAUD_RATE = 9600;
float current_temperature = 0.0;
float current_distance = 0.0;
const unsigned int DELAY = 1100;

dht DHT;

void setup() {
  Serial.begin(BAUD_RATE);
}

void loop() {
  current_temperature = get_temperature();
  Serial.print(scaled_value(current_temperature));
  Serial.print(",");
  const unsigned long duration = measure_distance();
  current_distance = microseconds_to_cm(duration);
  Serial.print(scaled_value(current_distance));
  Serial.print(",");
  Serial.println(scaled_value(DHT.humidity));
  delay(DELAY);
}

long scaled_value(const float value) {
  float round_offset = value < 0 ? -0.5 : 0.5;
  return (long)(value * 100 + round_offset);
}

const float get_temperature() {
  DHT.read11(TEMP_PIN);
  return DHT.temperature;
}

const float microseconds_per_cm() {
  return 1 / ((331.5 + (0.6 * current_temperature)) / 10000);
}

const float sensor_offset() {
  return SENSOR_GAP * microseconds_per_cm() * 2;
}

const float microseconds_to_cm(const unsigned long microseconds) {
  const float net_distance = max(0, microseconds - sensor_offset());
  return net_distance / microseconds_per_cm() / 2;
}

const unsigned long measure_distance() {
  // Init pins
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIG_PIN, OUTPUT);

  // Give delay
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(5);
  digitalWrite(TRIG_PIN, LOW);

  // Get time
  return pulseIn(ECHO_PIN, HIGH);

  delay(1000);
}
