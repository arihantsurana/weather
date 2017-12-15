# weather
This project sets up a basic spark application and provides means to execute the application using docker on local env.

The data to be shared is placed in the `./data` directory, for all execution types you will need to make sure you have the data directory and its contents available to the spark workers and master.

## Build and Execute
This is a spark application which can be built into a spark submittable jar using `sbt assembly`
However to execute it there are a few ways:

### On your spark cluster
Use the two command line args and submit the spark jar using
 ```
./bin/spark-submit \
  --class com.arihantsurana.weather.WeatherGenerator \
  --master <master-url> \
  --deploy-mode <deploy-mode> \
  --conf <key>=<value> \
  ... # other options
  /Users/arihant.surana/Documents/Code/arihantsurana/weather/target/scala-2.11/weather-assembly-1.0.jar \
  100 weather-output.csv
```

### On your local spark installation
Pretty much the same as above, use the jar and submit it as you would any other spark app.

### On Docker container - Least effort
You can use a transient docker based spark env to execute this project. The docker image outlined below, sets up an sbt env, downloads the github project, compiles and executes the project inside the container resulting in the data being shared using the `./data/` available to the container as `/data/`

How?
Just review and execute `./exec.sh`

NOTE -
For non linux runtime env, eg. mac you will need to setup the local ip of the machine in exec.sh script before you execute it.

You can also setup/tweak your input arguments in the script.

Since the container downloads,  and compiles the project, it takes significant amount of time to just setup sbt and setup all dependencies.
The actual execution times will be much less than the setup time. You can see the exec time calculated at the end of execution logs like so:
```
...
17/12/16 02:39:15 INFO OutputCommitCoordinator$OutputCommitCoordinatorEndpoint: OutputCommitCoordinator stopped!
17/12/16 02:39:15 INFO SparkContext: Successfully stopped SparkContext
17/12/16 02:39:15 INFO root: Total execution time 25425 ms
```


## Input
The application uses a list of iarta codes and their geographies from `https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat`

Apart from this, to have plausible starting points for generated weather data, a csv file is supplied in the `/data/average_conditions.csv` directory, this file was prepared from the data available at `https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat`

apart from this the spark application expects two inputs as command line arguments -
1) Time Series Size : Integer - to tune the size of time series data, i.e. how many days of data per station do you want?
2) Output path suffix : String - output path suffix to be applied to existing output directory path `/data/output/`

## Output
Csv data in the format:

is supplied in multiple files in the directory specified in the input

## About Weather Generator
The weather generator application is currently a starting point / framework which generates data using a list of all airport stations in the world, initializews their state as a generic average weather condition, and uses randomized slight variations to the previous days values to calculate next days values per station.

Currently the generation logic is agnostic to the effect of variables on each other, eg. the value of temprature and altitude combination does not necessarily dictate the pressure value for a given day.

 Eventually, to evolve this system into more realistic prediction engine, "learned" models can be easily plugged in to predict/extrapolate a day's weather data from previous values at the given station.

 Another possible evolution can be the use of lat - long geographies to correlate and affect weather data between stations.


