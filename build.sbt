name := "weather"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  // Spark dependencies
  "org.apache.spark" %% "spark-core" % "2.2.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.2.0" % "provided",
  // for date time handling
  "com.github.nscala-time" %% "nscala-time" % "2.18.0",
  // for unit tests
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

scalacOptions ++= Seq("-deprecation", "-feature")

run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))

// META-INF discarding
assemblyMergeStrategy in assembly := {
  {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}

