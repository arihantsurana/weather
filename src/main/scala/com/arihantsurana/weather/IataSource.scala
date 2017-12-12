package com.arihantsurana.weather

import scala.io.Source

/**
  * Created by arihant.surana on 12/12/17.
  */
object IataSource {
  val inputPath = s"/data/iata.csv"
  val inputUrl = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat"

  def readIataDataFromFile(): List[String] = {
    Source.fromFile(inputPath).getLines.toList
  }

  def readIataDatafromWeb(): List[String] = {
    // read iata data from https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat
    val content = scala.io.Source.fromURL(inputUrl).mkString
    content.split("\n").filter(_ != "").toList
  }
}
