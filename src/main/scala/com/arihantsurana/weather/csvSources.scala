package com.arihantsurana.weather

import scala.io.Source

/**
  * Created by arihant.surana on 12/12/17.
  */
object csvSources {
  val inputPathIataGeography = s"/data/iata.csv"
  val inputPathAvgValues = s"/data/average_conditions.csv"
  val inputUrlIataGeography = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat"

  // for backup purposes where spark may not have access to internet.
  def readIataDataFromFile(): List[String] = {
    Source.fromFile(inputPathIataGeography).getLines.toList
  }

  def readAvgValuesFromFile(): List[List[Double]] = {
    // read a file extracted from https://www.engineeringtoolbox.com/standard-atmosphere-d_604.html using SI units table
    Source.fromFile(inputPathAvgValues).getLines.toList.map(line => line.split(",").map(cell => cell.trim.toDouble).toList)
  }

  def readIataDatafromWeb(): List[String] = {
    // read iata data from https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat
    val content = scala.io.Source.fromURL(inputUrlIataGeography).mkString
    content.split("\n").filter(_ != "").toList
  }
}
