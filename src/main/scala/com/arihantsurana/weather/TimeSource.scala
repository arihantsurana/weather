package com.arihantsurana.weather

import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer


/**
  * Created by arihant.surana on 12/12/17.
  */
object TimeSource {
  def getTimeSeries(startSeedTime: Long, size: Integer): List[String] = {
    val buffer = new ListBuffer[String]
    // create a series with 1 day interval
    0 to size foreach { i => buffer.append(new DateTime(startSeedTime).plusDays(i).toString()) }
    buffer.toList
  }
}
