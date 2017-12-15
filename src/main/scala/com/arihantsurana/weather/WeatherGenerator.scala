package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession
import org.joda.time.{DateTime, Instant}

import scala.util.Random

/**
  * Created by arihant.surana on 11/12/17.
  */
object WeatherGenerator {

  def main(args: Array[String]) {
    val log = LogManager.getRootLogger
    args.foreach(s => log.info(s"Argument - ${s}"))
    val timeSeriesSize = args(0).toInt
    val outputPath = s"file:///data/output/${args(1)}"
    val startTime = new Instant().getMillis
    val spark = SparkSession
      .builder()
      .appName("WeatherGenerator")
      .getOrCreate()
    log.info(s"Output path set to ${outputPath}")
    val random = new Random(999494958679785L)
    val sc = spark.sparkContext
    val iataCitiesRdd =
      sc.parallelize(csvSources.readIataDatafromWeb())
    val localTimeRdd = sc.parallelize(TimeSource.getTimeSeries(DateTime.now.getMillis, timeSeriesSize))
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
      .flatMap(kv => RandomWeather.generateForTimeseries(kv._2.toList, csvSources.readAvgValuesFromFile))
      // Convert lat long and alt to a single column
      .map(row => List(row(0), row(1) + ", " + row(2) + ", " + row(3)) ++ row.drop(4))
      // Prepare csv formatted strings
      .map(row => prepCsv(row, "|"))
      // Write the output data to files
      .saveAsTextFile(outputPath)

    // stop spark context and thats that!
    spark.stop()
    val endTime = new Instant().getMillis
    log.info(s"Total execution time ${endTime - startTime} ms")
  }

  def prepCsv(row: List[String], delimiter: String): String = {
    row
      // cleanup by stripping any quotes from each cell
      .map(cell => cell.stripPrefix("\"").stripSuffix("\"").trim)
      // combine all cells to form a row
      .mkString(delimiter)
  }
}
