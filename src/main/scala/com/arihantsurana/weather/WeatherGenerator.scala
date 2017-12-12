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
    val iataCitiesRdd = sc.parallelize(IataSource.readIataDataFromFile())
    // read the iata codes and locations into a data frame
    import spark.implicits._
    val iataCitiesDf = iataCitiesRdd.toDF(
      "Airport ID",
      "Name",
      "City",
      "Country",
      "IATA",
      "ICAO",
      "Latitude",
      "Longitude",
      "Altitude",
      "Timezone",
      "DST",
      "Tz",
      "Type",
      "Source")
    val filteredDf = iataCitiesDf.select($"IATA", $"Latitude" + "," + $"Longitude" + "," +$"Altitude" )
    filteredDf.show()
    filteredDf.write.option("sep","|").option("header","true").csv(outputPath)
    spark.stop()
  }
}
