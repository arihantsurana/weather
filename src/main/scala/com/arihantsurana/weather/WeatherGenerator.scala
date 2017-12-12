package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.{Row, SparkSession}

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
    import spark.implicits._
    val iataCitiesDf = iataCitiesRdd.map(line => line.split(",")).toDF(
      "Airport ID", "Name", "City", "Country", "IATA", "ICAO", "Latitude", "Longitude", "Altitude", "Timezone", "DST", "Tz", "Type", "Source")
    iataCitiesDf.createOrReplaceTempView("stations")
    localTimeRdd.toDF("Localtime").createOrReplaceTempView("timeseries")
    // cross join cities with time series
    val joinedDf = spark.sql("SELECT IATA, CONCAT(Latitude , ', ' , Longitude , ', ' , Altitude) as Location, Localtime FROM stations JOIN timeseries ON 1=1 ")
    joinedDf.show()
    val resultDF = joinedDf.map(row => {
      val generatedWeather = RandomWeather.generate()
      Row(row.get(0), row.get(1), row.get(2), generatedWeather._1, generatedWeather._2, generatedWeather._3, generatedWeather._4)
    }).toDF("IATA", "Loacation", "Localtime", "Condition", "Temprature", "Pressure", "Humidity")
    resultDF.show()
    resultDF.write.option("sep", "|").option("header", "true").csv(outputPath)
    spark.stop()
  }
}
