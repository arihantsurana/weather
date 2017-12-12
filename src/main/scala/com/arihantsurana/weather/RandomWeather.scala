package com.arihantsurana.weather

/**
  * Created by arihant.surana on 12/12/17.
  */
object RandomWeather {
  // taken from http://weather.wikia.com/wiki/Types_of_Weather
  val weatherTypes =
    List("Rainy",
      "Stormy",
      "Sunny",
      "Cloudy",
      "Hot",
      "Cold",
      "Dry",
      "Wet",
      "Windy",
      "Hurricanes",
      "typhoons",
      "Sand-storms",
      "Snow-storms",
      "Tornados",
      "Humid",
      "Foggy",
      "Snow",
      "Thundersnow",
      "Hail",
      "Sleet",
      "drought",
      "wildfire",
      "blizzard",
      "avalanche",
      "Mist")
  def generate():(String, String, String, String)={
    var condition = weatherTypes(0)
    var temprature = ""
    var pressure =""
    var humidity = ""
    (condition, temprature, pressure, humidity)
  }

}
