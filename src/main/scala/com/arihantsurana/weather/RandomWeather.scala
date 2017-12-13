package com.arihantsurana.weather

import scala.util.Random

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
  def generate(random: Random):(String, String, String, String)={
    var condition = weatherTypes(random.nextInt(weatherTypes.size))
    var temprature = random.nextDouble()
    var pressure =random.nextDouble()
    var humidity = random.nextDouble()
    (condition, temprature.toString, pressure.toString, humidity.toString)
  }

}
