package com.arihantsurana.weather

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession

import scala.io.Source

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
    val inputPath =
      s"/data/somefile.txt"
    val outputPath =
      s"file:///data/output"
    log.info(s"Output path set to ${outputPath}")

    val sc = spark.sparkContext
    val textFile = sc.parallelize(Source.fromFile(inputPath).getLines.toList)
    // use with S3 or hdfs as source
    //val textFile = sc.textFile(inputPath)
    val counts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    counts.saveAsTextFile(outputPath)
    spark.stop()
  }
}
