package com.arihantsurana.weather

/**
  * Created by arihant.surana on 12/12/17.
  */
object TimeSource {
  val inputPath = s"/data/iata.csv"
  val inputUrl = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat"

  def getTimeSeries(): List[String] = {
    List("2015-12-23T05:02:12Z")
  }
}
