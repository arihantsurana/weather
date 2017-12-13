package com.arihantsurana.weather

import java.util.Random

import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer


/**
  * Created by arihant.surana on 12/12/17.
  */
object TimeSource {
  val inputPath = s"/data/iata.csv"
  val inputUrl = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat"

  def getTimeSeries(size: Integer): List[String] = {
    val random = new Random(9807470287039840L)
    val buffer: ListBuffer[String] = new ListBuffer[String]
    0 to size foreach { _ => buffer.append(new DateTime(random.nextInt(10000)).toString()) }
    buffer.toList
  }
}
