class SensorData {
  private float temperature;
  private float distance;
  private float humidity;
  
  SensorData(float temperature, float distance, float humidity) {
    this.temperature = temperature;
    this.distance = distance;
    this.humidity = humidity;
  }
  
  float getTemperature() {
    return this.temperature;
  }
  
  float getDistance() {
    return this.distance;
  }
  
  float getHumidity() {
    return this.humidity;
  }
  
  float getFahrenheit() {
    return this.temperature * 1.8 + 32;
  }
  
  float getInches() {
    return this.distance / 2.54;
  }
}
