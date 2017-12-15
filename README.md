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

### Least effort, no installation
You can use a transient docker based spark run time env using the docker image, which sets up an sbt env and downloads and compiles the jar on the container and executes the project resulting in the data being shared using the `./data/` available to the container as `/data/`

to be able to avail this method you need to review and execute `./exec.sh`

NOTE - for non linux runtime env, eg. mac you will need to setup the local ip of the machine in exec.sh script before you execute it.

You can also setup/tweak your input arguments in the script.


## Input
The application uses a list of iarta codes and their geographies from `https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat`

Apart from this, to have plausible starting points for generated weather data, a csv file is supplied in the `/data/average_conditions.csv` directory, this file was prepared from the data available at `https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat`

apart from this the spark application expects two inputs as command line arguments -
1) Time Series Size : Integer - to tune the size of time series data, i.e. how many days of data per station do you want?
2) Output path suffix : String - output path suffix to be applied to existing output directory path `/data/output/`

## Output
Csv data in the format:

is supplied in multiple files in the directory specified in the input

## About the Weather generator
The current operation generates data for all IATA stations from a list of all airports in the world and generated plausible weather data by use of random variation in the weather attributes like temprature pressure etc.

Each station is initialized with avg weather values for a given time series and then next dayus weather is extrapolated with a fixed variation from last day's weather.

Currently the generation logic is entirely dependent on randomized extrapolation values from previous value, independent of the effect of attributes on each other. eg. the value of temprature and altitude combination does not affect the pressure value being calculated.
 Eventually, to evolve this system into more realistic prediction engine, we can easily plug in "learned" models to predict a day's weather data from previous cvalues at the given station.


