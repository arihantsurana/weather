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

  def generate(random: Random): (String, String, String, String) = {
    var condition = weatherTypes(random.nextInt(weatherTypes.size))
    var temprature = random.nextDouble()
    var pressure = random.nextDouble()
    var humidity = random.nextDouble()
    (condition, temprature.toString, pressure.toString, humidity.toString)
  }

  def generateForTimeseries(inputRows: Iterable[List[String]], random: Random, variance: Double): Iterable[List[String]] = {
    // for each
    val outputRows = new ListBuffer[List[String]]
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
      temprature = generateNextDouble(temprature, -40.0, 50.0, variance, random)
      //TODO: convert this random generator to a function of temprature and altitude
      pressure = generateNextDouble(pressure, 870, 1065, variance, random)
      //TODO: convert this random generator to a function of temprature and altitude
      humidity = generateNextDouble(humidity, 0, 100, variance, random)
      //TODO: Convert randomly generated value to a function of other 3 variables
      conditionIndex = generateNextInt(conditionIndex, 0, weatherTypes.size, variance, random)
      // append the generated weather data to existing row
      outputRows.append(row ++ List(weatherTypes(conditionIndex), temprature.toString, pressure.toString, humidity.toString))
    })
    // gather the buffer as immutable iterator (list)
    outputRows.toList
  }

  def generateNextDouble(curVal: Double, absoluteMin: Double, absoluteMax: Double, variance: Double, random: Random): Double = {
    val precision = 100
    generateNextInt(
      (curVal * precision).toInt,
      (absoluteMin * precision).toInt,
      (absoluteMax * precision).toInt,
      variance,
      random
    ) / precision
  }

  def generateNextInt(curVal: Int, absoluteMin: Int, absoluteMax: Int, variance: Double, random: Random): Int = {
    val curMax = Math.min((curVal + (curVal * variance)).toInt, absoluteMax)
    val curMin = Math.max((curVal - (curVal * variance)).toInt, absoluteMin)
    curMin + random.nextInt(curMax - curMin + 1)
  }

}
