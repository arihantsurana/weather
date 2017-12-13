package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession

/**
  * Created by arihant.surana on 11/12/17.
  */

/**
  * Created by arihant.surana on 15/09/2016.
  *
  * primary Streaming object to handle Jirafe events and an entrypoint to Spark Streaming job
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

    val sc = spark.sparkContext
    val iataCitiesRdd = sc.parallelize(IataSource.readIataDataFromFile)
    val localTimeRdd = sc.parallelize(TimeSource.getTimeSeries)
    // read the iata codes and locations into a data frame
    val stationsRdd = iataCitiesRdd
      // Split Lines into individual cells of the csv input
      .map(line => line.split(","))
      // Extract the required columns, i.e. IATA code
      .map(row => List(row(4), row(6), row(7), row(8)))

    // cross join cities with time series
    val resultsRdd = stationsRdd.cartesian(localTimeRdd)
      // Flatten rdd to a single row and combine latitude, longitude and altitude as single locatiopn column
      .map(row => List(row._1(0), row._1(1) + ", " + row._1(2) + ", " + row._1(3), row._2))
      // Add randomized weather data to the row
      .map(row => {
      val generatedWeather = RandomWeather.generate()
      row ++ List(generatedWeather._1, generatedWeather._2, generatedWeather._3, generatedWeather._4)
    })
      // Prepare csv formatted strings
      .map(row => prepCsv(row, "|"))

    println("resultsRdd.top(10)" + resultsRdd.top(10))

    println("resultsRdd.count()" + resultsRdd.count())

    // Write the output data to files
    resultsRdd.saveAsTextFile(outputPath)


    // stop spark context and thats that!
    spark.stop()
  }

  def prepCsv(row: List[String], delimiter: String): String = {
    row.mkString(delimiter)
  }
}
