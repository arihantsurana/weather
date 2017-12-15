package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession
import org.joda.time.{DateTime, Instant}

import scala.util.Random

/**
  * Created by arihant.surana on 11/12/17.
  */
object WeatherGenerator {
  // Setup logger
  val log = LogManager.getRootLogger

  def main(args: Array[String]) {
    // read args and configure app
    val (timeSeriesSize, outputPath, random) = initializeApp(args)

    // record start time
    val startTime = new Instant().getMillis

    // Initialize sark
    val spark = SparkSession
      .builder()
      .appName("WeatherGenerator")
      .getOrCreate()
    val sc = spark.sparkContext

    // read iata data from web and setup an RDD
    val iataCitiesRdd = sc.parallelize(csvSources.readIataDatafromWeb())

    // Generate time series into an RDD
    val localTimeRdd = sc.parallelize(TimeSource.getTimeSeries(DateTime.now.getMillis, timeSeriesSize))

    iataCitiesRdd
      // Split Lines into individual cells of the csv input
      .map(line => line.split(","))
      // Extract the required subset of columns
      .map(row => List(row(4), row(6), row(7), row(8)))
      // cross join cities with time series
      .cartesian(localTimeRdd)
      // Flatten rdd elements from nested list value tuple to a list
      .map(row => row._1 :+ row._2)
      // group by the iata city codes to gather all data for a station in a single list for partitioned processing
      .groupBy(row => row(0))
      // perform random weather generation for each station
      .flatMap(kv => RandomWeather.generateForTimeseries(kv._2.toList, csvSources.readAvgValuesFromFile))
      // Convert lat, long and alt to a single column
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

  def initializeApp(args: Array[String]): (Integer, String, Random) = {
    args.foreach(s => log.info(s"Argument - ${s}"))
    if (args.length < 2) {
      System.err.println(
        s"""
           |   Usage: com.arihantsurana.weather [Time Series size Integer] [output file String]
           |   Please initialize the app correctly.
        """.stripMargin)
      System.exit(1)
    }
    val timeSeriesSize = args(0).toInt
    val outputPath = s"file:///data/output/${args(1)}"
    val random = new Random(999494958679785L)
    (timeSeriesSize, outputPath, random)
  }
}
