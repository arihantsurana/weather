package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession

import scala.util.Random

/**
  * Created by arihant.surana on 11/12/17.
  */
object WeatherGenerator {

  def main(args: Array[String]) {
    val log = LogManager.getRootLogger
    args.foreach(s => log.info(s"Argument - ${s}"))

    val spark = SparkSession
      .builder()
      .appName("WeatherGenerator")
      .getOrCreate()
    val outputPath = s"file:///data/output"
    log.info(s"Output path set to ${outputPath}")
    val random = new Random(999494958679785L)
    val sc = spark.sparkContext
    val iataCitiesRdd =
      sc.parallelize(IataSource.readIataDataFromFile).repartition(8)
    val localTimeRdd = sc.parallelize(TimeSource.getTimeSeries(100))
    // read the iata codes and locations into a data frame
    iataCitiesRdd
      // Split Lines into individual cells of the csv input
      .map(line => line.split(","))
      // Extract the required columns, the rows from this point forward are represented as a list of strings
      .map(row => List(row(4), row(6), row(7), row(8)))
      // cross join cities with time series
      .cartesian(localTimeRdd)
      // Flatten rdd elements from nested list value tuple to a list
      .map(row => row._1 :+ row._2)
      // group by the iata city codes so we can gather all data for a city in a single iterator
      .groupBy(row => row(0))
      // perform random weather generation for each city
      .flatMap(kv => RandomWeather.generateForTimeseries(kv._2, random, 10.5))
      // Prepare csv formatted strings
      .map(row => prepCsv(row, "|"))
      // Write the output data to files
      .saveAsTextFile(outputPath)

    // stop spark context and thats that!
    spark.stop()
  }

  def prepCsv(row: List[String], delimiter: String): String = {
    row
      // cleanup by stripping any quotes from each cell
      .map(cell => cell.stripPrefix(",").stripSuffix(",").trim)
      // combine all cells to form a row
      .mkString(delimiter)
  }
}
