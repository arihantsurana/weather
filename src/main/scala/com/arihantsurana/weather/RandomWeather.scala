package com.arihantsurana.weather

import scala.collection.mutable.ListBuffer
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
  val tempratureMin = -40.0
  val tempratureMax = 50.0
  val tempratureVariance = 10
  val pressureMin = 870
  val pressureMax = 1065
  val pressureVariance = 50
  val humidityMin = 0
  val humidityMax = 100
  val humidityVariance = 20

  def generate(random: Random): (String, String, String, String) = {
    var condition = weatherTypes(random.nextInt(weatherTypes.size))
    var temprature = random.nextDouble()
    var pressure = random.nextDouble()
    var humidity = random.nextDouble()
    (condition, temprature.toString, pressure.toString, humidity.toString)
  }

  def generateForTimeseries(inputRows: Iterable[List[String]]): Iterable[List[String]] = {
    // for each
    val outputRows = new ListBuffer[List[String]]
    val random = new Random
    //TODO: setup initial conditions as some meanigful value from an initial condition based on altitude
    var conditionIndex = random.nextInt(weatherTypes.size)
    var temprature = random.nextDouble()
    var pressure = random.nextDouble()
    var humidity = random.nextDouble()
    // sort the list of values on date
    val sortedRows = inputRows.toSeq.sortBy(row => row(4))
    sortedRows.foreach(row => {
      // altitude in meters
      val altitude = row(3)
      // Calculate temprature as a gradual random variation from last value
      temprature = generateNextDouble(temprature, tempratureMin, tempratureMax, tempratureVariance, random)
      //TODO: convert this random generator to a function of temprature and altitude
      pressure = generateNextDouble(pressure, pressureMin, pressureMax, pressureVariance, random)
      //TODO: convert this random generator to a function of temprature and altitude
      humidity = generateNextDouble(humidity, humidityMin, humidityMax, humidityVariance, random)
      //TODO: Convert randomly generated value to a function of other 3 variables
      conditionIndex = generateNextInt(conditionIndex, 0, weatherTypes.size, 3, random)
      // append the generated weather data to existing row
      outputRows.append(row ++ List(weatherTypes(conditionIndex), temprature.toString, pressure.toString, humidity.toString))
    })
    // gather the buffer as immutable iterator (list)
    outputRows.toList
  }

  def generateNextDouble(curVal: Double, absoluteMin: Double, absoluteMax: Double, variance: Int, random: Random): Double = {
    val precision = 100
    // generate double values by using integer generator
    generateNextInt(
      (curVal * precision).toInt,
      (absoluteMin * precision).toInt,
      (absoluteMax * precision).toInt,
      (variance * precision).toInt,
      random
    ) / precision
  }

  def generateNextInt(curVal: Int, absoluteMin: Int, absoluteMax: Int, variance: Int, random: Random): Int = {
    val incrementedValue = curVal + (random.nextInt(variance * 2) - variance)
    // cap the incremented value between the cur min and max ranges
    Math.max(Math.min(incrementedValue, absoluteMax), absoluteMin)
  }

}
